package com.akai.findCompanions.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.akai.findCompanions.common.ResultUtils;
import com.akai.findCompanions.enums.UserStatusEnum;
import com.akai.findCompanions.mapper.es.UserDocumentMapper;
import com.akai.findCompanions.model.domain.Es.UserDocument;
import com.akai.findCompanions.model.vo.UserLoginVO;
import com.akai.findCompanions.model.vo.UserRecommendVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.akai.findCompanions.common.ErrorCode;
import com.akai.findCompanions.exception.BusinessException;
import com.akai.findCompanions.model.domain.User;
import com.akai.findCompanions.service.IUserService;
import com.akai.findCompanions.mapper.db.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.conditions.update.LambdaEsUpdateWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.akai.findCompanions.contant.UserConstant.ADMIN_ROLE;
import static com.akai.findCompanions.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements IUserService {
    @Resource
    private UserDocumentMapper userDocumentMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    private static final String URL = "https://akainews.oss-cn-beijing.aliyuncs.com/Find-Compandions/%E2%80%98%E5%AF%BB%E4%BC%B4%E2%80%99%E5%9B%BE%E6%A0%87%20.png";
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "akai";

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param
     * @return 新用户 id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】'；：\"\"'。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已经存在");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        String avatarDefaultUrl = URL;
        String defaultUserName = "寻伴成员";
        user.setUsername(defaultUserName);
        user.setAvatarUrl(avatarDefaultUrl);
        boolean saveResult = this.save(user);

        if (!saveResult) {
            return -1;
        }
        try {
            UserDocument userDocument = new UserDocument();
            BeanUtils.copyProperties(user, userDocument);
            userDocumentMapper.insert(userDocument);
        } catch (Exception e) {
            log.error("用户注册成功，但写入 ES 失败，userId={}", user.getId(), e);
            //TODO 补偿机制
        }
        return user.getId();
    }



    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    @Override
    public UserLoginVO userLogin(String userAccount, String userPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】'；：\"\"'。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }
        // 3. 用户脱敏
        UserLoginVO userLoginVO = new UserLoginVO();
        // 4. 记录用户的登录态
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        userLoginVO.setUsername(user.getUsername());
        userLoginVO.setToken(token);
        userLoginVO.setUserId(user.getId());
        return userLoginVO;
    }

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setTags(originUser.getTags());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

    /**
     * 用户注销
     *
     */
    @Override
    public boolean userLogout(long userId) {
        // 移除登录态
        StpUtil.kickout(userId);
        return true;
    }

    @Override
    public int updateUser(User user) {
        //TODO 只能自己更新自己
        List<String> tagList = new ArrayList<>();
        if(user.getTags() != null){
            String[] tags = user.getTags().split(",");
            tagList = Arrays.stream(tags)
                    .map(String::trim).
                    filter(s -> !s.isEmpty()).
                    collect(Collectors.toList());
            String tagStr = String.join(",", tagList);
            user.setTags(tagStr);
        }
        //检验身份（只有管理员和自己能修改密码
        long userId = StpUtil.getLoginIdAsLong();
        User userLogin = this.getById(userId);
        if(userId != userLogin.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if(oldUser == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        // 打印SQL执行前的用户对象状态
        int result = userMapper.updateById(user);
        return result;
    }

    @Override
    public User getUserLogin(HttpServletRequest request) {
        if(request == null){
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userObj == null){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User) userObj;
    }
    @Override
    public boolean isAdmin() {
        long userId = StpUtil.getLoginIdAsLong();
        // 仅管理员可查询
        User user = this.getById(userId);
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
    //TODO 使用ES实现
    @Override
    public List<UserRecommendVO > recommedUsers(User loginUser) {
        List<String> tags = Arrays.stream(loginUser.getTags().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        if(tags.isEmpty()){
            return Collections.emptyList();
        }
        LambdaEsQueryWrapper<UserDocument> queryWrapper = new LambdaEsQueryWrapper<>();

        queryWrapper.not(w -> w.eq(UserDocument::getId, loginUser.getId()));

        queryWrapper.eq(
                UserDocument::getUserStatus,
                UserStatusEnum.NORMAL.getValue()
        );

        queryWrapper.and(w -> {
            for (String tag : tags) {
                w.should(s -> s.match(UserDocument::getTags, tag));
            }
        });

        queryWrapper.size(5);

        List<UserDocument> docList = userDocumentMapper.selectList(queryWrapper);
        List<UserRecommendVO> result = new ArrayList<>();
        for(UserDocument doc : docList){
            UserRecommendVO vo = new UserRecommendVO();
            BeanUtils.copyProperties(doc,vo);
            result.add(vo);
        }
        return result;
    }
}

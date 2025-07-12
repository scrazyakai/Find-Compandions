package com.akai.findCompandions.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.akai.findCompandions.common.ErrorCode;
import com.akai.findCompandions.enums.StatusEnum;
import com.akai.findCompandions.exception.BusinessException;
import com.akai.findCompandions.mapper.UserTeamMapper;
import com.akai.findCompandions.model.domain.Team;
import com.akai.findCompandions.mapper.TeamMapper;
import com.akai.findCompandions.model.domain.User;
import com.akai.findCompandions.model.domain.UserTeam;
import com.akai.findCompandions.model.dto.TeamQuery;
import com.akai.findCompandions.model.request.DeleteRequest;
import com.akai.findCompandions.model.request.TeamJoinRequest;
import com.akai.findCompandions.model.request.TeamQuitRequest;
import com.akai.findCompandions.model.request.TeamUpdateRequest;
import com.akai.findCompandions.model.vo.TeamUserVO;
import com.akai.findCompandions.model.vo.UserVO;
import com.akai.findCompandions.service.ITeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.akai.findCompandions.service.IUserService;
import com.akai.findCompandions.service.IUserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


import java.util.*;

import static com.akai.findCompandions.enums.StatusEnum.*;

/**
 * <p>
 * 队伍 服务实现类
 * </p>
 *
 * @author 凯哥
 * @since 2025-06-04
 */
@Slf4j
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements ITeamService {
    @Resource
    private IUserTeamService userTeamService;
    @Resource
    private IUserService userService;
    @Autowired
    private UserTeamMapper userTeamMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        final long userId = loginUser.getId();
                // 1. 请求参数是否为空？
        if(team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 是否登录，未登录不允许创建
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 3. 校验信息
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        //    1）队伍人数 > 1 且 <= 20
        if(maxNum < 1 || maxNum > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
        //    2）队伍标题 <= 20
        String name = team.getName();
        if(StringUtils.isBlank(name) || name.length() > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
        //    3）描述 <= 512
        String description = team.getDescription();
        if(StringUtils.isBlank(description) && description.length() > 512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        //    4）status 是否公开 (int)，不传默认为 0（公开）
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        StatusEnum teamStatus = StatusEnum.getEnumByValue(status);
        if(teamStatus == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不满足要求");
        }
        //    5）如果 status 是加密状态，一定要有密码，且密码 <= 32
        if (SECRET.equals(teamStatus)) {
            String password = team.getPassword();
            if(StringUtils.isBlank(password)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "没有设置密码");
            }
            if(password.length() > 32){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
            }
        }
        //    6）超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if(new Date().after(expireTime) ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间 > 当前时间");
        }
        //    7）校验用户最多创建 5 个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        long teamCount = count(queryWrapper);
        if(teamCount >= 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍数量达到上限");

        }
        // 4. 插入队伍信息到队伍表
        //TODO 同时创建100个队伍
        team.setId(null);
        team.setUserId(userId);
        boolean success = this.save(team);
        long teamId = team.getId();
        if(!success){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加队伍失败");
        }
        // 5. 插入用户 => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        success = userTeamService.save(userTeam);
        if(!success ){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加队伍失败");
        }
        return teamId;
    }

    /**
     * 列出队伍列表
     * @param teamQuery 队伍查询条件
     * @param isAdmin 是否是管理员
     * @return 队伍用户VO列表
     */
    @Override
    public List<TeamUserVO> listTeam(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();

        if (teamQuery != null) {
            // ID 查询
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }

            // ID 列表查询
            List<Long> idList = teamQuery.getIdList();
            // Explicitly check for null or empty idList to avoid IN () clause
            if (idList != null && !idList.isEmpty()) {
                queryWrapper.in("id", idList);
            } else {
                // Log when idList is empty to debug why this happens
                log.warn("idList is null or empty for teamQuery: {}", teamQuery);
            }

            // 搜索文本 (对名称和描述进行模糊匹配)
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }

            // 名称模糊查询
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }

            // 描述模糊查询
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }

            // 最大人数相等查询
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }

            // 根据创建人查询
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }

            // 根据状态查询
            Integer status = teamQuery.getStatus();
            StatusEnum statusEnum = StatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                statusEnum = StatusEnum.PUBLIC;
            }
            if (!isAdmin && statusEnum.equals(StatusEnum.PRIVATE)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status", statusEnum.getValue());
        } else {
            // 如果 teamQuery 为空，默认只查询公共队伍
            queryWrapper.eq("status", StatusEnum.PUBLIC.getValue());
        }

        // 不展示已过期的队伍
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));

        // Debug: Log the generated SQL query
        log.info("Generated QueryWrapper: {}", queryWrapper.getSqlSegment());

        // Execute query
        List<Team> teamList = this.list(queryWrapper);

        // 如果查询结果为空，返回空列表
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }

        // 关联查询创建人用户信息并进行脱敏
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        for (Team team : teamList) {
            Long creatorUserId = team.getUserId();
            if (creatorUserId == null) {
                continue;
            }
            User user = userService.getById(creatorUserId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);

            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public Boolean updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if(teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(request == null){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User loginUser = userService.getUserLogin(request);
        Long id = teamUpdateRequest.getId();
        if(id == null || id <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        if(oldTeam == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"队伍不存在");
        }
        long adminId = oldTeam.getUserId();
        if(!userService.isAdmin(request) && adminId != loginUser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if(SECRET.equals(StatusEnum.getEnumByValue(teamUpdateRequest.getStatus()))){
            if(StringUtils.isBlank(teamUpdateRequest.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密队伍需要设置密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,updateTeam);
        return this.updateById(updateTeam);
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if(teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = loginUser.getId();
        //用户最多加入5个队伍
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        long count = userTeamService.count(queryWrapper);
        if(count >= 5){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"您最多只能加入五个队伍");
        }
        //队伍必须存在
        Long teamId = teamJoinRequest.getTeamId();
        Team team = this.getById(teamId);
        if(team == null){
            throw  new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        //队伍未满
        Integer maxNum = team.getMaxNum();
        QueryWrapper<UserTeam> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("teamId",teamId);
        long teamMemberCount = userTeamService.count(teamQueryWrapper);
        if(teamMemberCount >= maxNum){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"队伍已满");
        }
        //队伍未过期
        Date expireTime = team.getExpireTime();
        if(expireTime.before(new Date())){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍已过期");
        }
        //不能加入已经加入的队伍
        QueryWrapper<UserTeam> hasTeamQuery = new QueryWrapper<>();
        hasTeamQuery.eq("userId",userId).eq("teamId",teamId);
        long hasTeamCount = userTeamService.count(hasTeamQuery);
        if( hasTeamCount > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"您已经加入该队伍");
        }
        //不能加入私人队伍
        Integer status = team.getStatus();
        StatusEnum statusEnum = StatusEnum.getEnumByValue(status);
        if(PRIVATE.equals(statusEnum)){
            throw new BusinessException(ErrorCode.NO_AUTH,"该队伍为私人队伍");
        }
        //如果加入队伍是加密的必须密码匹配才能加入
        String password = teamJoinRequest.getPassword();
        if(statusEnum.equals(SECRET) ){
            if(password == null|| !password.equals(team.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密钥错误");
            }
        }
        //修改队伍信息，补充人数

        //新增用户-队伍关联信息
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean success = userTeamService.save(userTeam);
        if(!success ){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加队伍失败");
        }
        return success;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if(teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        if(teamId == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"您没有加入该队伍");
        }
        //不能退出没有加入的队伍
        long userId = loginUser.getId();
        Team team = getById(teamId);
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setTeamId(teamId);
        queryUserTeam.setUserId(userId);
        QueryWrapper<UserTeam> hasJoinWrapper = new QueryWrapper<>();
        hasJoinWrapper.eq("teamId",teamId).eq("userId",userId);
        long count = userTeamService.count(hasJoinWrapper);
        if(count == 0){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"您未加入该队伍");
        }
        //查看队伍人数和退出的是否是队长
        QueryWrapper<UserTeam> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("teamId",teamId);
        long teamMemberCount = userTeamService.count(teamQueryWrapper);
        if(teamMemberCount == 1){
            this.removeById(teamId);
        }else{
            if(userId == team.getUserId()){
                // 把队伍转移给最早加入的用户
                // 1. 查询已加入队伍的所有用户和加入时间
                QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("teamId",teamId).last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
                if(userTeamList.isEmpty() || userTeamList.size() <= 1){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextCaptainUserId = nextUserTeam.getUserId();
                Team updateTeam = new Team();
                updateTeam.setUserId(nextCaptainUserId);
                updateTeam.setId(teamId);
                boolean success = this.updateById(updateTeam);
                if(!success){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新队长失败");
                }
            }
        }
        //删除成员队伍信息
        return userTeamService.remove(hasJoinWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(DeleteRequest deleteRequest, User loginUser) {
        if(deleteRequest == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        long id = deleteRequest.getId();
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long userId = loginUser.getId();
        Team deletedTeam = getById(id);
        if(deletedTeam == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        if(userId != deletedTeam.getUserId()){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        //删除队伍关联成员信息
        QueryWrapper<UserTeam> deleteQueryWrapper = new QueryWrapper<>();
        deleteQueryWrapper.eq("teamId",id);
        boolean success = userTeamService.remove(deleteQueryWrapper);
        if(!success){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"解散队伍失败");
        }
        //删除队伍
        return removeById(id);
    }


}

package com.yupi.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.usercenter.common.ErrorCode;
import com.yupi.usercenter.enums.StatusEnum;
import com.yupi.usercenter.exception.BusinessException;
import com.yupi.usercenter.model.domain.Team;
import com.yupi.usercenter.mapper.TeamMapper;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.model.domain.UserTeam;
import com.yupi.usercenter.service.ITeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.usercenter.service.IUserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static com.yupi.usercenter.enums.StatusEnum.SECRET;

/**
 * <p>
 * 队伍 服务实现类
 * </p>
 *
 * @author 凯哥
 * @since 2025-06-04
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements ITeamService {
    @Resource
    private IUserTeamService userTeamService;
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
}

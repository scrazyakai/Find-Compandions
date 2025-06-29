package com.yupi.usercenter.service;

import com.yupi.usercenter.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.usercenter.model.domain.User;

/**
 * <p>
 * 队伍 服务类
 * </p>
 *
 * @author 凯哥
 * @since 2025-06-04
 */
public interface ITeamService extends IService<Team> {
    long addTeam(Team team, User loginUser);
}

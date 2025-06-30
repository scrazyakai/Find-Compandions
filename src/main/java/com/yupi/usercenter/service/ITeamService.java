package com.yupi.usercenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.usercenter.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.model.dto.TeamQuery;
import com.yupi.usercenter.model.request.TeamJoinRequest;
import com.yupi.usercenter.model.request.TeamUpdateRequest;
import com.yupi.usercenter.model.vo.TeamUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    /**
     * 搜索队伍
     * @param teamQuery
     * @return
     */
    List<TeamUserVO> teamList(TeamQuery teamQuery,boolean isAdmin);

    Boolean updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request);

    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);
}

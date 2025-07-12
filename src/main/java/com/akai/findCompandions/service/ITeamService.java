package com.akai.findCompandions.service;

import com.akai.findCompandions.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.akai.findCompandions.model.domain.User;
import com.akai.findCompandions.model.dto.TeamQuery;
import com.akai.findCompandions.model.request.DeleteRequest;
import com.akai.findCompandions.model.request.TeamJoinRequest;
import com.akai.findCompandions.model.request.TeamQuitRequest;
import com.akai.findCompandions.model.request.TeamUpdateRequest;
import com.akai.findCompandions.model.vo.TeamUserVO;

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
    /**
     * 创建队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍
     * @param teamQuery
     * @return
     */
    List<TeamUserVO> listTeam(TeamQuery teamQuery,boolean isAdmin);

    /**
     * 更新队伍信息
     * @param teamUpdateRequest
     * @param request
     * @return
     */
    Boolean updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request);

    /**
     * 用户加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 用户退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 删除队伍
     * @param deleteRequest
     * @param loginUser
     * @return
     */
    boolean deleteTeam(DeleteRequest deleteRequest, User loginUser);
}

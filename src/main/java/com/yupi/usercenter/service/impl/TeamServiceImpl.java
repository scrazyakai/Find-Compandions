package com.yupi.usercenter.service.impl;

import com.yupi.usercenter.model.domain.Team;
import com.yupi.usercenter.mapper.TeamMapper;
import com.yupi.usercenter.service.ITeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}

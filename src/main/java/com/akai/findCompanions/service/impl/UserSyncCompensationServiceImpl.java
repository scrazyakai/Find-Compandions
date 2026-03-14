package com.akai.findCompanions.service.impl;

import com.akai.findCompanions.mapper.db.UserSyncCompensationMapper;
import com.akai.findCompanions.model.domain.UserSyncCompensation;
import com.akai.findCompanions.service.IUserSyncCompensationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserSyncCompensationServiceImpl extends ServiceImpl<UserSyncCompensationMapper, UserSyncCompensation>
        implements IUserSyncCompensationService {
}

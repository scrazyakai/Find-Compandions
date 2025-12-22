package com.akai.findCompanions.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.akai.findCompanions.model.dto.ChatGroupDTO;
import com.akai.findCompanions.mapper.db.ChatGroupMapper;
import com.akai.findCompanions.service.IChatGroupService;
import org.springframework.stereotype.Service;

@Service
public class ChatGroupServiceImpl extends ServiceImpl<ChatGroupMapper, ChatGroupDTO> implements IChatGroupService {
}

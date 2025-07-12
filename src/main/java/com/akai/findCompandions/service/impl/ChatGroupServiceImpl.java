package com.akai.findCompandions.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.akai.findCompandions.model.dto.ChatGroupDTO;
import com.akai.findCompandions.mapper.ChatGroupMapper;
import com.akai.findCompandions.service.IChatGroupService;
import org.springframework.stereotype.Service;

@Service
public class ChatGroupServiceImpl extends ServiceImpl<ChatGroupMapper, ChatGroupDTO> implements IChatGroupService {
}

package com.yupi.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.usercenter.model.dto.ChatGroupDTO;
import com.yupi.usercenter.mapper.ChatGroupMapper;
import com.yupi.usercenter.service.IChatGroupService;
import org.springframework.stereotype.Service;

@Service
public class ChatGroupServiceImpl extends ServiceImpl<ChatGroupMapper, ChatGroupDTO> implements IChatGroupService {
}

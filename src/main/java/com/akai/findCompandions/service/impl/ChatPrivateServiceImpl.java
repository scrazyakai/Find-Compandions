package com.akai.findCompandions.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.akai.findCompandions.common.ErrorCode;
import com.akai.findCompandions.exception.BusinessException;
import com.akai.findCompandions.entity.ChatPrivate;
import com.akai.findCompandions.mapper.ChatPrivateMapper;
import com.akai.findCompandions.model.domain.User;
import com.akai.findCompandions.model.vo.ChatPrivateVO;
import com.akai.findCompandions.service.IChatPrivateService;
import com.akai.findCompandions.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
@Service
public class ChatPrivateServiceImpl extends ServiceImpl<ChatPrivateMapper, ChatPrivate> implements IChatPrivateService {
@Resource
    IUserService userService;
    @Override
    public List<ChatPrivateVO> history(long fromUserId, long toUserId) {
        if(fromUserId <= 0 || toUserId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<ChatPrivate> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(qw->qw.eq("fromUserId",fromUserId).eq("toUserId",toUserId)
                .or().eq("fromUserId",toUserId).eq("toUserId",fromUserId)).orderByAsc("sendTime");
        List<ChatPrivate> chatPrivates = this.list(queryWrapper);
        List<ChatPrivateVO> chatPrivateVOS = new ArrayList<>();
        for(ChatPrivate msg : chatPrivates){
            ChatPrivateVO vo = new ChatPrivateVO();
            Long msgFromUserId = msg.getFromUserId();
            Long msgToUserId = msg.getToUserId();
            BeanUtils.copyProperties(msg,vo);
            User fromUser = userService.getById(msgFromUserId);
            if(fromUser != null){
                vo.setFromUsername(fromUser.getUsername());
                vo.setFromAvatarUrl(fromUser.getAvatarUrl());
            }
            User toUser = userService.getById(msgToUserId);
            if(toUser != null){
                vo.setToUsername(toUser.getUsername());
                vo.setToAvatarUrl(toUser.getAvatarUrl());
            }
            chatPrivateVOS.add(vo);
        }
       return chatPrivateVOS;
    }
}

package com.yupi.usercenter.controller;

import com.yupi.usercenter.common.BaseResponse;
import com.yupi.usercenter.common.ErrorCode;
import com.yupi.usercenter.common.ResultUtils;
import com.yupi.usercenter.entity.ChatPrivate;
import com.yupi.usercenter.model.dto.ChatGroupDTO;
import com.yupi.usercenter.exception.BusinessException;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.model.dto.ChatPrivateDTO;
import com.yupi.usercenter.model.dto.Message;
import com.yupi.usercenter.model.vo.ChatGroupVO;
import com.yupi.usercenter.model.vo.ChatPrivateVO;
import com.yupi.usercenter.service.IChatGroupService;
import com.yupi.usercenter.service.IChatPrivateService;
import com.yupi.usercenter.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = {"http://localhost:3000"})
public class ChatController {
    @Resource
    IUserService userService;
    @Resource
    IChatGroupService chatGroupService;
    @Resource
    IChatPrivateService chatPrivateService;
    @PostMapping("/private/send")
    public BaseResponse<Boolean> sendPrivate(@RequestBody ChatPrivateDTO chatPrivateDTO){
        if(chatPrivateDTO == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        chatPrivateDTO.setSendTime(new Date());
        ChatPrivate chatPrivate = new ChatPrivate();
        BeanUtils.copyProperties(chatPrivateDTO, chatPrivate);
        boolean success = chatPrivateService.save(chatPrivate);
        return ResultUtils.success(success);
    }
    @GetMapping("/private/history")
    public BaseResponse<List<ChatPrivateVO>> getHistory(Message message){
        if(message == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        long fromUserId = message.getFromUserId();
        long toUserId = message.getToUserId();
        List<ChatPrivateVO> messageList = chatPrivateService.history(fromUserId,toUserId);
        return ResultUtils.success(messageList);
    }
    @PostMapping("/group/send")
    public BaseResponse<Boolean> sendPrivate(@RequestBody ChatGroupDTO chatGroupDTO){
        if(chatGroupDTO == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        chatGroupDTO.setSendTime(new Date());
        return ResultUtils.success(chatGroupService.save(chatGroupDTO));
    }
    @GetMapping("/group/history")
    public BaseResponse<List<ChatGroupVO>> getGroupHistory(){
        List<ChatGroupDTO> messageList = chatGroupService.list();
        List<ChatGroupVO> result = new ArrayList<>();
        for(ChatGroupDTO msg : messageList){
            long fromUserId = msg.getUserId();
            ChatGroupVO vo = new ChatGroupVO();
            BeanUtils.copyProperties(msg,vo);
            User fromUser = userService.getById(fromUserId);
            if(fromUser == null){
                throw new BusinessException(ErrorCode.NULL_ERROR);
            }
            vo.setAvatarUrl(fromUser.getAvatarUrl());
            vo.setUsername(fromUser.getUsername());
            result.add(vo);
        }
        return ResultUtils.success(result);
    }

}

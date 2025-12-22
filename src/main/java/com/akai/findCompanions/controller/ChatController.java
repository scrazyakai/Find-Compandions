package com.akai.findCompanions.controller;

import com.akai.findCompanions.common.BaseResponse;
import com.akai.findCompanions.common.ErrorCode;
import com.akai.findCompanions.common.ResultUtils;
import com.akai.findCompanions.model.domain.ChatPrivate;
import com.akai.findCompanions.model.dto.ChatGroupDTO;
import com.akai.findCompanions.exception.BusinessException;
import com.akai.findCompanions.model.domain.User;
import com.akai.findCompanions.model.dto.ChatPrivateDTO;
import com.akai.findCompanions.model.dto.Message;
import com.akai.findCompanions.model.vo.ChatGroupVO;
import com.akai.findCompanions.model.vo.ChatPrivateVO;
import com.akai.findCompanions.service.IChatGroupService;
import com.akai.findCompanions.service.IChatPrivateService;
import com.akai.findCompanions.service.IUserService;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
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
        boolean containsSensitive =  SensitiveWordHelper.contains(chatPrivate.getContent());
        if(containsSensitive){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"内容违规");
        }

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
        boolean containsSensitive =  SensitiveWordHelper.contains(chatGroupDTO.getContent());
        if(containsSensitive){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"内容违规");
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

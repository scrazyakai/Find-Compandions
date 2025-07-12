package com.yupi.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.usercenter.entity.ChatPrivate;
import com.yupi.usercenter.model.dto.ChatPrivateDTO;
import com.yupi.usercenter.model.vo.ChatPrivateVO;

import java.util.List;


public interface IChatPrivateService extends IService<ChatPrivate> {

    List<ChatPrivateVO> history(long fromUserId, long toUserId);
}
package com.akai.findCompandions.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.akai.findCompandions.entity.ChatPrivate;
import com.akai.findCompandions.model.vo.ChatPrivateVO;

import java.util.List;


public interface IChatPrivateService extends IService<ChatPrivate> {

    List<ChatPrivateVO> history(long fromUserId, long toUserId);
}
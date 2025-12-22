package com.akai.findCompanions.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.akai.findCompanions.model.domain.ChatPrivate;
import com.akai.findCompanions.model.vo.ChatPrivateVO;

import java.util.List;


public interface IChatPrivateService extends IService<ChatPrivate> {

    List<ChatPrivateVO> history(long fromUserId, long toUserId);
}
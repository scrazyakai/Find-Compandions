package com.yuyuan.usercenter.service.impl;

import com.yuyuan.usercenter.entity.User;
import com.yuyuan.usercenter.mapper.UserMapper;
import com.yuyuan.usercenter.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author 凯哥
 * @since 2025-06-04
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}

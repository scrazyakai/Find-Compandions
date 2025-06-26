package com.yupi.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.usercenter.exception.BusinessException;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.service.IUserService;
import com.yupi.usercenter.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 定时预热缓存任务
 */
@Component
@Slf4j
public class PreJob {
    @Resource
    private final IUserService userService;
    @Resource
    private final RedisTemplate<String, Object> redisTemplate;
    //每天23:59:00缓存预热一次
    List<Long> mainUserList = Arrays.asList(1L);

    public PreJob(IUserService userService, RedisTemplate<String, Object> redisTemplate) {
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(cron = "0 59 23 * * ?" )

    public void doCacheRecommendUser(){
        for(Long userId : mainUserList){
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            Page<User> userPage = userService.page(new Page<>(1,20),queryWrapper);
            String redisKey = String.format("seacher-partner:user:recommend:%s",userId);
            ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
            try {
                valueOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.error("Redis set key error");
            }

        }
    }

}

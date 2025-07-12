package com.akai.findCompandions.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.akai.findCompandions.model.domain.User;
import com.akai.findCompandions.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
    private  IUserService userService;
    @Resource
    private  RedisTemplate<String, Object> redisTemplate;
    @Resource
    private  RedissonClient redissonClient;
    //每天23:59:00缓存预热一次
    List<Long> mainUserList = Arrays.asList(1L);

    @Scheduled(cron = "0 0 4 * * ?" )

    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("yupao:Prejob:docahche:lock");

        try {
            if((lock.tryLock(0,30000L,TimeUnit.MILLISECONDS))){
                for(Long userId : mainUserList){
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1,20),queryWrapper);
                    String redisKey = String.format("seacher-partner:user:recommend:%s",userId);
                    ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
                    //写缓存
                    try {
                        valueOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("Redis set key error");
                    }
                }
            }

        } catch (Exception e) {
            log.error("doCacheRecommendUser error" ,e);
        }finally {
            //判断是否是当前线程的锁
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }

}

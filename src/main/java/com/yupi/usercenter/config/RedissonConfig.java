package com.yupi.usercenter.config;

import io.lettuce.core.RedisClient;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redisClient(){
        //1.创建配置
        Config config = new Config();
        String redisAddress = "redis://127.0.0.1:6379";
        config.useSingleServer().setAddress(redisAddress).setDatabase(1);
        //2.创建实例
        return Redisson.create(config);
    }
}

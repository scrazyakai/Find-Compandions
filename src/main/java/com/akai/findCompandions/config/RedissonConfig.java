package com.akai.findCompandions.config;

import io.lettuce.core.RedisClient;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${redis.host:localhost}")
    private String host;

    @Value("${redis.port:6380}")
    private int port;

    @Value("${redis.database:1}")
    private int database;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 直接使用 Spring 配置的 host 和 port
        String redisAddress = "redis://" + host + ":" + port;
        config.useSingleServer().setAddress(redisAddress).setDatabase(database);
        return Redisson.create(config);
    }
}

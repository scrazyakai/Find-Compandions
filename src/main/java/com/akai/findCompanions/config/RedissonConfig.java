package com.akai.findCompanions.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${redis.host:127.0.0.1}")
    private String host;

    @Value("${redis.port:6379}")
    private int port;

    @Value("${redis.database:0}")
    private int database;
    //@Value("${redis.username}")
    //private String username;
    //@Value("${redis.password}")
    //private String password;
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 直接使用 Spring 配置的 host 和 port
        String redisAddress = "redis://" + host + ":" + port;
        config.useSingleServer().setAddress(redisAddress).setDatabase(database);//.setUsername(username).setPassword(password).setConnectionPoolSize(10).setConnectionMinimumIdleSize(5);
        return Redisson.create(config);
    }
}

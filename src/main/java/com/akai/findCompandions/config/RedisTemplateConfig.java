package com.akai.findCompandions.config;

import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisTemplateConfig {

    @Autowired
    private RedissonClient redissonClient;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        Config config = redissonClient.getConfig();
        SingleServerConfig singleServerConfig = (SingleServerConfig) config.useSingleServer();

        // 从 Redisson 配置中提取完整信息
        String[] addressParts = singleServerConfig.getAddress().toString().split(":");
        String host = addressParts[1].replace("//", "");
        int port = Integer.parseInt(addressParts[2]);
        String password = singleServerConfig.getPassword();
        int database = singleServerConfig.getDatabase();

        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(host, port);
        redisConfig.setPassword(RedisPassword.of(password));
        redisConfig.setDatabase(database);
        redisConfig.setUsername("default"); // Redis 6+ 需要

        return new LettuceConnectionFactory(redisConfig);
    }

    // 保持 RedisTemplate 配置不变
}
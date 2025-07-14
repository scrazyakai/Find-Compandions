package com.akai.findCompandions.config;

import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisTemplateConfig {

    @Autowired
    private RedissonClient redissonClient; // 注入已配置的 RedissonClient

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // 使用 Redisson 的连接工厂（需转换）
        Config config = redissonClient.getConfig();
        SingleServerConfig singleServerConfig = (SingleServerConfig) config.useSingleServer();
        String[] addressParts = singleServerConfig.getAddress().toString().split(":");
        String host = addressParts[1].replace("//", "");
        int port = Integer.parseInt(addressParts[2]);

        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(host, port);
        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(RedisSerializer.string());
        return template;
    }
}
package com.akai.findCompanions.sync.mq;

import com.akai.findCompanions.config.properties.UserSyncMqProperties;
import com.akai.findCompanions.model.dto.sync.UserSyncMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@ConditionalOnProperty(prefix = "sync.mq", name = "enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class UserSyncProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private UserSyncMqProperties userSyncMqProperties;

    public void send(UserSyncMessage message) {
        if (!userSyncMqProperties.isEnabled()) {
            return;
        }
        rocketMQTemplate.convertAndSend(userSyncMqProperties.getTopic(), message);
        log.info("发送用户同步消息成功, op={}, userId={}", message.getOperationType(), message.getId());
    }
}

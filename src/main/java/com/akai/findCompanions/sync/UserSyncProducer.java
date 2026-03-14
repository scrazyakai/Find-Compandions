package com.akai.findCompanions.sync;

import com.akai.findCompanions.config.properties.UserSyncMqProperties;
import com.akai.findCompanions.model.dto.sync.UserSyncMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class UserSyncProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private UserSyncMqProperties userSyncMqProperties;

    public void send(UserSyncMessage message) {
        if (!userSyncMqProperties.isEnabled() || message == null) {
            return;
        }
        message.setRetryTimes(message.getRetryTimes() == null ? 0 : message.getRetryTimes());
        Message<UserSyncMessage> mqMessage = MessageBuilder.withPayload(message).build();
        rocketMQTemplate.syncSend(destination(), mqMessage, userSyncMqProperties.getSendTimeoutMs());
    }

    public void sendRetry(UserSyncMessage message) {
        if (!userSyncMqProperties.isEnabled() || message == null) {
            return;
        }
        Message<UserSyncMessage> retryMessage = MessageBuilder.withPayload(message).build();
        rocketMQTemplate.syncSend(destination(), retryMessage, userSyncMqProperties.getSendTimeoutMs(),
                userSyncMqProperties.getRetryDelayLevel());
    }

    private String destination() {
        String topic = userSyncMqProperties.getTopic();
        if (StringUtils.isBlank(topic)) {
            throw new IllegalStateException("sync.mq.topic is blank");
        }
        return topic;
    }
}

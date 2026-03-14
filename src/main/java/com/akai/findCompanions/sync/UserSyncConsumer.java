package com.akai.findCompanions.sync;

import com.akai.findCompanions.config.properties.UserSyncMqProperties;
import com.akai.findCompanions.model.domain.UserSyncCompensation;
import com.akai.findCompanions.model.dto.sync.UserSyncMessage;
import com.akai.findCompanions.service.IUserSyncCompensationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
@ConditionalOnExpression("T(org.springframework.util.StringUtils).hasText('${rocketmq.name-server:}')")
@RocketMQMessageListener(
        topic = "${sync.mq.topic:user_sync_topic}",
        consumerGroup = "${sync.mq.consumerGroup:user_sync_consumer_group}",
        nameServer = "${rocketmq.name-server:}",
        consumeMode = ConsumeMode.CONCURRENTLY
)
public class UserSyncConsumer implements RocketMQListener<UserSyncMessage> {

    @Resource
    private UserEsSyncService userEsSyncService;

    @Resource
    private UserSyncProducer userSyncProducer;

    @Resource
    private UserSyncMqProperties userSyncMqProperties;

    @Resource
    private IUserSyncCompensationService userSyncCompensationService;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(UserSyncMessage message) {
        if (!userSyncMqProperties.isEnabled() || message == null) {
            return;
        }
        try {
            userEsSyncService.sync(message);
        } catch (Exception ex) {
            int currentRetry = message.getRetryTimes() == null ? 0 : message.getRetryTimes();
            if (currentRetry < userSyncMqProperties.getMaxRetryTimes()) {
                message.setRetryTimes(currentRetry + 1);
                userSyncProducer.sendRetry(message);
                log.warn("user sync retry scheduled, userId={}, retryTimes={}",
                        message.getId(), message.getRetryTimes(), ex);
                return;
            }
            saveCompensation(message, ex);
        }
    }

    private void saveCompensation(UserSyncMessage message, Exception ex) {
        UserSyncCompensation compensation = new UserSyncCompensation();
        compensation.setUserId(message.getId());
        compensation.setOperationType(message.getOperationType());
        compensation.setRetryTimes(message.getRetryTimes() == null ? 0 : message.getRetryTimes());
        compensation.setMaxRetryTimes(userSyncMqProperties.getMaxRetryTimes());
        compensation.setStatus(0);
        compensation.setErrorMessage(shortError(ex));
        compensation.setPayload(toJson(message));
        compensation.setCreateTime(new Date());
        compensation.setUpdateTime(new Date());
        userSyncCompensationService.save(compensation);
        log.error("user sync exceeded max retries, saved compensation. userId={}, operation={}",
                message.getId(), message.getOperationType(), ex);
    }

    private String toJson(UserSyncMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            return "{\"id\":" + message.getId() + "}";
        }
    }

    private String shortError(Exception ex) {
        String msg = ex.getMessage();
        if (msg == null) {
            return ex.getClass().getSimpleName();
        }
        return msg.length() > 1000 ? msg.substring(0, 1000) : msg;
    }
}

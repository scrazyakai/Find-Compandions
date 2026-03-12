package com.akai.findCompanions.sync.consumer;

import com.akai.findCompanions.mapper.es.UserDocumentMapper;
import com.akai.findCompanions.model.domain.Es.UserDocument;
import com.akai.findCompanions.model.dto.sync.UserSyncMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(prefix = "sync.mq", name = "enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
@RocketMQMessageListener(
        topic = "${sync.mq.topic:user_sync_topic}",
        consumerGroup = "${sync.mq.consumer-group:user_sync_consumer_group}")
public class UserSyncConsumer implements RocketMQListener<UserSyncMessage> {

    @Resource
    private UserDocumentMapper userDocumentMapper;

    @Override
    public void onMessage(UserSyncMessage message) {
        if (message == null || message.getId() == null) {
            return;
        }
        try {
            boolean needDelete = "DELETE".equalsIgnoreCase(message.getOperationType())
                    || Integer.valueOf(1).equals(message.getIsDelete());
            if (needDelete) {
                userDocumentMapper.deleteById(message.getId());
                log.info("消费用户同步消息: 删除ES文档, userId={}", message.getId());
                return;
            }
            UserDocument doc = toDocument(message);
            int updated = userDocumentMapper.updateById(doc);
            if (updated <= 0) {
                userDocumentMapper.insert(doc);
            }
            log.info("消费用户同步消息: 更新ES文档, op={}, userId={}", message.getOperationType(), message.getId());
        } catch (Exception e) {
            log.error("消费用户同步消息失败, op={}, userId={}", message.getOperationType(), message.getId(), e);
            throw e;
        }
    }

    private UserDocument toDocument(UserSyncMessage message) {
        UserDocument doc = new UserDocument();
        doc.setId(message.getId());
        doc.setUsername(message.getUsername());
        doc.setUserAccount(message.getUserAccount());
        doc.setAvatarUrl(message.getAvatarUrl());
        doc.setUserStatus(message.getUserStatus());
        doc.setProfile(message.getProfile());
        doc.setTags(parseTags(message.getTags()));
        return doc;
    }

    private List<String> parseTags(String tags) {
        if (StringUtils.isBlank(tags)) {
            return Collections.emptyList();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }
}

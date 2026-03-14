package com.akai.findCompanions.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sync.mq")
public class UserSyncMqProperties {

    private boolean enabled = true;

    private String topic = "user_sync_topic";

    private String consumerGroup = "user_sync_consumer_group";

    /**
     * Maximum retry times when ES sync fails.
     */
    private int maxRetryTimes = 3;

    /**
     * RocketMQ delay level for retry message.
     */
    private int retryDelayLevel = 3;

    /**
     * Producer send timeout in milliseconds.
     */
    private long sendTimeoutMs = 10000L;
}

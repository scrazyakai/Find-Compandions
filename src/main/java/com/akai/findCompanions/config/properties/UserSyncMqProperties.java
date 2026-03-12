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
}

package com.akai.findCompanions.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sync.full")
public class UserSyncFullProperties {

    /** 是否在应用启动时执行一次全量同步 */
    private boolean enabled = false;

    /** 分页批次大小 */
    private long batchSize = 500;
}

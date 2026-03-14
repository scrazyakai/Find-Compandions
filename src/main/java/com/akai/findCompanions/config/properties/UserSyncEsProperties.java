package com.akai.findCompanions.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sync.es")
public class UserSyncEsProperties {

    private String userIndex = "user_index";
}

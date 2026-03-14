package com.akai.findCompanions.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "sync.canal")
public class CanalProperties {

    private boolean enabled = false;

    private String host = "127.0.0.1";

    private Integer port = 11111;

    private String destination = "example";

    private String username = "";

    private String password = "";

    /** 数据库名称 */
    private String database = "find_companions";

    /** 要同步的表名称 */
    private String table = "user";

    private Integer batchSize = 100;

    private Long pollIntervalMs = 1000L;
}

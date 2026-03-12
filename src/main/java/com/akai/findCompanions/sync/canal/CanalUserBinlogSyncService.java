package com.akai.findCompanions.sync.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.akai.findCompanions.config.properties.CanalProperties;
import com.akai.findCompanions.model.dto.sync.UserSyncMessage;
import com.akai.findCompanions.sync.mq.UserSyncProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class CanalUserBinlogSyncService {

    @Resource
    private CanalProperties canalProperties;

    @Resource
    private UserSyncProducer userSyncProducer;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private volatile boolean running = true;

    @javax.annotation.PostConstruct
    public void init() {
        if (!canalProperties.isEnabled()) {
            log.info("Canal 增量同步未启用");
            return;
        }
        executorService.submit(this::syncLoop);
        log.info("Canal 增量同步线程已启动");
    }

    private void syncLoop() {
        CanalConnector connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress(canalProperties.getHost(), canalProperties.getPort()),
                canalProperties.getDestination(),
                canalProperties.getUsername(),
                canalProperties.getPassword());
        try {
            connector.connect();
            connector.subscribe(canalProperties.getDatabase() + "." + canalProperties.getTable());
            connector.rollback();

            while (running) {
                Message message = connector.getWithoutAck(canalProperties.getBatchSize());
                long batchId = message.getId();
                List<CanalEntry.Entry> entries = message.getEntries();
                if (batchId == -1 || entries == null || entries.isEmpty()) {
                    sleepQuietly(canalProperties.getPollIntervalMs());
                    continue;
                }
                try {
                    parseAndSend(entries);
                    connector.ack(batchId);
                } catch (Exception e) {
                    log.error("Canal 解析并发送消息失败, batchId={}", batchId, e);
                    connector.rollback(batchId);
                }
            }
        } catch (Exception e) {
            log.error("Canal 同步线程异常退出", e);
        } finally {
            connector.disconnect();
        }
    }

    private void parseAndSend(List<CanalEntry.Entry> entries) throws Exception {
        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() != CanalEntry.EntryType.ROWDATA) {
                continue;
            }
            CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            CanalEntry.EventType eventType = rowChange.getEventType();

            if (!(eventType == CanalEntry.EventType.INSERT
                    || eventType == CanalEntry.EventType.UPDATE
                    || eventType == CanalEntry.EventType.DELETE)) {
                continue;
            }

            CanalEntry.Header header = entry.getHeader();
            if (!canalProperties.getDatabase().equals(header.getSchemaName())
                    || !canalProperties.getTable().equals(header.getTableName())) {
                continue;
            }

            for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                List<CanalEntry.Column> columns = eventType == CanalEntry.EventType.DELETE
                        ? rowData.getBeforeColumnsList() : rowData.getAfterColumnsList();
                UserSyncMessage syncMessage = buildSyncMessage(columns, eventType);
                if (syncMessage.getId() == null) {
                    continue;
                }
                userSyncProducer.send(syncMessage);
            }
        }
    }

    private UserSyncMessage buildSyncMessage(List<CanalEntry.Column> columns, CanalEntry.EventType eventType) {
        UserSyncMessage message = new UserSyncMessage();
        message.setOperationType(eventType.name());
        message.setEventTime(new Date());
        for (CanalEntry.Column column : columns) {
            String name = column.getName();
            String value = column.getValue();
            if ("id".equals(name) && value != null && !value.isEmpty()) {
                message.setId(Long.valueOf(value));
            } else if ("username".equals(name)) {
                message.setUsername(value);
            } else if ("userAccount".equals(name)) {
                message.setUserAccount(value);
            } else if ("avatarUrl".equals(name)) {
                message.setAvatarUrl(value);
            } else if ("userStatus".equals(name) && value != null && !value.isEmpty()) {
                message.setUserStatus(Integer.valueOf(value));
            } else if ("tags".equals(name)) {
                message.setTags(value);
            } else if ("profile".equals(name)) {
                message.setProfile(value);
            } else if ("isDelete".equals(name) && value != null && !value.isEmpty()) {
                message.setIsDelete(Integer.valueOf(value));
            }
        }
        return message;
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    @PreDestroy
    public void destroy() {
        running = false;
        executorService.shutdownNow();
    }
}

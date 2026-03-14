package com.akai.findCompanions.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.akai.findCompanions.model.dto.sync.UserSyncMessage;
import com.akai.findCompanions.sync.UserSyncProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CanalSchedule implements Runnable {

    @Resource
    private CanalProperties canalProperties;

    @Resource
    private CanalConnector canalConnector;

    @Resource
    private UserSyncProducer userSyncProducer;

    @Override
    @Scheduled(fixedDelay = 100)
    public void run() {
        long batchId = -1;
        try {
            Message message = canalConnector.getWithoutAck(canalProperties.getBatchSize());
            batchId = message.getId();
            int size = message.getEntries().size();
            if (batchId == -1 || size == 0) {
                TimeUnit.SECONDS.sleep(1);
            } else {
                publishUserSyncEvents(message.getEntries());
            }
            canalConnector.ack(batchId);
        } catch (Exception e) {
            log.error("consume canal message error", e);
            canalConnector.rollback(batchId);
        }
    }

    private void publishUserSyncEvents(List<CanalEntry.Entry> entries) {
        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN
                    || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }
            if (!"user".equalsIgnoreCase(entry.getHeader().getTableName())) {
                continue;
            }
            CanalEntry.RowChange rowChange;
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                log.error("parse canal row change failed", e);
                continue;
            }
            for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                UserSyncMessage syncMessage = buildUserSyncMessage(rowData, rowChange.getEventType(),
                        entry.getHeader().getExecuteTime());
                if (syncMessage == null || syncMessage.getId() == null) {
                    continue;
                }
                userSyncProducer.send(syncMessage);
                log.info("published user sync message, userId={}, op={}",
                        syncMessage.getId(), syncMessage.getOperationType());
            }
        }
    }

    private UserSyncMessage buildUserSyncMessage(CanalEntry.RowData rowData,
                                                 CanalEntry.EventType eventType,
                                                 long executeTime) {
        if (!(eventType == CanalEntry.EventType.INSERT
                || eventType == CanalEntry.EventType.UPDATE
                || eventType == CanalEntry.EventType.DELETE)) {
            return null;
        }
        List<CanalEntry.Column> columns =
                eventType == CanalEntry.EventType.DELETE ? rowData.getBeforeColumnsList() : rowData.getAfterColumnsList();

        UserSyncMessage message = new UserSyncMessage();
        message.setOperationType(eventType.name());
        message.setRetryTimes(0);
        message.setEventTime(new Date(executeTime));

        for (CanalEntry.Column column : columns) {
            String name = column.getName();
            String value = column.getValue();
            if ("id".equalsIgnoreCase(name)) {
                message.setId(toLong(value));
            } else if ("username".equalsIgnoreCase(name)) {
                message.setUsername(value);
            } else if ("userAccount".equalsIgnoreCase(name)) {
                message.setUserAccount(value);
            } else if ("avatarUrl".equalsIgnoreCase(name)) {
                message.setAvatarUrl(value);
            } else if ("userStatus".equalsIgnoreCase(name)) {
                message.setUserStatus(toInteger(value));
            } else if ("tags".equalsIgnoreCase(name)) {
                message.setTags(value);
            } else if ("profile".equalsIgnoreCase(name)) {
                message.setProfile(value);
            } else if ("isDelete".equalsIgnoreCase(name)) {
                message.setIsDelete(toInteger(value));
            }
        }
        return message;
    }

    private Long toLong(String value) {
        try {
            return value == null || value.isEmpty() ? null : Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer toInteger(String value) {
        try {
            return value == null || value.isEmpty() ? null : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

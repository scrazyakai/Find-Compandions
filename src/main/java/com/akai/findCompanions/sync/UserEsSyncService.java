package com.akai.findCompanions.sync;

import com.akai.findCompanions.config.properties.UserSyncEsProperties;
import com.akai.findCompanions.model.dto.sync.UserSyncMessage;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserEsSyncService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private UserSyncEsProperties userSyncEsProperties;

    public void sync(UserSyncMessage message) throws IOException {
        if (message == null || message.getId() == null) {
            return;
        }
        if (isDelete(message)) {
            deleteDoc(message.getId());
            return;
        }
        upsertDoc(message);
    }

    private void upsertDoc(UserSyncMessage message) throws IOException {
        Map<String, Object> doc = new HashMap<>();
        doc.put("id", message.getId());
        doc.put("username", message.getUsername());
        doc.put("userAccount", message.getUserAccount());
        doc.put("avatarUrl", message.getAvatarUrl());
        doc.put("userStatus", message.getUserStatus());
        doc.put("tags", message.getTags());
        doc.put("profile", message.getProfile());
        doc.put("isDelete", message.getIsDelete());
        if (message.getEventTime() != null) {
            doc.put("eventTime", message.getEventTime().getTime());
        }

        UpdateRequest request = new UpdateRequest(userSyncEsProperties.getUserIndex(), String.valueOf(message.getId()))
                .doc(doc)
                .docAsUpsert(true);
        restHighLevelClient.update(request, RequestOptions.DEFAULT);
    }

    private void deleteDoc(Long id) throws IOException {
        DeleteRequest request = new DeleteRequest(userSyncEsProperties.getUserIndex(), String.valueOf(id));
        try {
            restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        } catch (ElasticsearchStatusException ex) {
            if (ex.status() != RestStatus.NOT_FOUND) {
                throw ex;
            }
            log.info("user doc not found when deleting from es, id={}", id);
        }
    }

    private boolean isDelete(UserSyncMessage message) {
        return "DELETE".equalsIgnoreCase(message.getOperationType())
                || Integer.valueOf(1).equals(message.getIsDelete());
    }
}

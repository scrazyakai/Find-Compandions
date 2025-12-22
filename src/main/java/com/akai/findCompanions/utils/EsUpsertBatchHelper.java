package com.akai.findCompanions.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.cglib.core.internal.Function;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class EsUpsertBatchHelper {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    private static final String INDEX_NAME = "user_index";

    public <T> void insertOrUpdateBatch(List<T> docs, Function<T, String> idGetter) {

        if (docs == null || docs.isEmpty()) {
            return;
        }

        BulkRequest bulkRequest = new BulkRequest();

        for (T doc : docs) {
            String id = idGetter.apply(doc);

            Map<String, Object> source =
                    JSON.parseObject(JSON.toJSONString(doc), Map.class);

            UpdateRequest updateRequest = new UpdateRequest(INDEX_NAME, id)
                    .doc(source)
                    .docAsUpsert(true); //  核心

            bulkRequest.add(updateRequest);
        }

        try {
            BulkResponse response = restHighLevelClient.bulk(
                    bulkRequest, RequestOptions.DEFAULT);

            if (response.hasFailures()) {
                log.error("ES 批量 upsert 部分失败: {}", response.buildFailureMessage());
            }
        } catch (Exception e) {
            log.error("ES 批量 upsert 失败", e);
            throw new RuntimeException(e);
        }
    }
}

package com.akai.findCompanions.sync.full;

import com.akai.findCompanions.config.properties.UserSyncFullProperties;
import com.akai.findCompanions.mapper.db.UserMapper;
import com.akai.findCompanions.model.domain.Es.UserDocument;
import com.akai.findCompanions.model.domain.User;
import com.akai.findCompanions.utils.EsUpsertBatchHelper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 首次接入 ES 的全量同步任务（启动时执行一次）
 */
@Component
@Slf4j
public class UserEsFullSyncRunner implements CommandLineRunner {

    @Resource
    private UserMapper userMapper;

    @Resource
    private EsUpsertBatchHelper esUpsertBatchHelper;

    @Resource
    private UserSyncFullProperties userSyncFullProperties;

    @Override
    public void run(String... args) {
        if (!userSyncFullProperties.isEnabled()) {
            return;
        }

        long batchSize = userSyncFullProperties.getBatchSize();
        long current = 1;
        long totalSynced = 0;

        log.info("开始执行用户全量同步到 ES, batchSize={}", batchSize);

        while (true) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("isDelete", 0);
            Page<User> page = userMapper.selectPage(new Page<>(current, batchSize), queryWrapper);
            List<User> records = page.getRecords();
            if (records == null || records.isEmpty()) {
                break;
            }

            List<UserDocument> docs = records.stream().map(this::toDocument).collect(Collectors.toList());
            esUpsertBatchHelper.insertOrUpdateBatch(docs, doc -> String.valueOf(doc.getId()));

            totalSynced += records.size();
            log.info("用户全量同步进度: page={}, synced={}", current, totalSynced);

            if (current >= page.getPages()) {
                break;
            }
            current++;
        }

        log.info("用户全量同步完成, totalSynced={}", totalSynced);
    }

    private UserDocument toDocument(User user) {
        UserDocument doc = new UserDocument();
        doc.setId(user.getId());
        doc.setUsername(user.getUsername());
        doc.setUserAccount(user.getUserAccount());
        doc.setAvatarUrl(user.getAvatarUrl());
        doc.setUserStatus(user.getUserStatus());
        doc.setProfile(user.getProfile());
        doc.setTags(parseTags(user.getTags()));
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

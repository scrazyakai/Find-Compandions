package com.akai.findCompanions.controller;
import java.util.ArrayList;

import com.akai.findCompanions.enums.UserStatusEnum;
import com.akai.findCompanions.mapper.db.UserMapper;
import com.akai.findCompanions.model.domain.Es.UserDocument;
import com.akai.findCompanions.model.domain.User;
import com.akai.findCompanions.utils.EsUpsertBatchHelper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class UserControllerTest {
    @Resource
    private EsUpsertBatchHelper esUpsertBatchHelper;

    @Resource
    private UserMapper userMapper;

    private final int pageSize = 1000;

    @Test
    public void DBImportES() {

        int pageNum = 1;

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(
                "userStatus",
                UserStatusEnum.getValueByEnum(UserStatusEnum.NORMAL)
        );

        while (true) {
            Page<User> userPages = new Page<>(pageNum, pageSize);
            userMapper.selectPage(userPages, queryWrapper);

            List<User> users = userPages.getRecords();
            if (users == null || users.isEmpty()) {
                break;
            }

            List<UserDocument> userDocumentList = users.stream().map(user -> {

                List<String> tagList = new ArrayList<>();
                if (StringUtils.isNotBlank(user.getTags())) {
                    tagList = Arrays.stream(user.getTags().split(","))
                            .map(String::trim)
                            .filter(tag -> !tag.isEmpty())
                            .collect(Collectors.toList());
                }

                UserDocument userDocument = new UserDocument();
                userDocument.setId(user.getId());
                userDocument.setUsername(user.getUsername());
                userDocument.setUserAccount(user.getUserAccount());
                userDocument.setAvatarUrl(user.getAvatarUrl());
                userDocument.setUserStatus(user.getUserStatus());
                userDocument.setTags(tagList);
                userDocument.setProfile(user.getProfile());
                return userDocument;

            }).collect(Collectors.toList());

            try {
                esUpsertBatchHelper.insertOrUpdateBatch(
                        userDocumentList,
                        doc -> doc.getId().toString()
                );
            } catch (Exception e) {
                log.error("ES 批量同步失败，pageNum={}", pageNum, e);
            }

            if (!userPages.hasNext()) {
                break;
            }
            pageNum++;
        }
    }


}
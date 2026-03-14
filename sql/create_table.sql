/*
 Navicat MySQL Dump SQL

 Source Server         : 京东云
 Source Server Type    : MySQL
 Source Server Version : 50743 (5.7.43-log)
 Source Host           : localhost:3306
 Source Schema         : findcompanionsdb

 Target Server Type    : MySQL
 Target Server Version : 50743 (5.7.43-log)
 File Encoding         : 65001

 Date: 19/07/2025 17:38:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_group
-- ----------------------------
DROP TABLE IF EXISTS `chat_group`;
CREATE TABLE `chat_group`  (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                               `userId` bigint(20) NULL DEFAULT NULL,
                               `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '发送的消息',
                               `sendTime` datetime NULL DEFAULT NULL,
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '聊天厅' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for chat_private
-- ----------------------------
DROP TABLE IF EXISTS `chat_private`;
CREATE TABLE `chat_private`  (
                                 `id` bigint(20) NOT NULL,
                                 `fromUserId` bigint(20) NOT NULL,
                                 `toUserId` bigint(20) NOT NULL,
                                 `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                 `sentTime` datetime NOT NULL,
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                        `tagName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签名称',
                        `userId` bigint(20) NULL DEFAULT NULL COMMENT '用户 id',
                        `parentId` bigint(20) NULL DEFAULT NULL COMMENT '父标签 id',
                        `isParent` tinyint(4) NULL DEFAULT NULL COMMENT '0 - 不是, 1 - 父标签',
                        `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
                        PRIMARY KEY (`id`) USING BTREE,
                        UNIQUE INDEX `uniIdx_tagName`(`tagName`) USING BTREE,
                        INDEX `idx_userId`(`userId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '标签' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for team
-- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team`  (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                         `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '队伍名称',
                         `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
                         `maxNum` int(11) NOT NULL DEFAULT 1 COMMENT '最大人数',
                         `expireTime` datetime NULL DEFAULT NULL COMMENT '过期时间',
                         `userId` bigint(20) NULL DEFAULT NULL COMMENT '用户id（队长 id）',
                         `status` int(11) NOT NULL DEFAULT 0 COMMENT '0 - 公开，1 - 私有，2 - 加密',
                         `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
                         `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '队伍' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
                         `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
                         `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                         `userAccount` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
                         `avatarUrl` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像',
                         `gender` tinyint(4) NULL DEFAULT NULL COMMENT '性别',
                         `userPassword` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
                         `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电话',
                         `email` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
                         `userStatus` int(11) NOT NULL DEFAULT 0 COMMENT '状态 0 - 正常',
                         `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
                         `userRole` int(11) NOT NULL DEFAULT 0 COMMENT '用户角色 0 - 普通用户 1 - 管理员',
                         `planetCode` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '星球编号',
                         `tags` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签 json 列表',
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1028 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_friend
-- ----------------------------
DROP TABLE IF EXISTS `user_friend`;
CREATE TABLE `user_friend`  (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `userId` bigint(20) NOT NULL COMMENT '发送者ID',
                                `friendId` int(11) NOT NULL COMMENT '同意者Id',
                                `status` int(11) NOT NULL COMMENT '处理状态，0为未处理，1拒绝，2同意',
                                `createTime` datetime NULL DEFAULT NULL COMMENT '好友申请时间',
                                `updateTime` datetime NULL DEFAULT NULL COMMENT '处理时间',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '好友表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_team
-- ----------------------------
DROP TABLE IF EXISTS `user_team`;
CREATE TABLE `user_team`  (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                              `userId` bigint(20) NULL DEFAULT NULL COMMENT '用户id',
                              `teamId` bigint(20) NULL DEFAULT NULL COMMENT '队伍id',
                              `joinTime` datetime NULL DEFAULT NULL COMMENT '加入时间',
                              `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户队伍关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for activity
-- ----------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity`  (
    `activityId` bigint(20) NOT NULL COMMENT '活动id',
    `activityDesc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '活动描述',
    `startTime` datetime NOT NULL COMMENT '开始时间',
    `endTime` datetime NOT NULL COMMENT '结束时间',
    `status` tinyint(4) NOT NULL COMMENT '活动状态(1未开始，2正在进行中，3已结束，4已取消)',
    `coverURL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面',
    `maxMemberNumber` int(11) NULL DEFAULT NULL COMMENT '最大活动人数',
    `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `updateTime` datetime NULL DEFAULT NULL COMMENT '更新时间',
    `ownerId` bigint(20) NOT NULL COMMENT '创建者Id',
    `userAvatarURL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像URL',
    `userName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
    `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '活动加密',
    PRIMARY KEY (`activityId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for activity_comment
-- ----------------------------
DROP TABLE IF EXISTS `activity_comment`;
CREATE TABLE `activity_comment`  (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `userId` bigint(20) NOT NULL COMMENT '用户Id',
    `activityId` bigint(20) NOT NULL COMMENT '活动Id',
    `parentId` bigint(20) NULL DEFAULT NULL COMMENT '父评论Id',
    `imageURL` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `isTop` tinyint(4) NULL DEFAULT NULL,
    `level` tinyint(4) NOT NULL DEFAULT 1 COMMENT '评论级别(1代表一级标题，2代表二级标题)',
    `replyTotal` bigint(20) NOT NULL DEFAULT 0 COMMENT '回复总数，仅一级评论',
    `likeTotal` bigint(20) NOT NULL DEFAULT 0 COMMENT '点赞总数',
    `createTime` datetime NOT NULL COMMENT '创建时间',
    `updateTime` datetime NOT NULL COMMENT '更新时间',
    `status` tinyint(4) NOT NULL COMMENT '逻辑删除',
    `replyCommentId` bigint(20) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `activity_id_index`(`activityId`) USING BTREE,
    INDEX `parent_id_idx`(`parentId`) USING BTREE,
    INDEX `user_id_index`(`userId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for comment_content
-- ----------------------------
DROP TABLE IF EXISTS `comment_content`;
CREATE TABLE `comment_content`  (
    `commentId` bigint(20) NOT NULL COMMENT '评论id',
    `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容',
    `yearMonth` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '年月',
    `activityId` bigint(20) NOT NULL COMMENT '活动id',
    PRIMARY KEY (`commentId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for comment_like
-- ----------------------------
DROP TABLE IF EXISTS `comment_like`;
CREATE TABLE `comment_like`  (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `userId` bigint(20) NOT NULL,
    `commentId` bigint(20) NOT NULL,
    `createTime` datetime NOT NULL,
    `isdelete` tinyint(4) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `comment_like_pk`(`userId`, `commentId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for activity_member
-- ----------------------------
DROP TABLE IF EXISTS `activity_member`;
CREATE TABLE `activity_member`  (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `userId` bigint(20) NOT NULL COMMENT '用户id',
    `activityId` bigint(20) NOT NULL COMMENT '活动id',
    `joinTime` datetime NULL DEFAULT NULL COMMENT '加入时间',
    `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '加入状态(0-待审核,1-已加入,2-已退出)',
    `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_userId`(`userId`) USING BTREE,
    INDEX `idx_activityId`(`activityId`) USING BTREE,
    UNIQUE INDEX `uniq_user_activity`(`userId`, `activityId`) USING BTREE COMMENT '同一用户不能重复加入同一活动'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '活动成员表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Table structure for user_sync_compensation
-- ----------------------------
DROP TABLE IF EXISTS `user_sync_compensation`;
CREATE TABLE `user_sync_compensation`  (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `userId` bigint(20) NOT NULL COMMENT 'user id',
    `operationType` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'INSERT/UPDATE/DELETE',
    `retryTimes` int(11) NOT NULL DEFAULT 0 COMMENT 'actual retry times',
    `maxRetryTimes` int(11) NOT NULL DEFAULT 3 COMMENT 'max retry times',
    `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0-pending,1-success,2-failed',
    `errorMessage` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'last error',
    `payload` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'original mq payload',
    `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_userId_status`(`userId`, `status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'user sync compensation';

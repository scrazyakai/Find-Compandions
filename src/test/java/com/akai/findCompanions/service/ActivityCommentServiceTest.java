package com.akai.findCompanions.service;

import com.akai.findCompanions.common.ErrorCode;
import com.akai.findCompanions.enums.CommentLevelEnum;
import com.akai.findCompanions.enums.CommentStatusEnum;
import com.akai.findCompanions.exception.BusinessException;
import com.akai.findCompanions.model.domain.Activity;
import com.akai.findCompanions.model.domain.ActivityComment;
import com.akai.findCompanions.model.domain.User;
import com.akai.findCompanions.service.impl.ActivityCommentServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 活动评论服务测试类
 *
 * @author Recursion
 * @since 2026-01-06
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")  // 使用测试配置
public class ActivityCommentServiceTest {

    @Resource
    private IActivityCommentService activityCommentService;

    @Resource
    private IActivityService activityService;

    @Resource
    private IUserService userService;

    private Long testActivityId;
    private Long testUserId;
    private Long testCommentId;

    /**
     * 测试前准备数据
     */
    @BeforeEach
    public void setUp() {
        log.info("======== 开始准备测试数据 ========");

        // 准备测试用户
        User testUser = new User();
        testUser.setUsername("测试用户");
        testUser.setUserAccount("testCommentUser");
        testUser.setUserPassword("12345678");
        testUser.setPhone("13800138000");
        userService.save(testUser);
        testUserId = testUser.getId();
        log.info("创建测试用户，ID: {}", testUserId);

        // 准备测试活动
        Activity testActivity = new Activity();
        testActivity.setActivityDesc("测试活动描述");
        testActivity.setOwnerId(testUserId);
        testActivity.setUserName("测试用户");
        testActivity.setUserAvatarURL("https://example.com/avatar.jpg");
        testActivity.setStatus(1);  // 未开始
        activityService.save(testActivity);
        testActivityId = testActivity.getActivityId();
        log.info("创建测试活动，ID: {}", testActivityId);

        log.info("======== 测试数据准备完成 ========");
    }

    /**
     * 测试添加一级评论
     */
    @Test
    @Transactional
    public void testAddFirstLevelComment() {
        log.info("======== 测试添加一级评论 ========");

        ActivityComment comment = new ActivityComment();
        comment.setActivityId(testActivityId);
        comment.setParentId(null);  // 一级评论
        String content = "这是一条测试评论内容";

        boolean result = activityCommentService.addComment(comment, content, testUserId);

        Assertions.assertTrue(result, "添加评论应该成功");
        Assertions.assertNotNull(comment.getId(), "评论ID应该被生成");
        Assertions.assertEquals(CommentLevelEnum.FIRST_LEVEL.getValue(), comment.getLevel(),
                "评论级别应该是一级");
        Assertions.assertEquals(CommentStatusEnum.NOT_DELETED.getValue(), comment.getStatus(),
                "评论状态应该是未删除");

        testCommentId = comment.getId();
        log.info("一级评论添加成功，评论ID: {}", testCommentId);
    }

    /**
     * 测试添加二级评论（回复）
     */
    @Test
    @Transactional
    public void testAddSecondLevelComment() {
        log.info("======== 测试添加二级评论 ========");

        // 先添加一级评论
        ActivityComment parentComment = new ActivityComment();
        parentComment.setActivityId(testActivityId);
        activityCommentService.addComment(parentComment, "父评论内容", testUserId);
        Long parentId = parentComment.getId();
        log.info("创建父评论，ID: {}", parentId);

        // 添加二级评论
        ActivityComment replyComment = new ActivityComment();
        replyComment.setActivityId(testActivityId);
        replyComment.setParentId(parentId);  // 回复父评论
        String content = "这是一条回复内容";

        boolean result = activityCommentService.addComment(replyComment, content, testUserId);

        Assertions.assertTrue(result, "添加回复应该成功");
        Assertions.assertNotNull(replyComment.getId(), "回复ID应该被生成");
        Assertions.assertEquals(CommentLevelEnum.SECOND_LEVEL.getValue(), replyComment.getLevel(),
                "评论级别应该是二级");
        Assertions.assertEquals(parentId, replyComment.getParentId(),
                "父评论ID应该匹配");

        log.info("二级评论添加成功，回复ID: {}", replyComment.getId());
    }

    /**
     * 测试删除评论
     */
    @Test
    @Transactional
    public void testDeleteComment() {
        log.info("======== 测试删除评论 ========");

        // 先添加评论
        ActivityComment comment = new ActivityComment();
        comment.setActivityId(testActivityId);
        activityCommentService.addComment(comment, "要删除的评论", testUserId);
        Long commentId = comment.getId();
        log.info("创建测试评论，ID: {}", commentId);

        // 删除评论
        boolean result = activityCommentService.deleteComment(commentId, testUserId);

        Assertions.assertTrue(result, "删除评论应该成功");

        // 验证评论状态
        ActivityComment deletedComment = activityCommentService.getById(commentId);
        Assertions.assertEquals(CommentStatusEnum.DELETED.getValue(), deletedComment.getStatus(),
                "评论状态应该是已删除");

        log.info("评论删除成功，ID: {}", commentId);
    }

    /**
     * 测试更新评论
     */
    @Test
    @Transactional
    public void testUpdateComment() {
        log.info("======== 测试更新评论 ========");

        // 先添加评论
        ActivityComment comment = new ActivityComment();
        comment.setActivityId(testActivityId);
        activityCommentService.addComment(comment, "原始内容", testUserId);
        Long commentId = comment.getId();
        log.info("创建测试评论，ID: {}", commentId);

        // 更新评论
        ActivityComment updateComment = new ActivityComment();
        updateComment.setId(commentId);
        updateComment.setImageURL("https://example.com/new-image.jpg");
        String newContent = "更新后的评论内容";

        boolean result = activityCommentService.updateComment(updateComment, newContent, testUserId);

        Assertions.assertTrue(result, "更新评论应该成功");

        log.info("评论更新成功，ID: {}", commentId);
    }

    /**
     * 测试查询评论列表
     */
    @Test
    @Transactional
    public void testGetCommentList() {
        log.info("======== 测试查询评论列表 ========");

        // 添加几条测试评论
        for (int i = 0; i < 5; i++) {
            ActivityComment comment = new ActivityComment();
            comment.setActivityId(testActivityId);
            activityCommentService.addComment(comment, "测试评论 " + i, testUserId);
        }
        log.info("创建了 5 条测试评论");

        // 查询评论列表
        Page<ActivityComment> commentPage = activityCommentService.getCommentList(testActivityId, 1L, 10L);

        Assertions.assertNotNull(commentPage, "评论列表不应为空");
        Assertions.assertTrue(commentPage.getRecords().size() >= 5, "至少应该有5条评论");
        Assertions.assertEquals(1, commentPage.getCurrent(), "当前页应该是1");
        Assertions.assertEquals(10, commentPage.getSize(), "页大小应该是10");

        log.info("查询到 {} 条评论", commentPage.getRecords().size());
    }

    /**
     * 测试查询回复列表
     */
    @Test
    @Transactional
    public void testGetReplyList() {
        log.info("======== 测试查询回复列表 ========");

        // 先添加一级评论
        ActivityComment parentComment = new ActivityComment();
        parentComment.setActivityId(testActivityId);
        activityCommentService.addComment(parentComment, "父评论", testUserId);
        Long parentId = parentComment.getId();
        log.info("创建父评论，ID: {}", parentId);

        // 添加几条回复
        for (int i = 0; i < 3; i++) {
            ActivityComment reply = new ActivityComment();
            reply.setActivityId(testActivityId);
            reply.setParentId(parentId);
            activityCommentService.addComment(reply, "回复 " + i, testUserId);
        }
        log.info("创建了 3 条回复");

        // 查询回复列表
        Page<ActivityComment> replyPage = activityCommentService.getReplyList(parentId, 1L, 10L);

        Assertions.assertNotNull(replyPage, "回复列表不应为空");
        Assertions.assertTrue(replyPage.getRecords().size() >= 3, "至少应该有3条回复");

        log.info("查询到 {} 条回复", replyPage.getRecords().size());
    }

    /**
     * 测试点赞评论
     */
    @Test
    @Transactional
    public void testToggleLike() {
        log.info("======== 测试点赞评论 ========");

        // 先添加评论
        ActivityComment comment = new ActivityComment();
        comment.setActivityId(testActivityId);
        activityCommentService.addComment(comment, "测试点赞的评论", testUserId);
        Long commentId = comment.getId();
        log.info("创建测试评论，ID: {}", commentId);

        // 点赞
        Long likeTotal = activityCommentService.toggleLike(commentId, testUserId);
        log.info("点赞后点赞数: {}", likeTotal);
        Assertions.assertEquals(1L, likeTotal, "点赞数应该是1");

        // 取消点赞
        likeTotal = activityCommentService.toggleLike(commentId, testUserId);
        log.info("取消点赞后点赞数: {}", likeTotal);
        Assertions.assertEquals(0L, likeTotal, "点赞数应该是0");

        // 再次点赞
        likeTotal = activityCommentService.toggleLike(commentId, testUserId);
        log.info("再次点赞后点赞数: {}", likeTotal);
        Assertions.assertEquals(1L, likeTotal, "点赞数应该是1");
    }

    /**
     * 测试添加评论失败 - 活动ID为空
     */
    @Test
    @Transactional
    public void testAddCommentFailActivityIdNull() {
        log.info("======== 测试添加评论失败 - 活动ID为空 ========");

        ActivityComment comment = new ActivityComment();
        // 不设置 activityId

        Assertions.assertThrows(BusinessException.class, () -> {
            activityCommentService.addComment(comment, "测试内容", testUserId);
        }, "应该抛出参数错误异常");
    }

    /**
     * 测试添加评论失败 - 内容为空
     */
    @Test
    @Transactional
    public void testAddCommentFailContentNull() {
        log.info("======== 测试添加评论失败 - 内容为空 ========");

        ActivityComment comment = new ActivityComment();
        comment.setActivityId(testActivityId);
        String content = "";  // 空内容

        Assertions.assertThrows(BusinessException.class, () -> {
            activityCommentService.addComment(comment, content, testUserId);
        }, "应该抛出参数错误异常");
    }

    /**
     * 测试删除评论失败 - 无权限
     */
    @Test
    @Transactional
    public void testDeleteCommentFailNoPermission() {
        log.info("======== 测试删除评论失败 - 无权限 ========");

        // 先用测试用户添加评论
        ActivityComment comment = new ActivityComment();
        comment.setActivityId(testActivityId);
        activityCommentService.addComment(comment, "测试评论", testUserId);
        Long commentId = comment.getId();

        // 尝试用另一个用户ID删除（假设这个用户不存在或不是评论者）
        Long anotherUserId = testUserId + 9999;

        Assertions.assertThrows(BusinessException.class, () -> {
            activityCommentService.deleteComment(commentId, anotherUserId);
        }, "应该抛出无权限异常");
    }
}

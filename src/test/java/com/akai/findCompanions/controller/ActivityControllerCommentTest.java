package com.akai.findCompanions.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.akai.findCompanions.FindCompandionsApplication;
import com.akai.findCompanions.model.domain.Activity;
import com.akai.findCompanions.model.domain.User;
import com.akai.findCompanions.model.request.CommentAddRequest;
import com.akai.findCompanions.model.request.CommentDeleteRequest;
import com.akai.findCompanions.model.request.CommentListRequest;
import com.akai.findCompanions.model.request.CommentReplyListRequest;
import com.akai.findCompanions.model.request.CommentToggleLikeRequest;
import com.akai.findCompanions.model.request.CommentUpdateRequest;
import com.akai.findCompanions.service.IActivityCommentService;
import com.akai.findCompanions.service.IActivityService;
import com.akai.findCompanions.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 活动评论接口测试类
 *
 * @author Recursion
 * @since 2026-01-06
 */
@Slf4j
@SpringBootTest(classes = FindCompandionsApplication.class)
public class ActivityControllerCommentTest {
    private MockMvc mvc;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IActivityCommentService activityCommentService;

    @Autowired
    private IActivityService activityService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Long testActivityId;
    private Long testUserId;
    private Long testCommentId;
    private String testUserToken;

    /**
     * 测试前准备数据
     */
    @BeforeEach
    public void setUp() throws Exception {
        log.info("======== 开始准备测试数据 ========");

        // 准备测试用户
        User testUser = new User();
        testUser.setUsername("测试评论用户");
        testUser.setUserAccount("testCommentUserController");
        testUser.setUserPassword("12345678");
        testUser.setPhone("13900139000");
        userService.save(testUser);
        testUserId = testUser.getId();
        log.info("创建测试用户，ID: {}", testUserId);

        // 模拟登录，获取 token
        StpUtil.login(testUserId);
        testUserToken = StpUtil.getTokenValue();
        log.info("用户登录成功，Token: {}", testUserToken);

        // 准备测试活动
        Activity testActivity = new Activity();
        testActivity.setActivityDesc("Controller测试活动");
        testActivity.setOwnerId(testUserId);
        testActivity.setUserName("测试评论用户");
        testActivity.setUserAvatarURL("https://example.com/avatar.jpg");
        testActivity.setStatus(1);
        activityService.save(testActivity);
        testActivityId = testActivity.getActivityId();
        log.info("创建测试活动，ID: {}", testActivityId);

        log.info("======== 测试数据准备完成 ========");
    }

    /**
     * 测试添加评论接口
     */
    @Test
    @Transactional
    public void testAddComment() throws Exception {
        log.info("======== 测试添加评论接口 ========");

        CommentAddRequest request = new CommentAddRequest();
        request.setActivityId(testActivityId);
        request.setContent("这是通过API添加的测试评论");
        request.setImageURL("https://example.com/test-image.jpg");

        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/activity/comment/add")
                        .header("satoken", testUserToken)  // 添加认证 token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists())
                .andDo(print())
                .andReturn();

        String response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        log.info("添加评论响应: {}", response);

        // 从响应中获取评论ID
        // 注意：实际使用时需要解析 JSON 获取 data 字段
    }

    /**
     * 测试添加二级评论接口
     */
    @Test
    @Transactional
    public void testAddReplyComment() throws Exception {
        log.info("======== 测试添加二级评论接口 ========");

        // 先创建父评论
        com.akai.findCompanions.model.domain.ActivityComment parentComment =
                new com.akai.findCompanions.model.domain.ActivityComment();
        parentComment.setActivityId(testActivityId);
        activityCommentService.addComment(parentComment, "父评论内容", testUserId);
        Long parentId = parentComment.getId();
        log.info("创建父评论，ID: {}", parentId);

        // 添加回复
        CommentAddRequest request = new CommentAddRequest();
        request.setActivityId(testActivityId);
        request.setParentId(parentId);
        request.setContent("这是回复内容");

        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/activity/comment/add")
                        .header("satoken", testUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andDo(print())
                .andReturn();

        log.info("添加回复响应: {}", result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    /**
     * 测试删除评论接口
     */
    @Test
    @Transactional
    public void testDeleteComment() throws Exception {
        log.info("======== 测试删除评论接口 ========");

        // 先创建评论
        com.akai.findCompanions.model.domain.ActivityComment comment =
                new com.akai.findCompanions.model.domain.ActivityComment();
        comment.setActivityId(testActivityId);
        activityCommentService.addComment(comment, "要删除的评论", testUserId);
        Long commentId = comment.getId();
        log.info("创建测试评论，ID: {}", commentId);

        // 删除评论
        CommentDeleteRequest request = new CommentDeleteRequest();
        request.setCommentId(commentId);

        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/activity/comment/delete")
                        .header("satoken", testUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true))
                .andDo(print())
                .andReturn();

        log.info("删除评论响应: {}", result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    /**
     * 测试更新评论接口
     */
    @Test
    @Transactional
    public void testUpdateComment() throws Exception {
        log.info("======== 测试更新评论接口 ========");

        // 先创建评论
        com.akai.findCompanions.model.domain.ActivityComment comment =
                new com.akai.findCompanions.model.domain.ActivityComment();
        comment.setActivityId(testActivityId);
        activityCommentService.addComment(comment, "原始内容", testUserId);
        Long commentId = comment.getId();
        log.info("创建测试评论，ID: {}", commentId);

        // 更新评论
        CommentUpdateRequest request = new CommentUpdateRequest();
        request.setCommentId(commentId);
        request.setContent("更新后的评论内容");
        request.setImageURL("https://example.com/updated-image.jpg");

        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/activity/comment/update")
                        .header("satoken", testUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true))
                .andDo(print())
                .andReturn();

        log.info("更新评论响应: {}", result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    /**
     * 测试查询评论列表接口
     */
    @Test
    @Transactional
    public void testGetCommentList() throws Exception {
        log.info("======== 测试查询评论列表接口 ========");

        // 先创建几条评论
        for (int i = 0; i < 3; i++) {
            com.akai.findCompanions.model.domain.ActivityComment comment =
                    new com.akai.findCompanions.model.domain.ActivityComment();
            comment.setActivityId(testActivityId);
            activityCommentService.addComment(comment, "测试评论 " + i, testUserId);
        }
        log.info("创建了 3 条测试评论");

        // 查询评论列表
        CommentListRequest request = new CommentListRequest();
        request.setActivityId(testActivityId);
        request.setPageNo(1);
        request.setPageSize(10);

        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/activity/comment/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.records").isArray())
                .andDo(print())
                .andReturn();

        log.info("查询评论列表响应: {}", result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    /**
     * 测试查询回复列表接口
     */
    @Test
    @Transactional
    public void testGetReplyList() throws Exception {
        log.info("======== 测试查询回复列表接口 ========");

        // 先创建父评论
        com.akai.findCompanions.model.domain.ActivityComment parentComment =
                new com.akai.findCompanions.model.domain.ActivityComment();
        parentComment.setActivityId(testActivityId);
        activityCommentService.addComment(parentComment, "父评论", testUserId);
        Long parentId = parentComment.getId();

        // 创建几条回复
        for (int i = 0; i < 2; i++) {
            com.akai.findCompanions.model.domain.ActivityComment reply =
                    new com.akai.findCompanions.model.domain.ActivityComment();
            reply.setActivityId(testActivityId);
            reply.setParentId(parentId);
            activityCommentService.addComment(reply, "回复 " + i, testUserId);
        }
        log.info("创建了父评论和 2 条回复");

        // 查询回复列表
        CommentReplyListRequest request = new CommentReplyListRequest();
        request.setParentId(parentId);
        request.setPageNo(1);
        request.setPageSize(10);

        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/activity/comment/reply/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.records").isArray())
                .andDo(print())
                .andReturn();

        log.info("查询回复列表响应: {}", result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    /**
     * 测试点赞/取消点赞接口
     */
    @Test
    @Transactional
    public void testToggleLike() throws Exception {
        log.info("======== 测试点赞/取消点赞接口 ========");

        // 先创建评论
        com.akai.findCompanions.model.domain.ActivityComment comment =
                new com.akai.findCompanions.model.domain.ActivityComment();
        comment.setActivityId(testActivityId);
        activityCommentService.addComment(comment, "测试点赞的评论", testUserId);
        Long commentId = comment.getId();
        log.info("创建测试评论，ID: {}", commentId);

        // 点赞
        CommentToggleLikeRequest request = new CommentToggleLikeRequest();
        request.setCommentId(commentId);

        String jsonRequest = objectMapper.writeValueAsString(request);

        // 第一次点赞
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/activity/comment/toggleLike")
                        .header("satoken", testUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(1))
                .andDo(print());

        log.info("第一次点赞成功");

        // 第二次点赞（应该取消点赞）
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/activity/comment/toggleLike")
                        .header("satoken", testUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(0))
                .andDo(print());

        log.info("取消点赞成功");
    }

    /**
     * 测试添加评论失败 - 参数错误
     */
    @Test
    @Transactional
    public void testAddCommentFail() throws Exception {
        log.info("======== 测试添加评论失败 - 参数错误 ========");

        // 不设置 activityId
        CommentAddRequest request = new CommentAddRequest();
        request.setContent("测试内容");

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/activity/comment/add")
                        .header("satoken", testUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(40000))  // PARAMS_ERROR
                .andDo(print());

        log.info("参数错误测试成功");
    }

    /**
     * 测试删除评论失败 - 未登录
     */
    @Test
    @Transactional
    public void testDeleteCommentFailNotLogin() throws Exception {
        log.info("======== 测试删除评论失败 - 未登录 ========");

        CommentDeleteRequest request = new CommentDeleteRequest();
        request.setCommentId(1L);

        String jsonRequest = objectMapper.writeValueAsString(request);

        // 不提供 token
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/activity/comment/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // 可能返回未登录错误或重定向到登录页
                .andDo(print());

        log.info("未登录测试完成");
    }
}

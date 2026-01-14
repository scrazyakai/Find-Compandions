package com.akai.findCompanions.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.akai.findCompanions.common.BaseResponse;
import com.akai.findCompanions.common.ErrorCode;
import com.akai.findCompanions.common.ResultUtils;
import com.akai.findCompanions.enums.ActivityParticipantEnum;
import com.akai.findCompanions.mapper.db.*;
import com.akai.findCompanions.model.domain.*;
import com.akai.findCompanions.exception.BusinessException;
import com.akai.findCompanions.model.request.ActivityJoinRequest;
import com.akai.findCompanions.model.request.ActivityQuitRequest;
import com.akai.findCompanions.model.request.CommentAddRequest;
import com.akai.findCompanions.model.request.CommentDeleteRequest;
import com.akai.findCompanions.model.request.CommentListRequest;
import com.akai.findCompanions.model.request.CommentReplyListRequest;
import com.akai.findCompanions.model.request.CommentToggleLikeRequest;
import com.akai.findCompanions.model.request.CommentUpdateRequest;
import com.akai.findCompanions.model.vo.ActivityCommentVO;
import com.akai.findCompanions.model.vo.ActivityVO;
import com.akai.findCompanions.service.IActivityCommentService;
import com.akai.findCompanions.service.IActivityService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Recursion
 * @since 2025-12-20
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {
    @Resource
    private IActivityService activityService;
    @Resource
    private IActivityCommentService activityCommentService;
    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private ActivityCommentMapper activityCommentMapper;
    @Resource
    private CommentContentMapper commentContentMapper;
    @Resource
    private CommentLikeMapper commentLikeMapper;
    @Resource
    private UserMapper userMapper;
    private final int pageSize = 10;
    @Autowired
    private ActivityMemberMapper activityMemberMapper;

    @PostMapping("/add")
    public BaseResponse<Boolean> add(@RequestBody Activity activity) {
        if (activity == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "活动参数不能为空");
        }
        // 设置初始参与人数为0
        activity.setCurrentMemberNumber(0);
        boolean result = activityService.save(activity);
        return ResultUtils.success(result);
    }

    @SaCheckLogin
    @PostMapping("/update")
    public BaseResponse<Boolean> update(@RequestBody Activity activity) {
        if (activity == null || activity.getActivityId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "活动ID不能为空");
        }
        // 获取当前登录用户ID
        long userId = StpUtil.getLoginIdAsLong();
        boolean result = activityService.updateActivity(activity, userId);
        return ResultUtils.success(result);
    }

    @SaCheckLogin
    @PostMapping("/cancel")
    public BaseResponse<Boolean> delete(Long activityId) {
        if (activityId == null || activityId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "活动ID不能为空");
        }
        // 获取当前登录用户ID
        long userId = StpUtil.getLoginIdAsLong();
        boolean result = activityService.cancelActivity(activityId, userId);
        return ResultUtils.success(result);
    }
    
    @SaCheckLogin
    @PostMapping("/join")
    public BaseResponse<Boolean> joinActivity(@RequestBody ActivityJoinRequest activityJoinRequest) {
        if (activityJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        if (activityJoinRequest.getActivityId() == null || activityJoinRequest.getActivityId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "活动ID不能为空");
        }

        long userId = StpUtil.getLoginIdAsLong();
        boolean result = activityService.joinActivity(activityJoinRequest.getActivityId(), userId);
        return ResultUtils.success(result);
    }


    @SaCheckLogin
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitActivity(@RequestBody ActivityQuitRequest activityQuitRequest) {
        if (activityQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        if (activityQuitRequest.getActivityId() == null || activityQuitRequest.getActivityId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "活动ID不能为空");
        }

        long userId = StpUtil.getLoginIdAsLong();
        boolean result = activityService.quitActivity(activityQuitRequest.getActivityId(), userId);
        return ResultUtils.success(result);
    }
    @SaCheckLogin
    @GetMapping("/{activityId}/participant")
    public BaseResponse<Boolean> participantStatus(@PathVariable Long activityId){
        if(activityId == null || activityId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean participant = activityService.participantStatus(activityId);
        return ResultUtils.success(participant);
    }
    @PostMapping("/list")
    public BaseResponse<Page<ActivityVO>> list(@RequestParam(defaultValue = "1") int pageNo) {
        if(pageNo <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"起始页参数异常");
        }
        Page<Activity> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<Activity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Activity::getCreateTime);
        Page<Activity> pages = activityService.page(page, queryWrapper);
        Page<ActivityVO> result = new Page<>(
                pages.getCurrent(),
                pages.getSize(),
                pages.getTotal()
        );
        List<ActivityVO> collect = pages.getRecords()
                .stream().map(activity -> {
            ActivityVO activityVO = new ActivityVO();
            BeanUtils.copyProperties(activity, activityVO);
            return activityVO;
        }).collect(Collectors.toList());
        BeanUtils.copyProperties(pages,result);
        result.setRecords(collect);
        return ResultUtils.success(result);
    }

    /**
     * 添加评论
     * @param commentAddRequest 评论添加请求
     * @return 评论ID
     */
    @SaCheckLogin
    @PostMapping("/comment/add")
    public BaseResponse<Long> addComment(@RequestBody CommentAddRequest commentAddRequest) {
        if (commentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        if (commentAddRequest.getActivityId() == null || commentAddRequest.getActivityId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "活动ID不能为空");
        }
        if (commentAddRequest.getContent() == null || commentAddRequest.getContent().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容不能为空");
        }

        // 构建评论对象
        ActivityComment activityComment = new ActivityComment();
        activityComment.setActivityId(commentAddRequest.getActivityId());
        activityComment.setParentId(commentAddRequest.getParentId());
        activityComment.setImageURL(commentAddRequest.getImageURL());

        // 获取当前登录用户ID
        long userId = StpUtil.getLoginIdAsLong();

        // 调用服务层添加评论
        boolean result = activityCommentService.addComment(activityComment, commentAddRequest.getContent(), userId);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加评论失败");
        }

        return ResultUtils.success(activityComment.getId());
    }

    /**
     * 删除评论
     * @param commentDeleteRequest 评论删除请求
     * @return 是否成功
     */
    @SaCheckLogin
    @PostMapping("/comment/delete")
    public BaseResponse<Boolean> deleteComment(@RequestBody CommentDeleteRequest commentDeleteRequest) {
        if (commentDeleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        if (commentDeleteRequest.getCommentId() == null || commentDeleteRequest.getCommentId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID不能为空");
        }

        long userId = StpUtil.getLoginIdAsLong();
        boolean result = activityCommentService.deleteComment(commentDeleteRequest.getCommentId(), userId);
        return ResultUtils.success(result);
    }

    /**
     * 更新评论
     * @param commentUpdateRequest 评论更新请求
     * @return 是否成功
     */
    @SaCheckLogin
    @PostMapping("/comment/update")
    public BaseResponse<Boolean> updateComment(@RequestBody CommentUpdateRequest commentUpdateRequest) {
        if (commentUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        if (commentUpdateRequest.getCommentId() == null || commentUpdateRequest.getCommentId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID不能为空");
        }
        if (commentUpdateRequest.getContent() == null || commentUpdateRequest.getContent().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容不能为空");
        }

        ActivityComment activityComment = new ActivityComment();
        activityComment.setId(commentUpdateRequest.getCommentId());
        activityComment.setImageURL(commentUpdateRequest.getImageURL());

        long userId = StpUtil.getLoginIdAsLong();

        boolean result = activityCommentService.updateComment(activityComment, commentUpdateRequest.getContent(), userId);
        return ResultUtils.success(result);
    }

    /**
     * 查询活动的评论列表（一级评论）
     * @param commentListRequest 评论列表请求
     * @return 评论列表
     */
    @PostMapping("/comment/list")
    public BaseResponse<Page<ActivityCommentVO>> getCommentList(@RequestBody CommentListRequest commentListRequest) {
        if (commentListRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        if (commentListRequest.getActivityId() == null || commentListRequest.getActivityId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "活动ID不能为空");
        }

        long pageNo = commentListRequest.getPageNo() != null && commentListRequest.getPageNo() > 0
                ? commentListRequest.getPageNo() : 1L;
        long pageSize = commentListRequest.getPageSize() != null && commentListRequest.getPageSize() > 0
                ? commentListRequest.getPageSize() : 10L;

        // 获取当前登录用户ID（可能未登录）
        Long currentUserId = null;
        try {
            currentUserId = StpUtil.getLoginIdAsLong();
        } catch (Exception ignored) {
        }

        // 查询评论列表
        Page<ActivityComment> commentPage = activityCommentService.getCommentList(
                commentListRequest.getActivityId(), pageNo, pageSize);

        // 转换为 VO
        Page<ActivityCommentVO> result = convertToCommentVO(commentPage, currentUserId);

        return ResultUtils.success(result);
    }

    /**
     * 查询评论的回复列表（二级评论）
     * @param commentReplyListRequest 评论回复列表请求
     * @return 回复列表
     */
    @PostMapping("/comment/reply/list")
    public BaseResponse<Page<ActivityCommentVO>> getReplyList(@RequestBody CommentReplyListRequest commentReplyListRequest) {
        if (commentReplyListRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        if (commentReplyListRequest.getParentId() == null || commentReplyListRequest.getParentId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "父评论ID不能为空");
        }

        long pageNo = commentReplyListRequest.getPageNo() != null && commentReplyListRequest.getPageNo() > 0
                ? commentReplyListRequest.getPageNo() : 1L;
        long pageSize = commentReplyListRequest.getPageSize() != null && commentReplyListRequest.getPageSize() > 0
                ? commentReplyListRequest.getPageSize() : 10L;

        // 获取当前登录用户ID（可能未登录）
        Long currentUserId = null;
        try {
            currentUserId = StpUtil.getLoginIdAsLong();
        } catch (Exception ignored) {
        }

        // 查询回复列表
        Page<ActivityComment> replyPage = activityCommentService.getReplyList(
                commentReplyListRequest.getParentId(), pageNo, pageSize);

        // 转换为 VO
        Page<ActivityCommentVO> result = convertToCommentVO(replyPage, currentUserId);

        return ResultUtils.success(result);
    }

    /**
     * 点赞/取消点赞评论
     * @param commentToggleLikeRequest 点赞请求
     * @return 点赞总数
     */
    @SaCheckLogin
    @PostMapping("/comment/toggleLike")
    public BaseResponse<Long> toggleLike(@RequestBody CommentToggleLikeRequest commentToggleLikeRequest) {
        if (commentToggleLikeRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        if (commentToggleLikeRequest.getCommentId() == null || commentToggleLikeRequest.getCommentId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID不能为空");
        }

        long userId = StpUtil.getLoginIdAsLong();
        Long likeTotal = activityCommentService.toggleLike(commentToggleLikeRequest.getCommentId(), userId);
        return ResultUtils.success(likeTotal);
    }

    /**
     * 将 ActivityComment 实体转换为 ActivityCommentVO
     * @param commentPage 评论分页数据
     * @param currentUserId 当前登录用户ID
     * @return 评论VO分页数据
     */
    private Page<ActivityCommentVO> convertToCommentVO(Page<ActivityComment> commentPage, Long currentUserId) {
        Page<ActivityCommentVO> result = new Page<>(
                commentPage.getCurrent(),
                commentPage.getSize(),
                commentPage.getTotal()
        );

        List<ActivityCommentVO> voList = commentPage.getRecords().stream().map(comment -> {
            ActivityCommentVO vo = new ActivityCommentVO();
            BeanUtils.copyProperties(comment, vo);

            // 查询用户信息
            User user = userMapper.selectById(comment.getUserId());
            if (user != null) {
                vo.setUserName(user.getUsername());
                vo.setUserAvatarURL(user.getAvatarUrl());
            }

            // 查询评论内容（直接使用 commentId 作为主键查询）
            CommentContent commentContent = commentContentMapper.selectById(comment.getId());
            if (commentContent != null) {
                vo.setContent(commentContent.getContent());
            }

            // 查询当前用户是否已点赞（检查记录存在且未删除）
            if (currentUserId != null) {
                LambdaQueryWrapper<com.akai.findCompanions.model.domain.CommentLike> likeWrapper =
                    new LambdaQueryWrapper<>();
                likeWrapper.eq(com.akai.findCompanions.model.domain.CommentLike::getCommentId, comment.getId())
                        .eq(com.akai.findCompanions.model.domain.CommentLike::getUserId, currentUserId)
                        .eq(com.akai.findCompanions.model.domain.CommentLike::getIsDelete, 0);  // 只查询未删除的记录
                long count = commentLikeMapper.selectCount(likeWrapper);
                vo.setIsLiked(count > 0);
            } else {
                vo.setIsLiked(false);
            }

            return vo;
        }).collect(Collectors.toList());

        result.setRecords(voList);
        return result;
    }

}

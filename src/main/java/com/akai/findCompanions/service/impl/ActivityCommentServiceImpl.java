package com.akai.findCompanions.service.impl;

import cn.hutool.core.date.DateUtil;
import com.akai.findCompanions.common.ErrorCode;
import com.akai.findCompanions.enums.CommentLevelEnum;
import com.akai.findCompanions.enums.CommentStatusEnum;
import com.akai.findCompanions.enums.DeleteStatusEnum;
import com.akai.findCompanions.exception.BusinessException;
import com.akai.findCompanions.mapper.db.ActivityCommentMapper;
import com.akai.findCompanions.mapper.db.ActivityMapper;
import com.akai.findCompanions.mapper.db.CommentContentMapper;
import com.akai.findCompanions.mapper.db.CommentLikeMapper;
import com.akai.findCompanions.model.domain.Activity;
import com.akai.findCompanions.model.domain.ActivityComment;
import com.akai.findCompanions.model.domain.CommentContent;
import com.akai.findCompanions.model.domain.CommentLike;
import com.akai.findCompanions.service.IActivityCommentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 活动评论服务实现类
 * </p>
 *
 * @author Recursion
 * @since 2026-01-06
 */
@Slf4j
@Service
public class ActivityCommentServiceImpl extends ServiceImpl<ActivityCommentMapper, ActivityComment>
        implements IActivityCommentService {

    @Resource
    private ActivityCommentMapper activityCommentMapper;

    @Resource
    private CommentContentMapper commentContentMapper;

    @Resource
    private CommentLikeMapper commentLikeMapper;

    @Resource
    private ActivityMapper activityMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addComment(ActivityComment activityComment, String content, Long userId) {
        // 1. 校验参数
        if (activityComment == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论信息不能为空");
        }
        if (activityComment.getActivityId() == null || activityComment.getActivityId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "活动ID不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容不能为空");
        }
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }

        // 2. 查询活动是否存在
        Activity activity = activityMapper.selectById(activityComment.getActivityId());
        if (activity == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "活动不存在");
        }

        // 3. 设置评论信息
        activityComment.setUserId(userId);
        activityComment.setCreateTime(LocalDateTime.now());
        activityComment.setUpdateTime(LocalDateTime.now());
        activityComment.setStatus(CommentStatusEnum.NOT_DELETED.getValue()); // 正常状态

        // 如果是回复评论（有 parentId），设置为二级评论
        if (activityComment.getParentId() != null && activityComment.getParentId() > 0) {
            activityComment.setLevel(CommentLevelEnum.SECOND_LEVEL.getValue());
            activityComment.setReplyCommentId(activityComment.getParentId());

            // 更新父评论的回复总数
            ActivityComment parentComment = activityCommentMapper.selectById(activityComment.getParentId());
            if (parentComment == null) {
                throw new BusinessException(ErrorCode.NULL_ERROR, "父评论不存在");
            }
            parentComment.setReplyTotal(parentComment.getReplyTotal() + 1);
            activityCommentMapper.updateById(parentComment);
        } else {
            activityComment.setLevel(CommentLevelEnum.FIRST_LEVEL.getValue());
            activityComment.setReplyCommentId(0L);
        }

        // 4. 保存评论主表
        boolean saveResult = this.save(activityComment);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评论保存失败");
        }

        // 5. 保存评论内容
        CommentContent commentContent = new CommentContent();
        commentContent.setCommentId(activityComment.getId());  // 使用评论主表的ID作为主键
        commentContent.setActivityId(activityComment.getActivityId());
        commentContent.setContent(content.trim());
        // 使用当前年月作为分表标识
        String yearMonth = DateUtil.format(new Date(), "yyyy-MM");
        commentContent.setYearMonth(yearMonth);
        int contentResult = commentContentMapper.insert(commentContent);

        return contentResult > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long commentId, Long userId) {
        // 1. 校验参数
        if (commentId == null || commentId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID不能为空");
        }
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }

        // 2. 查询评论是否存在
        ActivityComment comment = activityCommentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "评论不存在");
        }

        // 3. 检查是否是评论者
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "只有评论者才能删除评论");
        }

        // 4. 逻辑删除评论
        comment.setStatus(CommentStatusEnum.DELETED.getValue()); // 设置为已删除
        comment.setUpdateTime(LocalDateTime.now());
        return activityCommentMapper.updateById(comment) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateComment(ActivityComment activityComment, String content, Long userId) {
        // 1. 校验参数
        if (activityComment == null || activityComment.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容不能为空");
        }
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }

        // 2. 查询原评论是否存在
        ActivityComment existingComment = activityCommentMapper.selectById(activityComment.getId());
        if (existingComment == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "评论不存在");
        }

        // 3. 检查是否是评论者
        if (!existingComment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "只有评论者才能修改评论");
        }

        // 4. 更新评论主表（只能更新图片等字段，不能修改用户ID、活动ID等）
        ActivityComment updateComment = new ActivityComment();
        updateComment.setId(activityComment.getId());
        updateComment.setImageURL(activityComment.getImageURL());
        updateComment.setUpdateTime(LocalDateTime.now());
        activityCommentMapper.updateById(updateComment);

        // 5. 更新评论内容
        // 直接使用 commentId（主键）查询评论内容
        CommentContent existingContent = commentContentMapper.selectById(existingComment.getId());

        if (existingContent != null) {
            existingContent.setContent(content.trim());
            return commentContentMapper.updateById(existingContent) > 0;
        }

        return false;
    }

    @Override
    public Page<ActivityComment> getCommentList(Long activityId, Long pageNo, Long pageSize) {
        // 1. 校验参数
        if (activityId == null || activityId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "活动ID不能为空");
        }
        if (pageNo == null || pageNo <= 0) {
            pageNo = 1L;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10L;
        }

        // 2. 分页查询一级评论
        Page<ActivityComment> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<ActivityComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityComment::getActivityId, activityId)
                .eq(ActivityComment::getLevel, CommentLevelEnum.FIRST_LEVEL.getValue()) // 只查询一级评论
                .eq(ActivityComment::getStatus, CommentStatusEnum.NOT_DELETED.getValue()) // 只查询正常状态的评论
                .orderByDesc(ActivityComment::getIsTop) // 置顶的在前
                .orderByDesc(ActivityComment::getCreateTime); // 按时间倒序

        return activityCommentMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Page<ActivityComment> getReplyList(Long parentId, Long pageNo, Long pageSize) {
        // 1. 校验参数
        if (parentId == null || parentId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "父评论ID不能为空");
        }
        if (pageNo == null || pageNo <= 0) {
            pageNo = 1L;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10L;
        }

        // 2. 分页查询二级评论
        Page<ActivityComment> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<ActivityComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityComment::getParentId, parentId)
                .eq(ActivityComment::getStatus, CommentStatusEnum.NOT_DELETED.getValue()) // 只查询正常状态的评论
                .orderByAsc(ActivityComment::getCreateTime); // 回复按时间正序

        return activityCommentMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long toggleLike(Long commentId, Long userId) {
        // 1. 校验参数
        if (commentId == null || commentId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID不能为空");
        }
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }

        // 2. 查询评论是否存在
        ActivityComment comment = activityCommentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "评论不存在");
        }

        // 3. 查询是否已点赞（使用原生 SQL 绕过 MyBatis-Plus 逻辑删除自动过滤）
        CommentLike existingLike = commentLikeMapper.findByUserAndCommentIgnoreDelete(userId, commentId);

        if (existingLike == null) {
            // 没有任何记录，插入新记录
            log.info("未找到任何记录，执行插入新记录操作 - userId: {}, commentId: {}", userId, commentId);
            CommentLike commentLike = new CommentLike();
            commentLike.setUserId(userId);
            commentLike.setCommentId(commentId);
            commentLike.setCreateTime(LocalDateTime.now());
            commentLike.setIsDelete(DeleteStatusEnum.NOT_DELETED.getValue());

            commentLikeMapper.insert(commentLike);
            comment.setLikeTotal(comment.getLikeTotal() + 1);
            log.info("插入成功，当前点赞数: {}", comment.getLikeTotal());
        } else {
            // 有记录，切换 isDelete 状态
            log.info("找到现有记录 - id: {}, isDelete: {}, userId: {}, commentId: {}",
                    existingLike.getId(), existingLike.getIsDelete(), existingLike.getUserId(), existingLike.getCommentId());

            if (existingLike.getIsDelete() == DeleteStatusEnum.NOT_DELETED.getValue()) {
                // 当前是未删除状态(0)，改为已删除(1) - 取消点赞
                log.info("执行取消点赞操作，isDelete: 0 -> 1");
                commentLikeMapper.unlike(existingLike.getUserId(), existingLike.getCommentId());
                comment.setLikeTotal(comment.getLikeTotal() - 1);
            } else {
                // 当前是已删除状态(1)，改为未删除(0) - 重新点赞
                log.info("执行恢复点赞操作，isDelete: 1 -> 0");
                commentLikeMapper.like(existingLike.getUserId(), existingLike.getCommentId());
                comment.setLikeTotal(comment.getLikeTotal() + 1);
            }
            log.info("更新成功，当前点赞数: {}", comment.getLikeTotal());
        }

        // 4. 更新评论的点赞总数
        activityCommentMapper.updateById(comment);

        return comment.getLikeTotal();
    }
}

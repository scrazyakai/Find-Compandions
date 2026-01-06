package com.akai.findCompanions.service;

import com.akai.findCompanions.model.domain.ActivityComment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 活动评论服务类
 * </p>
 *
 * @author Recursion
 * @since 2026-01-06
 */
public interface IActivityCommentService extends IService<ActivityComment> {

    /**
     * 添加评论（包含内容）
     * @param activityComment 评论主表信息
     * @param content 评论内容
     * @param userId 当前登录用户ID
     * @return 是否成功
     */
    boolean addComment(ActivityComment activityComment, String content, Long userId);

    /**
     * 删除评论（逻辑删除，仅评论者可删除）
     * @param commentId 评论ID
     * @param userId 当前登录用户ID
     * @return 是否成功
     */
    boolean deleteComment(Long commentId, Long userId);

    /**
     * 更新评论（仅评论者可修改）
     * @param activityComment 评论信息
     * @param content 评论内容
     * @param userId 当前登录用户ID
     * @return 是否成功
     */
    boolean updateComment(ActivityComment activityComment, String content, Long userId);

    /**
     * 分页查询活动的评论列表（一级评论）
     * @param activityId 活动ID
     * @param pageNo 页码
     * @param pageSize 页大小
     * @return 评论分页数据
     */
    Page<ActivityComment> getCommentList(Long activityId, Long pageNo, Long pageSize);

    /**
     * 查询评论的回复列表（二级评论）
     * @param parentId 父评论ID
     * @param pageNo 页码
     * @param pageSize 页大小
     * @return 回复分页数据
     */
    Page<ActivityComment> getReplyList(Long parentId, Long pageNo, Long pageSize);

    /**
     * 点赞/取消点赞评论
     * @param commentId 评论ID
     * @param userId 当前登录用户ID
     * @return 操作后的点赞总数
     */
    Long toggleLike(Long commentId, Long userId);
}

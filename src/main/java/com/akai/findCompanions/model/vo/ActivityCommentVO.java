package com.akai.findCompanions.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动评论VO
 *
 * @author Recursion
 * @since 2026-01-06
 */
@Data
public class ActivityCommentVO {
    /**
     * 评论ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像URL
     */
    private String userAvatarURL;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 父评论ID
     */
    private Long parentId;

    /**
     * 回复的评论ID
     */
    private Long replyCommentId;

    /**
     * 评论图片URL
     */
    private String imageURL;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 评论级别(1代表一级评论，2代表二级评论)
     */
    private Integer level;

    /**
     * 回复总数
     */
    private Long replyTotal;

    /**
     * 点赞总数
     */
    private Long likeTotal;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 当前用户是否已点赞
     */
    private Boolean isLiked;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

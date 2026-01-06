package com.akai.findCompanions.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 添加评论请求体
 *
 * @author Recursion
 * @since 2026-01-06
 */
@Data
public class CommentAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 父评论ID（一级评论为null，二级评论传入父评论ID）
     */
    private Long parentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论图片URL（可选）
     */
    private String imageURL;
}

package com.akai.findCompanions.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 点赞/取消点赞评论请求体
 *
 * @author Recursion
 * @since 2026-01-06
 */
@Data
public class CommentToggleLikeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    private Long commentId;
}

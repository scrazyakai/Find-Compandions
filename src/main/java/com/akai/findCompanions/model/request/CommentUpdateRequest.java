package com.akai.findCompanions.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新评论请求体
 *
 * @author Recursion
 * @since 2026-01-06
 */
@Data
public class CommentUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    private Long commentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论图片URL（可选）
     */
    private String imageURL;
}

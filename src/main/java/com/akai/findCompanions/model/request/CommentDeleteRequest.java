package com.akai.findCompanions.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除评论请求体
 *
 * @author Recursion
 * @since 2026-01-06
 */
@Data
public class CommentDeleteRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    private Long commentId;
}

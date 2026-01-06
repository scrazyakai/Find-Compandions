package com.akai.findCompanions.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询评论列表请求体
 *
 * @author Recursion
 * @since 2026-01-06
 */
@Data
public class CommentListRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 页码
     */
    private Integer pageNo;

    /**
     * 页大小
     */
    private Integer pageSize;
}

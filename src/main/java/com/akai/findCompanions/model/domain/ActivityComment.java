package com.akai.findCompanions.model.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author Recursion
 * @since 2026-01-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("activity_comment")
public class ActivityComment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 活动Id
     */
    private Long activityId;

    /**
     * 父评论Id
     */
    private Long parentId;

    /**
     * 回复的评论Id(0代表活动)
     */
    private Long replyCommentId;

    /**
     * 带图评论
     */
    private String imageURL;
    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 评论级别(1代表一级标题，2代表二级标题)
     */
    private Integer level;

    /**
     * 回复总数，仅一级评论
     */
    private Long replyTotal;

    /**
     * 点赞总数
     */
    private Long likeTotal;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    private Integer status;


}

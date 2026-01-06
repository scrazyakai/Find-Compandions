package com.akai.findCompanions.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 评论内容表
 * </p>
 *
 * @author Recursion
 * @since 2026-01-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("comment_content")
public class CommentContent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID（对应 activity_comment.id）
     */
    @TableId(value = "commentId", type = IdType.NONE)
    @TableField("commentId")
    private Long commentId;

    /**
     * 活动id
     */
    @TableField("activityId")
    private Long activityId;

    /**
     * 评论内容
     */
    @TableField("content")
    private String content;

    /**
     * 年月（用于分表）
     */
    @TableField("yearMonth")
    private String yearMonth;

}

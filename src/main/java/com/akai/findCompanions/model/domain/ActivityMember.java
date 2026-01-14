package com.akai.findCompanions.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 活动成员实体类
 * 
 * @author Kai
 * @since 2026-01-06
 */
@Data
@TableName("activity_member")
public class ActivityMember implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    @TableField("userId")
    private Long userId;
    
    /**
     * 活动ID
     */
    @TableField("activityId")
    private Long activityId;
    
    /**
     * 加入时间
     */
    @TableField("joinTime")
    private Date joinTime;
    
    /**
     * 加入状态(0-待审核,1-已加入,2-已退出)
     */
    @TableField("status")
    private Integer status;
    
    /**
     * 创建时间
     */
    @TableField("createTime")
    private Date createTime;
    
    /**
     * 更新时间
     */
    @TableField("updateTime")
    private Date updateTime;
    
    /**
     * 是否删除(0-未删除,1-已删除)
     */
    @TableLogic
    private Integer isDelete;
}

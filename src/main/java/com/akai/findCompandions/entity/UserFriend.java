package com.akai.findCompandions.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 凯哥
 * @since 2025-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_friend")
public class UserFriend implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("userId")
    private Long userId;

    /**
     * 好友ID
     */
    @TableField("friendId")
    private Long friendId;

    /**
     * 通过状态  0待处理，1拒绝，2通过
     */
    @TableField("status")
    private Integer status;

    /**
     * 申请时间
     */
    @TableField("createTime")
    private Date createTime;

    /**
     * 处理时间
     */
    @TableField("updateTime")
    private Date updateTime;


}

package com.akai.findCompanions.model.domain;

import com.akai.findCompanions.config.CustomDateDeserializer;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
 * @author Recursion
 * @since 2025-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("activity")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动id
     */
    @TableId(value = "activityId", type = IdType.AUTO)
    private Long activityId;

    /**
     * 活动描述
     */
    private String activityDesc;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date endTime;

    /**
     * 活动状态(1未开始，2正在进行中，3已结束，4已取消)
     */
    private Integer status;

    /**
     * 封面
     */
    private String coverURL;

    /**
     * 最大活动人数
     */
    private Integer maxMemberNumber;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date updateTime;

    /**
     * 创建者Id
     */
    private Long ownerId;

    /**
     * 用户头像URL
     */
    private String userAvatarURL;

    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 当前参与人数
     */
    private Integer currentMemberNumber;

}

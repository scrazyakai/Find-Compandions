package com.akai.findCompanions.model.vo;


import lombok.Data;

import java.util.Date;

@Data
public class ActivityVO {

    /**
     * 活动描述
     */
    private String activityDesc;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
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
    private Date createTime;

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
}

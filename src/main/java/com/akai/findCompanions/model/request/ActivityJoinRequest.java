package com.akai.findCompanions.model.request;


import lombok.Data;

import java.io.Serializable;

/**
 * 用户参与活动请求体
 *
 */
@Data
public class ActivityJoinRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 活动ID
     */
    private Long activityId;

}

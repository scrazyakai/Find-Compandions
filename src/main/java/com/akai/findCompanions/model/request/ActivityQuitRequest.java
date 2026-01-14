package com.akai.findCompanions.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出活动请求体
 *
 */
@Data
public class ActivityQuitRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 活动ID
     */
    private Long activityId;

}

package com.akai.findCompanions.model.dto.sync;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * User incremental sync message.
 */
@Data
public class UserSyncMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** INSERT / UPDATE / DELETE */
    private String operationType;

    private Long id;

    private String username;

    private String userAccount;

    private String avatarUrl;

    private Integer userStatus;

    private String tags;

    private String profile;

    private Integer isDelete;

    private Integer retryTimes;

    private Date eventTime;
}

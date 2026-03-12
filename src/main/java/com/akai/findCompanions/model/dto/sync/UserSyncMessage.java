package com.akai.findCompanions.model.dto.sync;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户增量同步消息
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

    /** DB里逗号分隔的tags */
    private String tags;

    private String profile;

    /** 逻辑删除标记 */
    private Integer isDelete;

    private Date eventTime;
}

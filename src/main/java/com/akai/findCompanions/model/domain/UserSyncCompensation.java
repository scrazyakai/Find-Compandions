package com.akai.findCompanions.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("user_sync_compensation")
public class UserSyncCompensation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String operationType;

    private Integer retryTimes;

    private Integer maxRetryTimes;

    /**
     * 0 pending, 1 success, 2 failed
     */
    private Integer status;

    private String errorMessage;

    private String payload;

    private Date createTime;

    private Date updateTime;
}

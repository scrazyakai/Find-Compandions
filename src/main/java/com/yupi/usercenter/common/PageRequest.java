package com.yupi.usercenter.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 4886004727262210680L;
    /**
     * 分页大小
     */
    int pageSize = 10;
    /**
     * 当前第几页
     */
    int pageNum = 1;
}

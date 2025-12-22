package com.akai.findCompanions.model.domain.Es;

import lombok.Data;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.Analyzer;
import org.dromara.easyes.annotation.rely.FieldType;

import java.util.List;

@Data
@IndexName(value = "user_index")
public class UserDocument {
    /**
     * 唯一ID
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private Long id;
    /**
     * 用户昵称
     */

    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_SMART)
    private String username; // 模糊搜索

    /**
     * 账号
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String userAccount;// 精确查询

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 状态 0 - 正常
     */
    @IndexField(fieldType = FieldType.INTEGER)
    private Integer userStatus;


    /**
     * 标签列表
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private List<String> tags;
    /**
     * 简介
     */

    private String profile;

}

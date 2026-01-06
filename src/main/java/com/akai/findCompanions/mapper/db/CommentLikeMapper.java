package com.akai.findCompanions.mapper.db;

import com.akai.findCompanions.model.domain.CommentLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 评论点赞表 Mapper 接口
 * </p>
 *
 * @author Recursion
 * @since 2026-01-06
 */
public interface CommentLikeMapper extends BaseMapper<CommentLike> {

    /**
     * 根据用户ID和评论ID查询点赞记录（忽略逻辑删除）
     * 使用原生 SQL 绕过 MyBatis-Plus 的逻辑删除自动过滤
     *
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 点赞记录
     */
    CommentLike findByUserAndCommentIgnoreDelete(@Param("userId") Long userId, @Param("commentId") Long commentId);

    /**
     * 点赞（设置 isDelete = 0）
     * 使用原生 SQL 更新，绕过 MyBatis-Plus 逻辑删除
     *
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 影响行数
     */
    int like(@Param("userId") Long userId, @Param("commentId") Long commentId);

    /**
     * 取消点赞（设置 isDelete = 1）
     * 使用原生 SQL 更新，绕过 MyBatis-Plus 逻辑删除
     *
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 影响行数
     */
    int unlike(@Param("userId") Long userId, @Param("commentId") Long commentId);
}

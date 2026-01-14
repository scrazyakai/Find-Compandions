package com.akai.findCompanions.service;

import com.akai.findCompanions.model.domain.Activity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Recursion
 * @since 2025-12-20
 */
public interface IActivityService extends IService<Activity> {

    /**
     * 取消活动（仅拥有者可取消）
     * @param activityId 活动ID
     * @param userId 当前登录用户ID
     * @return 是否成功
     */
    boolean cancelActivity(Long activityId, Long userId);

    /**
     * 更新活动（仅拥有者可修改）
     * @param activity 活动信息
     * @param userId 当前登录用户ID
     * @return 是否成功
     */
    boolean updateActivity(Activity activity, Long userId);

    /**
     * 用户参与活动
     * @param activityId 活动ID
     * @param userId 当前登录用户ID
     * @return 是否成功
     */
    boolean joinActivity(Long activityId, Long userId);

    /**
     * 用户退出活动
     * @param activityId 活动ID
     * @param userId 当前登录用户ID
     * @return 是否成功
     */
    boolean quitActivity(Long activityId, Long userId);

    boolean participantStatus(Long activityId);
}

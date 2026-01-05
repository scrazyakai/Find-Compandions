package com.akai.findCompanions.service.impl;

import com.akai.findCompanions.common.ErrorCode;
import com.akai.findCompanions.enums.ActivityStatusEnum;
import com.akai.findCompanions.exception.BusinessException;
import com.akai.findCompanions.model.domain.Activity;
import com.akai.findCompanions.mapper.db.ActivityMapper;
import com.akai.findCompanions.service.IActivityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Recursion
 * @since 2025-12-20
 */
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements IActivityService {

    @Override
    public boolean cancelActivity(Long activityId, Long userId) {
        // 1. 校验参数
        if (activityId == null || activityId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "活动ID不能为空");
        }
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }

        // 2. 查询活动是否存在
        Activity activity = this.getById(activityId);
        if (activity == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "活动不存在");
        }

        // 3. 检查是否是活动拥有者
        if (!activity.getOwnerId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "只有活动创建者才能取消活动");
        }
        // 4. 检查活动是否已经取消/结束
        if(activity.getStatus() == ActivityStatusEnum.CANCELLED.getValue()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "活动已经取消");
        }
        if(activity.getStatus() == ActivityStatusEnum.ENDED.getValue()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "活动已经结束");
        }
        // 5. 取消活动
        Activity oldActivity = this.getById(activityId);
        Activity newActivity = new Activity();
        if(oldActivity != null){
            BeanUtils.copyProperties(oldActivity, newActivity);
        }
        newActivity.setStatus(ActivityStatusEnum.CANCELLED.getValue());
        return this.updateById(newActivity);
    }

    @Override
    public boolean updateActivity(Activity activity, Long userId) {
        // 1. 校验参数
        if (activity == null || activity.getActivityId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "活动ID不能为空");
        }
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }

        // 2. 查询原活动是否存在
        Activity existingActivity = this.getById(activity.getActivityId());
        if (existingActivity == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "活动不存在");
        }

        // 3. 检查是否是活动拥有者
        if (!existingActivity.getOwnerId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "只有活动创建者才能修改活动");
        }

        // 4. 更新活动
        return this.updateById(activity);
    }
}

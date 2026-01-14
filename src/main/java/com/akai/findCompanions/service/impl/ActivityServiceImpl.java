package com.akai.findCompanions.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.akai.findCompanions.common.ErrorCode;
import com.akai.findCompanions.common.ResultUtils;
import com.akai.findCompanions.enums.ActivityParticipantEnum;
import com.akai.findCompanions.enums.ActivityStatusEnum;
import com.akai.findCompanions.exception.BusinessException;
import com.akai.findCompanions.model.domain.Activity;
import com.akai.findCompanions.model.domain.ActivityMember;
import com.akai.findCompanions.mapper.db.ActivityMapper;
import com.akai.findCompanions.mapper.db.ActivityMemberMapper;
import com.akai.findCompanions.service.IActivityService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

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

    @Resource
    private ActivityMemberMapper activityMemberMapper;

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

    @Override
    public boolean joinActivity(Long activityId, Long userId) {
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

        // 3. 检查活动状态
        if (activity.getStatus() == ActivityStatusEnum.CANCELLED.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "活动已取消，无法参与");
        }
        if (activity.getStatus() == ActivityStatusEnum.ENDED.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "活动已结束，无法参与");
        }

        // 4. 检查用户是否已经在活动中
        LambdaQueryWrapper<ActivityMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityMember::getActivityId, activityId)
                .eq(ActivityMember::getUserId, userId);
        ActivityMember existingMember = activityMemberMapper.selectOne(queryWrapper);
        if (existingMember != null) {
            if (existingMember.getStatus() == ActivityParticipantEnum.JOINED.getValue()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "您已经参与该活动");
            } else if (existingMember.getStatus() == ActivityParticipantEnum.QUITED.getValue()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "您已经退出该活动,不能频繁加入");
            }
        }

        // 5. 检查活动人数是否已满
        int currentMemberNumber = activity.getCurrentMemberNumber() != null ? activity.getCurrentMemberNumber() : 0;
        if (activity.getMaxMemberNumber() != null && currentMemberNumber >= activity.getMaxMemberNumber()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "活动人数已满");
        }

        // 6. 创建活动成员记录
        ActivityMember activityMember = new ActivityMember();
        activityMember.setUserId(userId);
        activityMember.setActivityId(activityId);
        activityMember.setStatus(ActivityParticipantEnum.JOINED.getValue());
        activityMember.setCreateTime(new Date());
        activityMember.setUpdateTime(new Date());
        boolean result = activityMemberMapper.insert(activityMember) > 0;

        // 7. 更新活动当前参与人数
        if (result) {
            Activity updateActivity = new Activity();
            updateActivity.setActivityId(activityId);
            updateActivity.setCurrentMemberNumber(currentMemberNumber + 1);
            this.updateById(updateActivity);
        }

        return result;
    }

    @Override
    public boolean quitActivity(Long activityId, Long userId) {
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

        // 3. 查询用户是否在该活动中
        LambdaQueryWrapper<ActivityMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityMember::getActivityId, activityId)
                .eq(ActivityMember::getUserId, userId);
        ActivityMember activityMember = activityMemberMapper.selectOne(queryWrapper);
        if (activityMember == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您未参与该活动");
        }

        // 4. 检查用户状态
        if (activityMember.getStatus() == ActivityParticipantEnum.QUITED.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您已经退出该活动");
        }

        // 5. 更新成员状态为已退出
        ActivityMember updateMember = new ActivityMember();
        updateMember.setId(activityMember.getId());
        updateMember.setStatus(ActivityParticipantEnum.QUITED.getValue()); // 已退出
        updateMember.setUpdateTime(new Date());
        boolean result = activityMemberMapper.updateById(updateMember) > 0;

        // 6. 更新活动当前参与人数
        if (result) {
            int currentMemberNumber = activity.getCurrentMemberNumber() != null ? activity.getCurrentMemberNumber() : 0;
            Activity updateActivity = new Activity();
            updateActivity.setActivityId(activityId);
            updateActivity.setCurrentMemberNumber(Math.max(0, currentMemberNumber - 1));
            this.updateById(updateActivity);
        }

        return result;
    }

    @Override
    public boolean participantStatus(Long activityId) {
        if(activityId == null || activityId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"activityId非法");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        QueryWrapper<ActivityMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("activityId", activityId);
        ActivityMember activityMember = activityMemberMapper.selectOne(queryWrapper);
        if(activityMember == null){
            return false;
        }
        return activityMember.getStatus() == ActivityParticipantEnum.JOINED.getValue();
    }
}

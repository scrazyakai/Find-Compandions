package com.akai.findCompanions.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.akai.findCompanions.common.BaseResponse;
import com.akai.findCompanions.common.ErrorCode;
import com.akai.findCompanions.common.ResultUtils;
import com.akai.findCompanions.model.domain.Activity;
import com.akai.findCompanions.exception.BusinessException;
import com.akai.findCompanions.mapper.db.ActivityMapper;
import com.akai.findCompanions.model.vo.ActivityVO;
import com.akai.findCompanions.service.IActivityService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Recursion
 * @since 2025-12-20
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {
    @Resource
    private IActivityService activityService;
    @Resource
    private ActivityMapper activityMapper;
    private final int pageSize = 10;
    @PostMapping("/add")
    public BaseResponse<Boolean> add(@RequestBody Activity activity) {
        if (activity == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "活动参数不能为空");
        }
        boolean result = activityService.save(activity);
        return ResultUtils.success(result);
    }

    @SaCheckLogin
    @PostMapping("/update")
    public BaseResponse<Boolean> update(@RequestBody Activity activity) {
        if (activity == null || activity.getActivityId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "活动ID不能为空");
        }
        // 获取当前登录用户ID
        long userId = StpUtil.getLoginIdAsLong();
        boolean result = activityService.updateActivity(activity, userId);
        return ResultUtils.success(result);
    }

    @SaCheckLogin
    @PostMapping("/cancel")
    public BaseResponse<Boolean> delete(Long activityId) {
        if (activityId == null || activityId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "活动ID不能为空");
        }
        // 获取当前登录用户ID
        long userId = StpUtil.getLoginIdAsLong();
        boolean result = activityService.cancelActivity(activityId, userId);
        return ResultUtils.success(result);
    }

    @SaCheckLogin
    @PostMapping("/list")
    public BaseResponse<Page<ActivityVO>> list(@RequestParam(defaultValue = "1") int pageNo) {
        if(pageNo <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"起始页参数异常");
        }
        Page<Activity> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<Activity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Activity::getCreateTime);
        Page<Activity> pages = activityService.page(page, queryWrapper);
        Page<ActivityVO> result = new Page<>(
                pages.getCurrent(),
                pages.getSize(),
                pages.getTotal()
        );
        List<ActivityVO> collect = pages.getRecords()
                .stream().map(activity -> {
            ActivityVO activityVO = new ActivityVO();
            BeanUtils.copyProperties(activity, activityVO);
            return activityVO;
        }).collect(Collectors.toList());
        BeanUtils.copyProperties(pages,result);
        result.setRecords(collect);
        return ResultUtils.success(result);
    }

}

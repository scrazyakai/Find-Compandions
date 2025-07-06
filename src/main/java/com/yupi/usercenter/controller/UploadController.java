package com.yupi.usercenter.controller;

import com.yupi.usercenter.common.BaseResponse;
import com.yupi.usercenter.common.ErrorCode;
import com.yupi.usercenter.common.ResultUtils;
import com.yupi.usercenter.exception.BusinessException;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.service.IUserService;
import com.yupi.usercenter.utils.AliyunOssUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController()
@RequestMapping("/upload")
@CrossOrigin(origins = {"http://localhost:3000"})
public class UploadController {
    @Resource
    IUserService userService;
    @Resource
    AliyunOssUtils aliyunOssUtils;
    @PostMapping("/image")
    public BaseResponse<String> uploadImage(@RequestParam("file")MultipartFile file, HttpServletRequest request) throws IOException {
        if(file == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getUserLogin(request);
        String url = aliyunOssUtils.upload(file);
        User user = userService.getById(loginUser.getId());
        user.setAvatarUrl(url);
        userService.updateById(user);
        return ResultUtils.success(url);
    }
}

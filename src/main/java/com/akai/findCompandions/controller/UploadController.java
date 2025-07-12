package com.akai.findCompandions.controller;

import com.akai.findCompandions.common.BaseResponse;
import com.akai.findCompandions.common.ErrorCode;
import com.akai.findCompandions.common.ResultUtils;
import com.akai.findCompandions.exception.BusinessException;
import com.akai.findCompandions.model.domain.User;
import com.akai.findCompandions.service.IUserService;
import com.akai.findCompandions.utils.AliyunOssUtils;
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

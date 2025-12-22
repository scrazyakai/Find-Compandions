package com.akai.findCompanions.controller;

import com.akai.findCompanions.common.BaseResponse;
import com.akai.findCompanions.common.ErrorCode;
import com.akai.findCompanions.common.ResultUtils;
import com.akai.findCompanions.exception.BusinessException;
import com.akai.findCompanions.model.domain.User;
import com.akai.findCompanions.service.IUserService;
import com.akai.findCompanions.utils.AliyunOssUtils;
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

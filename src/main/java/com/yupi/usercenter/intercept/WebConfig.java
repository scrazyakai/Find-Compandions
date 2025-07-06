package com.yupi.usercenter.intercept;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {
    @Autowired
    LoginInterceptor loginInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns(
                        // 注意: 用户登录接口
                        // 因为控制器已经定义了@RequestMapping("/user")
                        // 所以完整路径是/api/user/login，但在拦截器配置中要去掉/api
                        "/user/login",
                        "/user/register",
                        "/user/registerAndLogin"

                        // Knife4j路径 - 去掉/api前缀
//                        "/doc.html",
//                        "/webjars/**",
//                        "/swagger-resources/**",
//                        "/v3/api-docs/**",
//                        "/swagger-ui/**"
                )
                .addPathPatterns("/**");
    }
}

package com.akai.findCompandions.intercept;

import com.akai.findCompandions.model.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.akai.findCompandions.contant.UserConstant.USER_LOGIN_STATE;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        //判断token是否为空
        if (user == null) {
            // 返回 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            //设置响应类型为json
            response.setContentType("application/json;charset=UTF-8");
            //写出响应
            response.getWriter().write("{\"code\":401, \"msg\":\"未登录或 Token 缺失\"}");
            return false;//拦截请求
        }
        //放行
        return true;
    }

}
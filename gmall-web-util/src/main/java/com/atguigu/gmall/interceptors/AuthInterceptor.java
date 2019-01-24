package com.atguigu.gmall.interceptors;

import com.atguigu.gmall.annotations.LoginRequired;
import com.atguigu.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandle;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 注解判断
        HandlerMethod hm = (HandlerMethod) handler;

        LoginRequired methodAnnotation = hm.getMethodAnnotation(LoginRequired.class);


        String token = request.getParameter("newToken");

        if (methodAnnotation != null) {
            // 校验
            boolean loginCheck = false;
            if(StringUtils.isNotBlank(token)){
                loginCheck =true;
            }

            // 校验不通过，并且必须登陆
            if(loginCheck==false&&methodAnnotation.isNeedLogin()==true){
                response.sendRedirect("http://passport.gmall.com:8085/index?returnUrl="+request.getRequestURL());
                return false;
            }

        }
        return true;

    }
}

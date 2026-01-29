// src/main/java/com/huangbin/smartcommunityelderlycare/config/WebConfig.java
package com.huangbin.smartcommunityelderlycare.config;

import com.huangbin.smartcommunityelderlycare.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")  // 拦截所有/api/开头的请求
                .excludePathPatterns("/api/user/login", "/api/user/register"); // 排除登录注册
    }
}
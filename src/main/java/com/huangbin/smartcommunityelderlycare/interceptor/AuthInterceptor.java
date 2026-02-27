// src/main/java/com/huangbin/smartcommunityelderlycare/interceptor/AuthInterceptor.java
package com.huangbin.smartcommunityelderlycare.interceptor;

import com.huangbin.smartcommunityelderlycare.entity.User;
import com.huangbin.smartcommunityelderlycare.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    // 公开接口，不需要登录
    private final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/user/login",
            "/api/user/register",
            "/api/user/test",
            "/api/health/test",
            "/api/service/test",
            "/api/order/test"
    );

    // 管理员专用接口
    private final List<String> ADMIN_PATHS = Arrays.asList(
            "/api/admin/",
            "/api/user/all",
            "/api/order/all",
            "/api/notices/all",
            "/api/warning/check-signin"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // 1. 检查是否为公开接口
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            return true;
        }

        // 2. 检查用户是否登录（通过Session或token）
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"请先登录\"}");
            return false;
        }

        // 3. 获取用户信息
        User user = userService.getUserById(userId);
        if (user == null || user.getStatus() != 1) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"用户不存在或已禁用\"}");
            return false;
        }

        // 4. 检查管理员权限
        if (ADMIN_PATHS.stream().anyMatch(path::startsWith)) {
            if (!"admin".equals(user.getRole())) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"code\":403,\"message\":\"无权限访问\"}");
                return false;
            }
        }

        // 5. 将用户信息存入request
        request.setAttribute("userId", userId);
        request.setAttribute("userRole", user.getRole());

        return true;
    }

    /**
     * 从请求中获取用户ID（这里需要根据你的认证方式实现）
     * 如果是Session认证，从Session获取
     * 如果是Token认证，解析Token
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        // 从Header获取token（这里简单处理，实际应为JWT解析）
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                return Long.parseLong(token);  // 暂时直接解析为userId
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
package com.huangbin.smartcommunityelderlycare.controller;

import com.huangbin.smartcommunityelderlycare.common.Result;
import com.huangbin.smartcommunityelderlycare.entity.User;
import com.huangbin.smartcommunityelderlycare.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            // 不返回密码
            registeredUser.setPassword(null);
            return Result.success("注册成功", registeredUser);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    // 在UserController的login方法中，添加Session或返回token
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        try {
            String phone = loginData.get("phone");
            String password = loginData.get("password");

            User user = userService.login(phone, password);
            user.setPassword(null);

            // 创建返回数据，包含用户信息和简单token（用户ID）
            Map<String, Object> result = new HashMap<>();
            result.put("user", user);
            result.put("token", user.getUserId().toString()); // 简单token，实际应该用JWT

            // 如果需要Session认证
            // request.getSession().setAttribute("userId", user.getUserId());

            return Result.success("登录成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取所有用户
     */
    @GetMapping("/all")
    public Result<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            // 隐藏密码
            users.forEach(user -> user.setPassword(null));
            return Result.success(users);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据角色获取用户
     */
    @GetMapping("/role/{role}")
    public Result<List<User>> getUsersByRole(@PathVariable("role") String role) {
        try {
            List<User> users = userService.getUsersByRole(role);
            users.forEach(user -> user.setPassword(null));
            return Result.success(users);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable("id") Long id) {
        try {
            User user = userService.getUserById(id);
            user.setPassword(null);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 测试接口
     */
    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("用户管理接口正常");
    }
}
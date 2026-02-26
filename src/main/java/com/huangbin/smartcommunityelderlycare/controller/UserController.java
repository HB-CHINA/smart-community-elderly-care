package com.huangbin.smartcommunityelderlycare.controller;

import com.huangbin.smartcommunityelderlycare.common.Result;
import com.huangbin.smartcommunityelderlycare.dto.RegisterDTO;
import com.huangbin.smartcommunityelderlycare.entity.User;
import com.huangbin.smartcommunityelderlycare.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public Result<User> register(@Valid @RequestBody RegisterDTO registerDTO) {
        try {
            User user = new User();
            user.setPhone(registerDTO.getPhone());
            user.setPassword(registerDTO.getPassword());
            user.setName(registerDTO.getName());
            user.setRole(registerDTO.getRole());
            user.setEmergencyContact(registerDTO.getEmergencyContact());

            User registeredUser = userService.register(user);
            registeredUser.setPassword(null);
            return Result.success("注册成功", registeredUser);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        try {
            String phone = loginData.get("phone");
            String password = loginData.get("password");

            User user = userService.login(phone, password);
            user.setPassword(null);

            Map<String, Object> result = new HashMap<>();
            result.put("user", user);
            result.put("token", user.getUserId().toString());

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
     * 头像上传
     */
    @PostMapping("/upload/avatar")
    public Result<String> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestAttribute("userId") Long userId) {
        try {
            // 校验文件是否为空
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            // 校验文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return Result.error("文件名无效");
            }

            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            if (!".jpg".equalsIgnoreCase(suffix) && !".png".equalsIgnoreCase(suffix)
                    && !".jpeg".equalsIgnoreCase(suffix)) {
                return Result.error("仅支持jpg/png/jpeg格式");
            }

            // 校验文件大小（2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return Result.error("文件大小不能超过2MB");
            }

            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + suffix;

            // 使用绝对路径，确保目录存在
            String uploadPath = System.getProperty("user.dir") + "/upload/avatar/";
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    return Result.error("创建上传目录失败");
                }
            }

            // 保存文件
            String fullPath = uploadPath + fileName;
            file.transferTo(new File(fullPath));

            // 更新用户头像路径（返回相对路径给前端）
            String relativePath = "/upload/avatar/" + fileName;
            userService.updateAvatar(userId, relativePath);

            return Result.success("上传成功", relativePath);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件保存失败: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败: " + e.getMessage());
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
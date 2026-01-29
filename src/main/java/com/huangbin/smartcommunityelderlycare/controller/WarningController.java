// src/main/java/com/huangbin/smartcommunityelderlycare/controller/WarningController.java
package com.huangbin.smartcommunityelderlycare.controller;

import com.huangbin.smartcommunityelderlycare.common.Result;
import com.huangbin.smartcommunityelderlycare.service.WarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warning")
@CrossOrigin
public class WarningController {

    @Autowired
    private WarningService warningService;

    /**
     * 手动触发签到缺失检查（管理员用）
     */
    @PostMapping("/check-signin")
    public Result<Integer> manualCheckSignIn() {
        try {
            int warningCount = warningService.manualCheckSignIn();
            return Result.success("签到缺失检查完成", warningCount);
        } catch (Exception e) {
            return Result.error("检查失败: " + e.getMessage());
        }
    }

    /**
     * 检查指定老人今日签到状态
     */
    @GetMapping("/signin-status/{elderId}")
    public Result<Boolean> getTodaySignInStatus(@PathVariable Long elderId) {
        try {
            boolean hasSignedIn = warningService.checkTodaySignInStatus(elderId);
            return Result.success("查询成功", hasSignedIn);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}
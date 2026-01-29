package com.huangbin.smartcommunityelderlycare.controller;

import com.huangbin.smartcommunityelderlycare.common.Result;
import com.huangbin.smartcommunityelderlycare.entity.CheckIn;
import com.huangbin.smartcommunityelderlycare.service.CheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkin")
@CrossOrigin
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    // 原有的方法保持不变...

    // ============ 新增统计接口 ============

    /**
     * 获取今日签到数量
     */
    @GetMapping("/today-count")
    public Result<Long> getTodayCheckinsCount() {
        try {
            Long count = checkInService.getTodayCheckinsCount();
            return Result.success("查询成功", count);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 统计指定日期范围的签到数据（用于图表）
     */
    @GetMapping("/stats/by-date")
    public Result<List<Map<String, Object>>> getCheckinsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<Map<String, Object>> stats = checkInService.getCheckinsByDateRange(startDate, endDate);
            return Result.success("查询成功", stats);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取签到活跃用户排名
     */
    @GetMapping("/stats/active-users")
    public Result<List<Map<String, Object>>> getActiveUsers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<Map<String, Object>> ranking = checkInService.getCheckinRanking(startDate, endDate);
            return Result.success("查询成功", ranking);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取过去N天内签到过的独立用户数
     */
    @GetMapping("/stats/distinct-users")
    public Result<Long> getDistinctUsersCheckinAfter(
            @RequestParam Integer days) {
        try {
            LocalDate date = LocalDate.now().minusDays(days);
            Long count = checkInService.countDistinctUsersCheckinAfter(date);
            return Result.success("查询成功", count);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}
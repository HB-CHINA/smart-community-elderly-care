package com.huangbin.smartcommunityelderlycare.controller;

import com.huangbin.smartcommunityelderlycare.common.Result;
import com.huangbin.smartcommunityelderlycare.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取每日签到统计
     */
    @GetMapping("/daily-checkins")
    public Result<Map<String, Object>> getDailyCheckins(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Map<String, Object> data = statisticsService.getDailyCheckinStats(startDate, endDate);
            return Result.success("查询成功", data);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取订单统计
     */
    @GetMapping("/orders")
    public Result<Map<String, Object>> getOrderStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Map<String, Object> data = statisticsService.getOrderStatistics(startDate, endDate);
            return Result.success("查询成功", data);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取服务分布
     */
    @GetMapping("/service-distribution")
    public Result<Map<String, Object>> getServiceDistribution() {
        try {
            Map<String, Object> data = statisticsService.getServiceDistribution();
            return Result.success("查询成功", data);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取健康趋势
     */
    @GetMapping("/health-trend/{elderId}")
    public Result<Map<String, Object>> getHealthTrend(
            @PathVariable Long elderId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Map<String, Object> data = statisticsService.getHealthTrend(elderId, startDate, endDate);
            return Result.success("查询成功", data);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取系统概览
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getSystemOverview() {
        try {
            Map<String, Object> data = statisticsService.getSystemOverview();
            return Result.success("查询成功", data);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }


    /**
     * 获取仪表盘实时数据
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> data = statisticsService.getDashboardStats();
            return Result.success("仪表盘数据获取成功", data);
        } catch (Exception e) {
            return Result.error("获取仪表盘数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取趋势分析数据
     */
    @GetMapping("/trend/analysis")
    public Result<Map<String, Object>> getTrendAnalysis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "CHECKIN") String trendType) {
        try {
            Map<String, Object> data = statisticsService.getTrendData(startDate, endDate, trendType);
            return Result.success("趋势分析数据获取成功", data);
        } catch (Exception e) {
            return Result.error("获取趋势分析数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取对比分析数据
     */
    @GetMapping("/comparison")
    public Result<Map<String, Object>> getComparisonAnalysis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date1,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date2) {
        try {
            Map<String, Object> data = statisticsService.getComparisonData(date1, date2);
            return Result.success("对比分析数据获取成功", data);
        } catch (Exception e) {
            return Result.error("获取对比分析数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取健康数据统计概览
     */
    @GetMapping("/health/stats")
    public Result<Map<String, Object>> getHealthStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Map<String, Object> data = statisticsService.getHealthStatistics(startDate, endDate);
            return Result.success("健康统计数据获取成功", data);
        } catch (Exception e) {
            return Result.error("获取健康统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取财务数据统计
     */
    @GetMapping("/financial/stats")
    public Result<Map<String, Object>> getFinancialStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Map<String, Object> data = statisticsService.getFinancialStatistics(startDate, endDate);
            return Result.success("财务统计数据获取成功", data);
        } catch (Exception e) {
            return Result.error("获取财务统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取系统详细统计
     */
    @GetMapping("/system/stats")
    public Result<Map<String, Object>> getSystemStatistics() {
        try {
            Map<String, Object> data = statisticsService.getSystemStatistics();
            return Result.success("系统详细统计数据获取成功", data);
        } catch (Exception e) {
            return Result.error("获取系统统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取报表概览
     */
    @GetMapping("/reports/overview")
    public Result<Map<String, Object>> getReportOverview() {
        try {
            Map<String, Object> data = statisticsService.getReportOverview();
            return Result.success("报表概览获取成功", data);
        } catch (Exception e) {
            return Result.error("获取报表概览失败: " + e.getMessage());
        }
    }

    /**
     * 批量生成历史报表
     */
    @PostMapping("/reports/generate/batch")
    public Result<String> generateReportsBatch(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            statisticsService.generateReportsForDateRange(startDate, endDate);
            return Result.success("批量生成报表成功",
                    String.format("已生成%s至%s的统计报表", startDate, endDate));
        } catch (Exception e) {
            return Result.error("批量生成报表失败: " + e.getMessage());
        }
    }
}
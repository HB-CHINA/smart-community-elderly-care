package com.huangbin.smartcommunityelderlycare.controller;

import com.huangbin.smartcommunityelderlycare.common.Result;
import com.huangbin.smartcommunityelderlycare.entity.StatisticsReport;
import com.huangbin.smartcommunityelderlycare.service.StatisticsReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics/report")
@CrossOrigin
public class StatisticsReportController {

    @Autowired
    private StatisticsReportService statisticsReportService;

    /**
     * 生成指定日期的统计报表
     */
    @PostMapping("/generate/{date}")
    public Result<StatisticsReport> generateReport(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            StatisticsReport report = statisticsReportService.generateDailyReport(date);
            return Result.success("统计报表生成成功", report);
        } catch (Exception e) {
            return Result.error("生成统计报表失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定日期的统计报表
     */
    @GetMapping("/date/{date}")
    public Result<StatisticsReport> getReportByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            StatisticsReport report = statisticsReportService.getReportByDate(date);
            return Result.success("查询成功", report);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取最新统计报表
     */
    @GetMapping("/latest")
    public Result<StatisticsReport> getLatestReport() {
        try {
            StatisticsReport report = statisticsReportService.getLatestReport();
            if (report == null) {
                return Result.success("暂无统计报表", null);
            }
            return Result.success("查询成功", report);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取时间段内的统计报表
     */
    @GetMapping("/range")
    public Result<List<StatisticsReport>> getReportsInRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<StatisticsReport> reports = statisticsReportService.getReports(startDate, endDate);
            return Result.success("查询成功", reports);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 手动触发生成昨日报表（管理员用）
     */
    @PostMapping("/generate/yesterday")
    public Result<StatisticsReport> generateYesterdayReport() {
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            StatisticsReport report = statisticsReportService.generateDailyReport(yesterday);
            return Result.success("昨日报表生成成功", report);
        } catch (Exception e) {
            return Result.error("生成报表失败: " + e.getMessage());
        }
    }
}
package com.huangbin.smartcommunityelderlycare.service;

import com.huangbin.smartcommunityelderlycare.entity.StatisticsReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StatisticsReportService {

    // 原有方法
    StatisticsReport generateDailyReport(LocalDate date);
    StatisticsReport getReportByDate(LocalDate date);
    List<StatisticsReport> getReports(LocalDate start, LocalDate end);
    StatisticsReport getLatestReport();

    // 新增：仪表盘统计数据
    Map<String, Object> getDashboardStats();

    // 新增：趋势分析数据
    Map<String, Object> getTrendData(LocalDate startDate, LocalDate endDate, String trendType);

    // 新增：对比分析（同比、环比）
    Map<String, Object> getComparisonData(LocalDate date1, LocalDate date2);

    // 新增：健康数据统计
    Map<String, Object> getHealthStatistics(LocalDate startDate, LocalDate endDate);

    // 新增：财务数据统计
    Map<String, Object> getFinancialStatistics(LocalDate startDate, LocalDate endDate);

    // 新增：系统运行统计
    Map<String, Object> getSystemStatistics();

    // 新增：批量生成报表
    void generateReportsForDateRange(LocalDate startDate, LocalDate endDate);

    // 新增：删除报表
    void deleteReport(Long reportId);

    // 新增：验证报表
    StatisticsReport verifyReport(Long reportId, String verifiedBy);

    // 新增：获取报表摘要
    Map<String, Object> getReportOverview();
}
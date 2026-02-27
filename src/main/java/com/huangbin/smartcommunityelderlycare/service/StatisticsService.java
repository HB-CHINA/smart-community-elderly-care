package com.huangbin.smartcommunityelderlycare.service;

import com.huangbin.smartcommunityelderlycare.entity.StatisticsReport;

import java.time.LocalDate;
import java.util.Map;

public interface StatisticsService {

    // 基础统计方法
    Map<String, Object> getDailyCheckinStats(LocalDate startDate, LocalDate endDate);
    Map<String, Object> getOrderStatistics(LocalDate startDate, LocalDate endDate);
    Map<String, Object> getServiceDistribution();
    Map<String, Object> getHealthTrend(Long elderId, LocalDate startDate, LocalDate endDate);
    Map<String, Object> getSystemOverview();
    Map<String, Object> getDashboardStats();

    // 趋势和对比分析
    Map<String, Object> getTrendData(LocalDate startDate, LocalDate endDate, String trendType);
    Map<String, Object> getComparisonData(LocalDate date1, LocalDate date2);

    // 详细统计
    Map<String, Object> getHealthStatistics(LocalDate startDate, LocalDate endDate);
    Map<String, Object> getFinancialStatistics(LocalDate startDate, LocalDate endDate);
    Map<String, Object> getSystemStatistics();

    // 报表管理
    Map<String, Object> getReportOverview();
    void generateReportsForDateRange(LocalDate startDate, LocalDate endDate);
}
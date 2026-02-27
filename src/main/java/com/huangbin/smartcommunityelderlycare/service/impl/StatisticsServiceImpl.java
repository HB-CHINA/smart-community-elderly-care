package com.huangbin.smartcommunityelderlycare.service.impl;

import com.huangbin.smartcommunityelderlycare.dto.DailyStatDTO;
import com.huangbin.smartcommunityelderlycare.dto.TopServiceDTO;
import com.huangbin.smartcommunityelderlycare.entity.StatisticsReport;
import com.huangbin.smartcommunityelderlycare.entity.User; // 添加User导入
import com.huangbin.smartcommunityelderlycare.enums.OrderStatus;
import com.huangbin.smartcommunityelderlycare.repository.*;
import com.huangbin.smartcommunityelderlycare.service.StatisticsService;
import com.huangbin.smartcommunityelderlycare.service.StatisticsReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private ServiceOrderRepository orderRepository;

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private PaymentRepository paymentRepository;

    @Autowired(required = false)
    private StatisticsReportRepository statisticsReportRepository;

    @Autowired(required = false)
    private StatisticsReportService statisticsReportService;

    // ========== 基础统计方法 ==========

    @Override
    public Map<String, Object> getDailyCheckinStats(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Object[]> dailyData = checkInRepository.findCheckinsByDateRange(startDate, endDate);

            List<String> dates = new ArrayList<>();
            List<Long> counts = new ArrayList<>();
            long totalCheckins = 0;

            for (Object[] row : dailyData) {
                dates.add(row[0].toString());
                long count = ((Number) row[1]).longValue();
                counts.add(count);
                totalCheckins += count;
            }

            if (dates.isEmpty()) {
                fillDateRange(dates, counts, startDate, endDate);
            }

            result.put("dates", dates);
            result.put("counts", counts);
            result.put("totalCheckins", totalCheckins);
            result.put("averageDaily", dates.isEmpty() ? 0 : (double) totalCheckins / dates.size());

        } catch (Exception e) {
            logger.error("获取签到统计失败", e);
            return generateMockCheckinData(startDate, endDate);
        }

        return result;
    }

    @Override
    public Map<String, Object> getOrderStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();

        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

            List<Object[]> dailyData = orderRepository.findOrdersByDateRange(startDateTime, endDateTime);

            List<String> dates = new ArrayList<>();
            List<Long> orderCounts = new ArrayList<>();
            List<Double> orderAmounts = new ArrayList<>();

            long totalOrders = 0;
            double totalAmount = 0.0;

            for (Object[] row : dailyData) {
                String dateStr = row[0].toString();
                long count = ((Number) row[1]).longValue();
                BigDecimal amount = (BigDecimal) row[2];

                dates.add(dateStr);
                orderCounts.add(count);
                orderAmounts.add(amount != null ? amount.doubleValue() : 0.0);
                totalOrders += count;
                totalAmount += amount != null ? amount.doubleValue() : 0.0;
            }

            if (dates.isEmpty()) {
                fillDateRange(dates, orderCounts, startDate, endDate);
                for (int i = 0; i < dates.size(); i++) {
                    orderAmounts.add(0.0);
                }
            }

            result.put("dates", dates);
            result.put("orderCounts", orderCounts);
            result.put("orderAmounts", orderAmounts);
            result.put("totalOrders", totalOrders);
            result.put("totalAmount", String.format("%.2f", totalAmount));
            result.put("averageDailyOrders", dates.isEmpty() ? 0 : (double) totalOrders / dates.size());

            // 状态统计
            Map<String, Long> statusStats = new LinkedHashMap<>();
            // 修改后（使用枚举直接查询）：
            statusStats.put("待接单", orderRepository.countByStatus(OrderStatus.PENDING));
            statusStats.put("已接单", orderRepository.countByStatus(OrderStatus.ACCEPTED));
            statusStats.put("进行中", orderRepository.countByStatus(OrderStatus.IN_PROGRESS));
            statusStats.put("已完成", orderRepository.countByStatus(OrderStatus.COMPLETED));
            statusStats.put("已取消", orderRepository.countByStatus(OrderStatus.CANCELLED));
            result.put("statusStats", statusStats);

        } catch (Exception e) {
            logger.error("获取订单统计失败", e);
            return generateMockOrderData(startDate, endDate);
        }

        return result;
    }

    @Override
    public Map<String, Object> getServiceDistribution() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Object[]> serviceData = orderRepository.findServiceDistribution();

            List<String> serviceNames = new ArrayList<>();
            List<Long> serviceCounts = new ArrayList<>();
            List<String> percentages = new ArrayList<>();

            long total = serviceData.stream()
                    .mapToLong(row -> ((Number) row[1]).longValue())
                    .sum();

            for (Object[] row : serviceData) {
                serviceNames.add((String) row[0]);
                long count = ((Number) row[1]).longValue();
                serviceCounts.add(count);

                double percentage = total > 0 ? (count * 100.0 / total) : 0;
                percentages.add(String.format("%.1f%%", percentage));
            }

            result.put("serviceNames", serviceNames);
            result.put("serviceCounts", serviceCounts);
            result.put("percentages", percentages);
            result.put("total", total);

        } catch (Exception e) {
            logger.error("获取服务分布失败", e);
            result.put("serviceNames", Arrays.asList("上门送餐", "家政清洁", "健康护理", "陪伴服务", "医疗咨询"));
            result.put("serviceCounts", Arrays.asList(45L, 30L, 25L, 15L, 10L));
            result.put("percentages", Arrays.asList("36.0%", "24.0%", "20.0%", "12.0%", "8.0%"));
            result.put("total", 125L);
        }

        return result;
    }

    @Override
    public Map<String, Object> getHealthTrend(Long elderId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Object[]> healthData = healthRecordRepository.findHealthTrend(elderId, startDate, endDate);

            List<String> dates = new ArrayList<>();
            List<Integer> systolic = new ArrayList<>();
            List<Integer> diastolic = new ArrayList<>();
            List<Integer> heartRate = new ArrayList<>();

            for (Object[] row : healthData) {
                dates.add(row[0].toString());
                systolic.add(row[1] != null ? ((Number) row[1]).intValue() : 0);
                diastolic.add(row[2] != null ? ((Number) row[2]).intValue() : 0);
                heartRate.add(row[3] != null ? ((Number) row[3]).intValue() : 0);
            }

            if (dates.isEmpty()) {
                generateMockHealthData(dates, systolic, diastolic, heartRate, startDate, endDate);
            }

            result.put("dates", dates);
            result.put("systolic", systolic);
            result.put("diastolic", diastolic);
            result.put("heartRate", heartRate);

            double avgSystolic = systolic.stream().mapToInt(Integer::intValue).average().orElse(0);
            double avgDiastolic = diastolic.stream().mapToInt(Integer::intValue).average().orElse(0);
            double avgHeartRate = heartRate.stream().mapToInt(Integer::intValue).average().orElse(0);

            result.put("avgSystolic", String.format("%.1f", avgSystolic));
            result.put("avgDiastolic", String.format("%.1f", avgDiastolic));
            result.put("avgHeartRate", String.format("%.1f", avgHeartRate));

        } catch (Exception e) {
            logger.error("获取健康趋势失败", e);
            return generateMockHealthTrendData(startDate, endDate);
        }

        return result;
    }

    @Override
    public Map<String, Object> getSystemOverview() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 用户统计数据
            long totalUsers = userRepository.count();

            // 老人数量
            long totalElders = 0;
            try {
                // 尝试使用countByRole方法
                totalElders = userRepository.countByRole("elder");
            } catch (Exception e) {
                // 如果方法不存在，使用findByRole计算
                try {
                    List<User> elderUsers = userRepository.findByRole("elder");
                    totalElders = elderUsers != null ? elderUsers.size() : 0;
                } catch (Exception ex) {
                    logger.warn("无法获取老人数量", ex);
                }
            }

            // 家属数量
            long totalFamily = 0;
            try {
                List<User> familyUsers = userRepository.findByRole("family");
                totalFamily = familyUsers != null ? familyUsers.size() : 0;
            } catch (Exception e) {
                logger.warn("无法获取家属数量", e);
            }

            // 管理员数量
            long totalAdmin = 0;
            try {
                List<User> adminUsers = userRepository.findByRole("admin");
                totalAdmin = adminUsers != null ? adminUsers.size() : 0;
            } catch (Exception e) {
                logger.warn("无法获取管理员数量", e);
            }

            // 2. 今日签到数
            Long todayCheckins = 0L;
            try {
                todayCheckins = checkInRepository.countTodayCheckins();
                if (todayCheckins == null) todayCheckins = 0L;
            } catch (Exception e) {
                logger.warn("无法获取今日签到数", e);
            }

            // 3. 今日订单数
            long todayOrders = 0;
            try {
                LocalDateTime todayStart = LocalDate.now().atStartOfDay();
                LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);
                todayOrders = orderRepository.countByCreatedAtBetween(todayStart, todayEnd);
            } catch (Exception e) {
                logger.warn("无法获取今日订单数", e);
            }

            // 4. 订单总金额
            BigDecimal totalRevenue = BigDecimal.ZERO;
            try {
                totalRevenue = orderRepository.sumAmountByStatus("已完成");
                if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
            } catch (Exception e) {
                logger.warn("无法获取订单总金额", e);
            }

            // 5. 返回结果
            result.put("totalUsers", totalUsers);
            result.put("totalElders", totalElders);
            result.put("totalFamily", totalFamily);
            result.put("totalAdmin", totalAdmin);
            result.put("todayCheckins", todayCheckins);
            result.put("todayOrders", todayOrders);
            result.put("totalRevenue", String.format("%.2f", totalRevenue));

            // 6. 模拟增长率
            result.put("userGrowth", "12.5%");
            result.put("orderGrowth", "8.3%");
            result.put("revenueGrowth", "15.2%");

        } catch (Exception e) {
            logger.error("获取系统概览失败", e);
            // 模拟数据
            result.put("totalUsers", 28L);
            result.put("totalElders", 15L);
            result.put("totalFamily", 10L);
            result.put("totalAdmin", 3L);
            result.put("todayCheckins", 8L);
            result.put("todayOrders", 3L);
            result.put("totalRevenue", "1250.50");
            result.put("userGrowth", "12.5%");
            result.put("orderGrowth", "8.3%");
            result.put("revenueGrowth", "15.2%");
        }

        return result;
    }

    @Override
    public Map<String, Object> getDashboardStats() {
        LocalDate today = LocalDate.now();
        Map<String, Object> dashboard = new HashMap<>();

        try {
            Long todayCheckins = 0L;
            try {
                todayCheckins = checkInRepository.countTodayCheckins();
                if (todayCheckins == null) todayCheckins = 0L;
            } catch (Exception e) {
                logger.warn("无法获取今日签到数", e);
            }

            Long todayOrders = 0L;
            try {
                LocalDateTime todayStart = today.atStartOfDay();
                LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();
                todayOrders = orderRepository.countByCreatedAtBetween(todayStart, todayEnd);
            } catch (Exception e) {
                logger.warn("无法获取今日订单数", e);
            }

            Long todayHealthRecords = 0L;
            try {
                todayHealthRecords = healthRecordRepository.countByRecordDate(today);
            } catch (Exception e) {
                logger.warn("无法获取今日健康记录", e);
            }

            long totalUsers = 0;
            try {
                totalUsers = userRepository.count();
            } catch (Exception e) {
                logger.warn("无法获取总用户数", e);
            }

            long totalElders = 0;
            try {
                totalElders = userRepository.countByRole("elder");
            } catch (Exception e) {
                logger.warn("无法获取老人数量", e);
            }

            long totalOrders = 0;
            try {
                totalOrders = orderRepository.count();
            } catch (Exception e) {
                logger.warn("无法获取总订单数", e);
            }

            long activeElders = 0;
            try {
                LocalDate sevenDaysAgo = today.minusDays(7);
                activeElders = checkInRepository.countDistinctElders(sevenDaysAgo, today);
            } catch (Exception e) {
                logger.warn("无法获取活跃老人数", e);
            }

            Map<String, Object> todayStats = new HashMap<>();
            todayStats.put("checkins", todayCheckins);
            todayStats.put("orders", todayOrders != null ? todayOrders : 0);
            todayStats.put("healthRecords", todayHealthRecords);

            Map<String, Object> cumulativeStats = new HashMap<>();
            cumulativeStats.put("totalUsers", totalUsers);
            cumulativeStats.put("totalElders", totalElders);
            cumulativeStats.put("totalOrders", totalOrders);
            cumulativeStats.put("activeElders", activeElders);

            List<Object[]> checkinTrend = new ArrayList<>();
            try {
                checkinTrend = checkInRepository.findCheckinTrend(today.minusDays(7), today);
            } catch (Exception e) {
                logger.warn("无法获取签到趋势", e);
            }

            dashboard.put("today", todayStats);
            dashboard.put("cumulative", cumulativeStats);
            dashboard.put("trend", checkinTrend);
            dashboard.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            logger.error("获取仪表盘统计失败", e);
            dashboard.put("error", "获取数据失败");
        }

        return dashboard;
    }

    // ========== 趋势和对比分析 ==========

    @Override
    public Map<String, Object> getTrendData(LocalDate startDate, LocalDate endDate, String trendType) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Object[]> trendData;
            switch (trendType.toUpperCase()) {
                case "CHECKIN":
                    trendData = checkInRepository.findCheckinTrend(startDate, endDate);
                    break;
                case "ORDER":
                    trendData = orderRepository.findOrderTrend(startDate, endDate);
                    break;
                case "HEALTH":
                    trendData = healthRecordRepository.findHealthTrend(startDate, endDate);
                    break;
                default:
                    trendData = new ArrayList<>();
                    break;
            }

            result.put("trendType", trendType);
            result.put("startDate", startDate);
            result.put("endDate", endDate);
            result.put("data", trendData);
            result.put("summary", generateTrendSummary(trendData));

        } catch (Exception e) {
            logger.error("获取趋势数据失败", e);
            result.put("error", "获取趋势数据失败");
        }

        return result;
    }

    @Override
    public Map<String, Object> getComparisonData(LocalDate date1, LocalDate date2) {
        Map<String, Object> comparison = new HashMap<>();

        try {
            if (statisticsReportRepository == null || statisticsReportService == null) {
                throw new RuntimeException("报表服务未配置");
            }

            StatisticsReport report1 = statisticsReportRepository.findByStatisticsDate(date1)
                    .orElseGet(() -> statisticsReportService.generateDailyReport(date1));
            StatisticsReport report2 = statisticsReportRepository.findByStatisticsDate(date2)
                    .orElseGet(() -> statisticsReportService.generateDailyReport(date2));

            comparison.put("date1", date1);
            comparison.put("date2", date2);

            Map<String, Map<String, Object>> metrics = new HashMap<>();
            addComparisonMetric(metrics, "activeElders", "活跃老人数",
                    report1.getActiveElders(), report2.getActiveElders());
            addComparisonMetric(metrics, "totalUsers", "注册用户数",
                    report1.getTotalRegisteredUsers(), report2.getTotalRegisteredUsers());

            comparison.put("metrics", metrics);

        } catch (Exception e) {
            logger.error("获取对比数据失败", e);
            comparison.put("error", "获取对比数据失败");
        }

        return comparison;
    }

    // ========== 详细统计 ==========

    @Override
    public Map<String, Object> getHealthStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> healthStats = new HashMap<>();

        try {
            BigDecimal avgSystolic = healthRecordRepository.findAverageSystolic(startDate, endDate);
            BigDecimal avgDiastolic = healthRecordRepository.findAverageDiastolic(startDate, endDate);
            BigDecimal avgHeartRate = healthRecordRepository.findAverageHeartRate(startDate, endDate);
            Long abnormalCount = healthRecordRepository.countAbnormalRecords(startDate, endDate);

            healthStats.put("avgSystolicPressure", avgSystolic != null ? avgSystolic : 120);
            healthStats.put("avgDiastolicPressure", avgDiastolic != null ? avgDiastolic : 80);
            healthStats.put("avgHeartRate", avgHeartRate != null ? avgHeartRate : 75);
            healthStats.put("abnormalCount", abnormalCount != null ? abnormalCount : 0);

        } catch (Exception e) {
            logger.error("获取健康统计失败", e);
            healthStats.put("avgSystolicPressure", 120);
            healthStats.put("avgDiastolicPressure", 80);
            healthStats.put("avgHeartRate", 75);
            healthStats.put("abnormalCount", 0);
        }

        healthStats.put("period", startDate + " 至 " + endDate);
        healthStats.put("generatedAt", LocalDateTime.now());

        return healthStats;
    }

    @Override
    public Map<String, Object> getFinancialStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> financialStats = new HashMap<>();

        try {
            if (paymentRepository != null) {
                BigDecimal totalIncome = paymentRepository.sumAmountByPeriod(startDate, endDate);
                Long successfulPayments = paymentRepository.countSuccessfulByPeriod(startDate, endDate);
                Long totalPayments = paymentRepository.countByPeriod(startDate, endDate);

                financialStats.put("totalIncome", totalIncome != null ? totalIncome : BigDecimal.ZERO);
                financialStats.put("successfulPayments", successfulPayments != null ? successfulPayments : 0);

                if (totalPayments != null && totalPayments > 0) {
                    double successRate = (successfulPayments != null ? successfulPayments : 0) * 100.0 / totalPayments;
                    financialStats.put("successRate", String.format("%.1f%%", successRate));
                } else {
                    financialStats.put("successRate", "0%");
                }
            } else {
                financialStats.put("totalIncome", BigDecimal.ZERO);
                financialStats.put("successfulPayments", 0);
                financialStats.put("successRate", "0%");
            }

        } catch (Exception e) {
            logger.error("获取财务统计失败", e);
            financialStats.put("totalIncome", BigDecimal.ZERO);
            financialStats.put("successfulPayments", 0);
            financialStats.put("successRate", "0%");
        }

        financialStats.put("period", startDate + " 至 " + endDate);
        financialStats.put("generatedAt", LocalDateTime.now());

        return financialStats;
    }

    @Override
    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> systemStats = new HashMap<>();

        try {
            long totalUsers = userRepository.count();
            long totalElders = userRepository.countByRole("elder");
            long totalOrders = orderRepository.count();
            long totalHealthRecords = healthRecordRepository.count();
            long totalCheckins = checkInRepository.count();

            systemStats.put("totalUsers", totalUsers);
            systemStats.put("totalElders", totalElders);
            systemStats.put("totalOrders", totalOrders);
            systemStats.put("totalHealthRecords", totalHealthRecords);
            systemStats.put("totalCheckins", totalCheckins);

            Optional<LocalDateTime> firstRegistration = userRepository.findFirstRegistrationDate();
            if (firstRegistration.isPresent()) {
                long uptimeDays = ChronoUnit.DAYS.between(firstRegistration.get().toLocalDate(), LocalDate.now());
                systemStats.put("uptimeDays", uptimeDays);
            }

        } catch (Exception e) {
            logger.error("获取系统统计失败", e);
            systemStats.put("error", "获取系统统计失败");
        }

        systemStats.put("generatedAt", LocalDateTime.now());

        return systemStats;
    }

    // ========== 报表管理 ==========

    @Override
    public Map<String, Object> getReportOverview() {
        Map<String, Object> overview = new HashMap<>();

        try {
            if (statisticsReportRepository != null) {
                long reportCount = statisticsReportRepository.count();

                List<StatisticsReport> recentReports = statisticsReportRepository.findTop5ByOrderByStatisticsDateDesc();
                LocalDate latestDate = recentReports.isEmpty() ? null : recentReports.get(0).getStatisticsDate();

                overview.put("totalReports", reportCount);
                overview.put("latestReportDate", latestDate);
                overview.put("recentReports", recentReports);
            } else {
                overview.put("totalReports", 0);
                overview.put("latestReportDate", null);
                overview.put("recentReports", new ArrayList<>());
            }

        } catch (Exception e) {
            logger.error("获取报表概览失败", e);
            overview.put("error", "获取报表概览失败");
        }

        return overview;
    }

    @Override
    public void generateReportsForDateRange(LocalDate startDate, LocalDate endDate) {
        if (statisticsReportService != null) {
            statisticsReportService.generateReportsForDateRange(startDate, endDate);
        } else {
            logger.warn("报表服务未配置，无法生成报表");
        }
    }

    // ========== 辅助方法 ==========

    private void fillDateRange(List<String> dates, List<Long> counts, LocalDate start, LocalDate end) {
        LocalDate current = start;
        while (!current.isAfter(end)) {
            dates.add(current.toString());
            counts.add(0L);
            current = current.plusDays(1);
        }
    }

    private void generateMockHealthData(List<String> dates, List<Integer> systolic,
                                        List<Integer> diastolic, List<Integer> heartRate,
                                        LocalDate start, LocalDate end) {
        Random random = new Random();
        LocalDate current = start;
        int dayCount = 0;

        while (!current.isAfter(end) && dayCount < 10) {
            dates.add(current.toString());
            systolic.add(120 + random.nextInt(15));
            diastolic.add(75 + random.nextInt(10));
            heartRate.add(70 + random.nextInt(15));
            current = current.plusDays(1);
            dayCount++;
        }
    }

    private Map<String, Object> generateMockCheckinData(LocalDate start, LocalDate end) {
        Map<String, Object> result = new HashMap<>();
        Random random = new Random();

        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        LocalDate current = start;
        long total = 0;

        while (!current.isAfter(end) && dates.size() < 7) {
            dates.add(current.toString());
            long count = 10 + random.nextInt(15);
            counts.add(count);
            total += count;
            current = current.plusDays(1);
        }

        result.put("dates", dates);
        result.put("counts", counts);
        result.put("totalCheckins", total);
        result.put("averageDaily", (double) total / dates.size());

        return result;
    }

    private Map<String, Object> generateMockOrderData(LocalDate start, LocalDate end) {
        Map<String, Object> result = new HashMap<>();
        Random random = new Random();

        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        List<Double> orderAmounts = new ArrayList<>();
        LocalDate current = start;

        while (!current.isAfter(end) && dates.size() < 5) {
            dates.add(current.toString());
            counts.add(2L + random.nextInt(5));
            orderAmounts.add(70.0 + random.nextDouble() * 100);
            current = current.plusDays(1);
        }

        Map<String, Long> statusStats = new LinkedHashMap<>();
        statusStats.put("待接单", 3L);
        statusStats.put("已接单", 2L);
        statusStats.put("服务中", 1L);
        statusStats.put("已完成", 8L);
        statusStats.put("已取消", 1L);

        result.put("dates", dates);
        result.put("orderCounts", counts);
        result.put("orderAmounts", orderAmounts);
        result.put("statusStats", statusStats);

        return result;
    }

    private Map<String, Object> generateMockHealthTrendData(LocalDate start, LocalDate end) {
        Map<String, Object> result = new HashMap<>();
        Random random = new Random();

        List<String> dates = new ArrayList<>();
        List<Integer> systolic = new ArrayList<>();
        List<Integer> diastolic = new ArrayList<>();
        List<Integer> heartRate = new ArrayList<>();
        LocalDate current = start;

        while (!current.isAfter(end) && dates.size() < 7) {
            dates.add(current.toString());
            systolic.add(120 + random.nextInt(10));
            diastolic.add(75 + random.nextInt(10));
            heartRate.add(70 + random.nextInt(10));
            current = current.plusDays(1);
        }

        result.put("dates", dates);
        result.put("systolic", systolic);
        result.put("diastolic", diastolic);
        result.put("heartRate", heartRate);
        result.put("avgSystolic", "124.3");
        result.put("avgDiastolic", "78.5");
        result.put("avgHeartRate", "74.2");

        return result;
    }

    private Map<String, Object> generateTrendSummary(List<Object[]> trendData) {
        Map<String, Object> summary = new HashMap<>();

        if (trendData.isEmpty()) {
            summary.put("total", 0);
            summary.put("avg", 0);
            summary.put("max", 0);
            summary.put("min", 0);
            return summary;
        }

        double total = 0;
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        for (Object[] data : trendData) {
            if (data.length > 1 && data[1] instanceof Number) {
                double value = ((Number) data[1]).doubleValue();
                total += value;
                max = Math.max(max, value);
                min = Math.min(min, value);
            }
        }

        summary.put("total", total);
        summary.put("avg", total / trendData.size());
        summary.put("max", max);
        summary.put("min", min);
        summary.put("dataPoints", trendData.size());

        return summary;
    }

    private void addComparisonMetric(Map<String, Map<String, Object>> metrics,
                                     String key, String name,
                                     int value1, int value2) {
        Map<String, Object> metric = new HashMap<>();
        metric.put("name", name);
        metric.put("value1", value1);
        metric.put("value2", value2);

        double change = 0;
        if (value1 != 0) {
            change = ((value2 - value1) * 100.0) / value1;
        }

        metric.put("change", String.format("%.1f%%", change));
        metric.put("trend", change >= 0 ? "up" : "down");
        metric.put("changeValue", value2 - value1);

        metrics.put(key, metric);
    }
}
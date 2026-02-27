package com.huangbin.smartcommunityelderlycare.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "statistics_report")
@Data
public class StatisticsReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statisticsId;

    @Column(name = "statistics_date", nullable = false, unique = true)
    private LocalDate statisticsDate;

    // ===== 基础统计数据 =====
    @Column(name = "total_registered_users", nullable = false)
    private Integer totalRegisteredUsers;

    @Column(name = "active_elders", nullable = false)
    private Integer activeElders;  // 近期有签到的老人

    @Column(name = "monthly_order_count", nullable = false)
    private Integer monthlyOrderCount;

    @Column(name = "service_distribution", columnDefinition = "TEXT")
    private String serviceDistribution;  // JSON格式：{"家政":"45%","送餐":"30%"}

    // ===== 新增：健康数据统计 =====
    @Column(name = "health_record_count")
    private Integer healthRecordCount = 0;  // 健康记录总数

    @Column(name = "abnormal_health_count")
    private Integer abnormalHealthCount = 0;  // 异常健康指标数

    @Column(name = "avg_blood_pressure_systolic")
    private Double avgBloodPressureSystolic;  // 平均收缩压

    @Column(name = "avg_blood_pressure_diastolic")
    private Double avgBloodPressureDiastolic;  // 平均舒张压

    @Column(name = "avg_heart_rate")
    private Double avgHeartRate;  // 平均心率

    // ===== 新增：求助数据统计 =====
    @Column(name = "emergency_call_count")
    private Integer emergencyCallCount = 0;  // 求助呼叫次数

    @Column(name = "emergency_resolved_count")
    private Integer emergencyResolvedCount = 0;  // 已处理求助数

    @Column(name = "emergency_avg_response_time")
    private Double emergencyAvgResponseTime;  // 平均响应时间（分钟）

    // ===== 新增：社区数据统计 =====
    @Column(name = "community_post_count")
    private Integer communityPostCount = 0;  // 社区发帖数

    @Column(name = "community_comment_count")
    private Integer communityCommentCount = 0;  // 社区评论数

    @Column(name = "activity_participant_count")
    private Integer activityParticipantCount = 0;  // 活动参与人数

    // ===== 新增：财务数据统计 =====
    @Column(name = "daily_income", precision = 10, scale = 2)
    private BigDecimal dailyIncome = BigDecimal.ZERO;  // 当日收入

    @Column(name = "daily_expense", precision = 10, scale = 2)
    private BigDecimal dailyExpense = BigDecimal.ZERO;  // 当日支出

    @Column(name = "payment_success_rate")
    private Double paymentSuccessRate;  // 支付成功率

    // ===== 新增：系统性能统计 =====
    @Column(name = "active_users_today")
    private Integer activeUsersToday = 0;  // 今日活跃用户数

    @Column(name = "new_registrations_today")
    private Integer newRegistrationsToday = 0;  // 今日新注册用户数

    @Column(name = "system_uptime_days")
    private Integer systemUptimeDays;  // 系统运行天数

    @Column(name = "generated_at")
    private LocalDateTime generatedAt = LocalDateTime.now();

    @Column(name = "report_status", length = 20)
    private String reportStatus = "GENERATED";  // GENERATED, VERIFIED, ARCHIVED

    @Column(name = "report_summary", columnDefinition = "TEXT")
    private String reportSummary;  // 报表摘要

    @PrePersist
    protected void onCreate() {
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
        }
        if (reportStatus == null) {
            reportStatus = "GENERATED";
        }
    }
}
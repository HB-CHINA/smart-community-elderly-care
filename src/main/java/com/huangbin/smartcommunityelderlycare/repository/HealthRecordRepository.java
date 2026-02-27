package com.huangbin.smartcommunityelderlycare.repository;

import com.huangbin.smartcommunityelderlycare.entity.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    // 原有方法
    List<HealthRecord> findByElder_UserId(Long elderId);
    List<HealthRecord> findByElder_UserIdOrderByRecordDateDesc(Long elderId);

    // ========== 新增统计服务需要的方法 ==========

    /**
     * StatisticsServiceImpl需要：获取健康趋势数据（带老人ID）
     * 返回数组：[日期, 收缩压, 舒张压, 心率]
     */
    @Query("SELECT hr.recordDate, hr.systolicPressure, hr.diastolicPressure, hr.heartRate " +
            "FROM HealthRecord hr " +
            "WHERE hr.elder.userId = :elderId " +
            "AND hr.recordDate BETWEEN :startDate AND :endDate " +
            "ORDER BY hr.recordDate")
    List<Object[]> findHealthTrend(@Param("elderId") Long elderId,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    /**
     * StatisticsServiceImpl需要：获取健康趋势数据（不带老人ID，统计所有）
     * 返回数组：[日期, 平均收缩压, 平均舒张压, 平均心率]
     */
    @Query("SELECT hr.recordDate, " +
            "AVG(hr.systolicPressure), " +
            "AVG(hr.diastolicPressure), " +
            "AVG(hr.heartRate) " +
            "FROM HealthRecord hr " +
            "WHERE hr.recordDate BETWEEN :startDate AND :endDate " +
            "GROUP BY hr.recordDate " +
            "ORDER BY hr.recordDate")
    List<Object[]> findHealthTrend(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    /**
     * StatisticsServiceImpl需要：平均收缩压
     */
    @Query("SELECT AVG(hr.systolicPressure) FROM HealthRecord hr " +
            "WHERE hr.recordDate BETWEEN :startDate AND :endDate")
    BigDecimal findAverageSystolic(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    /**
     * StatisticsServiceImpl需要：平均舒张压
     */
    @Query("SELECT AVG(hr.diastolicPressure) FROM HealthRecord hr " +
            "WHERE hr.recordDate BETWEEN :startDate AND :endDate")
    BigDecimal findAverageDiastolic(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    /**
     * StatisticsServiceImpl需要：平均心率
     */
    @Query("SELECT AVG(hr.heartRate) FROM HealthRecord hr " +
            "WHERE hr.recordDate BETWEEN :startDate AND :endDate")
    BigDecimal findAverageHeartRate(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    /**
     * StatisticsServiceImpl需要：统计异常记录数
     */
    @Query("SELECT COUNT(hr) FROM HealthRecord hr " +
            "WHERE hr.recordDate BETWEEN :startDate AND :endDate " +
            "AND (hr.systolicPressure > 140 OR hr.diastolicPressure > 90 OR hr.heartRate > 100)")
    Long countAbnormalRecords(@Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);

    // ========== 原有统计方法保持不变 ==========

    /**
     * 获取健康趋势数据（ECharts折线图用）- 保留原有方法名
     */
    @Query("SELECT hr.recordDate, hr.systolicPressure, hr.diastolicPressure, hr.heartRate " +
            "FROM HealthRecord hr " +
            "WHERE hr.elder.userId = :elderId " +
            "AND hr.recordDate BETWEEN :startDate AND :endDate " +
            "ORDER BY hr.recordDate")
    List<Object[]> findHealthTrendByElderId(@Param("elderId") Long elderId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    /**
     * 获取最近N条健康记录
     */
    @Query("SELECT hr FROM HealthRecord hr " +
            "WHERE hr.elder.userId = :elderId " +
            "ORDER BY hr.recordDate DESC " +
            "LIMIT :limit")
    List<HealthRecord> findRecentHealthRecords(@Param("elderId") Long elderId,
                                               @Param("limit") int limit);

    /**
     * 获取今日健康记录
     */
    @Query("SELECT hr FROM HealthRecord hr " +
            "WHERE hr.elder.userId = :elderId " +
            "AND hr.recordDate = CURRENT_DATE")
    Optional<HealthRecord> findTodayHealthRecord(@Param("elderId") Long elderId);

    /**
     * 获取平均健康指标
     */
    @Query("SELECT AVG(hr.systolicPressure), AVG(hr.diastolicPressure), AVG(hr.heartRate) " +
            "FROM HealthRecord hr " +
            "WHERE hr.elder.userId = :elderId " +
            "AND hr.recordDate BETWEEN :startDate AND :endDate")
    Object[] getAverageHealthMetrics(@Param("elderId") Long elderId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(hr) FROM HealthRecord hr WHERE hr.recordDate = :date")
    Long countByRecordDate(@Param("date") LocalDate date);

    @Query("SELECT AVG(hr.systolicPressure), AVG(hr.diastolicPressure), AVG(hr.heartRate) " +
            "FROM HealthRecord hr WHERE hr.recordDate BETWEEN :startDate AND :endDate")
    List<Object[]> getAverageHealthMetricsForPeriod(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

    // 简化的异常判断，实际应该根据医学标准
    @Query("SELECT COUNT(hr) FROM HealthRecord hr " +
            "WHERE hr.recordDate BETWEEN :startDate AND :endDate " +
            "AND (hr.systolicPressure > 140 OR hr.diastolicPressure > 90 OR hr.heartRate > 100)")
    Long countAbnormalHealthRecords(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
}
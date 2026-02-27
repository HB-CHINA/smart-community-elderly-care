package com.huangbin.smartcommunityelderlycare.repository;

import com.huangbin.smartcommunityelderlycare.entity.StatisticsReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatisticsReportRepository extends JpaRepository<StatisticsReport, Long> {

    Optional<StatisticsReport> findByStatisticsDate(LocalDate date);

    List<StatisticsReport> findByStatisticsDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT sr FROM StatisticsReport sr ORDER BY sr.statisticsDate DESC")
    List<StatisticsReport> findAllOrderByDateDesc();

    // 新增：统计服务需要的方法
    default List<StatisticsReport> findTop5ByOrderByStatisticsDateDesc() {
        return findRecentReports(5);
    }

    // 新增：检查报表是否存在
    boolean existsByStatisticsDate(LocalDate date);

    // 新增：获取趋势数据（用于图表）
    @Query("SELECT sr.statisticsDate, sr.activeElders, sr.healthRecordCount, sr.emergencyCallCount " +
            "FROM StatisticsReport sr " +
            "WHERE sr.statisticsDate BETWEEN :startDate AND :endDate " +
            "ORDER BY sr.statisticsDate")
    List<Object[]> findTrendData(@Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate);

    // 新增：获取活跃用户趋势
    @Query("SELECT sr.statisticsDate, sr.activeUsersToday FROM StatisticsReport sr " +
            "WHERE sr.statisticsDate BETWEEN :startDate AND :endDate " +
            "ORDER BY sr.statisticsDate")
    List<Object[]> findActiveUserTrend(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    // 新增：获取服务收入趋势
    @Query("SELECT sr.statisticsDate, sr.dailyIncome FROM StatisticsReport sr " +
            "WHERE sr.statisticsDate BETWEEN :startDate AND :endDate " +
            "ORDER BY sr.statisticsDate")
    List<Object[]> findIncomeTrend(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    // 新增：获取最新的N条报表
    @Query("SELECT sr FROM StatisticsReport sr ORDER BY sr.statisticsDate DESC LIMIT :limit")
    List<StatisticsReport> findRecentReports(@Param("limit") int limit);

    // 新增：获取统计数据摘要
    @Query("SELECT COUNT(sr), MIN(sr.statisticsDate), MAX(sr.statisticsDate) FROM StatisticsReport sr")
    Object[] getReportSummary();


}
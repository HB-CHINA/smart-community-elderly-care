package com.huangbin.smartcommunityelderlycare.repository;

import com.huangbin.smartcommunityelderlycare.entity.EmergencyAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmergencyAlertRepository extends JpaRepository<EmergencyAlert, Long> {

    // 根据老人ID查询求助记录
    List<EmergencyAlert> findByElder_UserIdOrderByTriggerTimeDesc(Long elderId);

    // 根据状态查询求助记录
    List<EmergencyAlert> findByStatus(String status);

    // 根据类型查询求助记录
    List<EmergencyAlert> findByAlertType(String alertType);

    // StatisticsReportServiceImpl需要的方法
    @Query("SELECT COUNT(a) FROM EmergencyAlert a WHERE DATE(a.triggerTime) = :date")
    Long countByAlertDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM EmergencyAlert a WHERE DATE(a.triggerTime) = :date AND a.status = '已处理'")
    Long countResolvedByDate(@Param("date") LocalDate date);

    @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, a.triggerTime, a.handledAt)) FROM EmergencyAlert a " +
            "WHERE DATE(a.triggerTime) = :date AND a.status = '已处理' AND a.handledAt IS NOT NULL")
    Double findAverageResponseTime(@Param("date") LocalDate date);

    // 其他可能需要的统计方法
    @Query("SELECT COUNT(a) FROM EmergencyAlert a WHERE a.triggerTime BETWEEN :start AND :end")
    Long countByTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(a) FROM EmergencyAlert a WHERE a.triggerTime BETWEEN :start AND :end AND a.status = '已处理'")
    Long countResolvedByTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 今日未处理求助
    @Query("SELECT COUNT(a) FROM EmergencyAlert a WHERE DATE(a.triggerTime) = CURRENT_DATE AND a.status = '待处理'")
    Long countTodayPendingAlerts();
}
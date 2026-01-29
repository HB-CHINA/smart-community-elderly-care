package com.huangbin.smartcommunityelderlycare.repository;

import com.huangbin.smartcommunityelderlycare.entity.AlarmRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmRecord, Long> {

    // 查找老人的所有报警记录 - 修正方法名
    List<AlarmRecord> findByElderUserId(Long elderId);

    // 按状态查找报警记录
    List<AlarmRecord> findByStatus(String status);

    // 检查今天是否已生成过某类型的预警
    @Query("SELECT COUNT(a) > 0 FROM AlarmRecord a WHERE a.elder.userId = :elderId " +
            "AND a.alarmType = :alarmType " +
            "AND DATE(a.triggerTime) = CURRENT_DATE")
    boolean existsTodayAlarm(
            @Param("elderId") Long elderId,
            @Param("alarmType") String alarmType
    );

    // 统计未处理的报警数量
    long countByStatus(String status);

    // 按报警类型查找
    List<AlarmRecord> findByAlarmType(String alarmType);

    // 查找指定时间范围内的报警记录
    List<AlarmRecord> findByTriggerTimeBetween(LocalDateTime start, LocalDateTime end);

    // 查找老人的待处理报警
    List<AlarmRecord> findByElderUserIdAndStatus(Long elderId, String status);

    // 获取所有报警并关联老人信息 - 使用 LEFT JOIN 避免空值问题
    @Query("SELECT a FROM AlarmRecord a LEFT JOIN FETCH a.elder ORDER BY a.triggerTime DESC")
    List<AlarmRecord> findAllWithElder();

    // 原生SQL查询作为备用方案
    @Query(value = "SELECT * FROM alarm_record WHERE status = ?1 ORDER BY trigger_time DESC", nativeQuery = true)
    List<AlarmRecord> findByStatusNative(String status);

    @Query(value = "SELECT * FROM alarm_record ORDER BY trigger_time DESC", nativeQuery = true)
    List<AlarmRecord> findAllNative();

    /**
     * 检查今天是否已生成过健康数据异常预警
     */
    @Query("SELECT COUNT(a) > 0 FROM AlarmRecord a WHERE a.elder.userId = :elderId " +
            "AND a.alarmType = '健康数据异常预警' " +
            "AND DATE(a.triggerTime) = CURRENT_DATE")
    boolean existsTodayHealthAlert(@Param("elderId") Long elderId);
}
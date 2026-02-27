package com.huangbin.smartcommunityelderlycare.repository;

import com.huangbin.smartcommunityelderlycare.entity.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    // 原有方法保持不变
    Optional<CheckIn> findByElder_UserIdAndCheckInDate(Long userId, LocalDate checkInDate);
    List<CheckIn> findByElder_UserId(Long userId);
    boolean existsByElder_UserIdAndCheckInDate(Long userId, LocalDate checkInDate);

    // ========== 新增统计服务需要的方法 ==========

    /**
     * StatisticsServiceImpl需要：根据日期范围查询签到数据
     * 返回数组：[日期, 签到数]
     */
    @Query("SELECT DATE(c.checkInDate), COUNT(c) FROM CheckIn c " +
            "WHERE c.checkInDate BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(c.checkInDate) " +
            "ORDER BY DATE(c.checkInDate)")
    List<Object[]> findCheckinsByDateRange(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    /**
     * StatisticsServiceImpl需要：签到趋势统计
     * 返回数组：[日期, 签到数]
     */
    @Query("SELECT DATE(c.checkInDate), COUNT(c) FROM CheckIn c " +
            "WHERE c.checkInDate BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(c.checkInDate) " +
            "ORDER BY DATE(c.checkInDate)")
    List<Object[]> findCheckinTrend(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    /**
     * StatisticsServiceImpl需要：统计指定时间段内不同的老人数量
     * 统计活跃老人数
     */
    @Query("SELECT COUNT(DISTINCT c.elder.userId) FROM CheckIn c " +
            "WHERE c.checkInDate BETWEEN :startDate AND :endDate")
    Long countDistinctElders(@Param("startDate") LocalDate startDate,
                             @Param("endDate") LocalDate endDate);

    /**
     * StatisticsServiceImpl需要：根据老人ID和日期查询签到记录
     * 使用现有方法名，保持兼容
     */
    default Optional<CheckIn> findByUserIdAndDate(Long userId, LocalDate date) {
        return findByElder_UserIdAndCheckInDate(userId, date);
    }

    // ========== 原有统计方法保持不变 ==========

    /**
     * 统计每日签到数量（ECharts用）
     */
    @Query("SELECT DATE(c.checkInDate), COUNT(c) FROM CheckIn c " +
            "WHERE c.checkInDate BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(c.checkInDate) " +
            "ORDER BY DATE(c.checkInDate)")
    List<Object[]> countCheckinsByDate(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    /**
     * 今日签到数量
     */
    @Query("SELECT COUNT(c) FROM CheckIn c WHERE DATE(c.checkInDate) = CURRENT_DATE")
    Long countTodayCheckins();

    /**
     * 按老人统计签到次数
     */
    @Query("SELECT u.name, COUNT(c) FROM CheckIn c " +
            "JOIN c.elder u " +
            "WHERE c.checkInDate BETWEEN :startDate AND :endDate " +
            "GROUP BY u.userId, u.name " +
            "ORDER BY COUNT(c) DESC")
    List<Object[]> countCheckinsByElder(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    // 检查老人某天是否签到（使用自定义查询）
    @Query("SELECT COUNT(c) > 0 FROM CheckIn c WHERE c.elder.userId = :elderId AND c.checkInDate = :checkInDate")
    boolean existsByElderIdAndCheckInDate(@Param("elderId") Long elderId, @Param("checkInDate") LocalDate checkInDate);

    // 使用 c.elder.userId 而不是 c.elderId
    @Query("SELECT COUNT(DISTINCT c.elder.userId) FROM CheckIn c WHERE c.checkInDate >= :date")
    long countDistinctEldersWithCheckInAfter(@Param("date") LocalDate date);

    @Query("SELECT COUNT(c) FROM CheckIn c WHERE c.checkInDate = :date")
    Long countByCheckInDate(@Param("date") LocalDate date);
}
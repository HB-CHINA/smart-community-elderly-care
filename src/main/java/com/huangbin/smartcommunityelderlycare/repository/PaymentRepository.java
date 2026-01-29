package com.huangbin.smartcommunityelderlycare.repository;

import com.huangbin.smartcommunityelderlycare.entity.PaymentRecord;
import com.huangbin.smartcommunityelderlycare.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentRecord, Long> {

    // 原有方法保持不变
    PaymentRecord findByOrderId(String orderId);

    // ========== 新增统计服务需要的方法 ==========

    /**
     * StatisticsServiceImpl需要：根据时间段统计支付金额
     */
    @Query("SELECT SUM(p.amount) FROM PaymentRecord p " +
            "WHERE p.paymentTime BETWEEN :startDate AND :endDate " +
            "AND p.paymentStatus = com.huangbin.smartcommunityelderlycare.enums.PaymentStatus.SUCCESS")  // 使用完整类名
    BigDecimal sumAmountByPeriod(@Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate);

    /**
     * StatisticsServiceImpl需要：统计成功支付数量
     */
    @Query("SELECT COUNT(p) FROM PaymentRecord p " +
            "WHERE p.paymentTime BETWEEN :startDate AND :endDate " +
            "AND p.paymentStatus = com.huangbin.smartcommunityelderlycare.enums.PaymentStatus.SUCCESS")
    Long countSuccessfulByPeriod(@Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate);

    /**
     * StatisticsServiceImpl需要：统计时间段内总支付数量
     */
    @Query("SELECT COUNT(p) FROM PaymentRecord p " +
            "WHERE p.paymentTime BETWEEN :startDate AND :endDate")
    Long countByPeriod(@Param("startDate") LocalDate startDate,
                       @Param("endDate") LocalDate endDate);

    // ========== 可选：其他可能需要的统计方法 ==========

    /**
     * 根据状态查询支付记录
     */
    List<PaymentRecord> findByPaymentStatus(PaymentStatus paymentStatus);  // 使用枚举类型

    /**
     * 根据用户ID查询支付记录
     */
    List<PaymentRecord> findByUserId(Long userId);

    /**
     * 今日成功支付统计
     */
    @Query("SELECT COUNT(p) FROM PaymentRecord p " +
            "WHERE DATE(p.paymentTime) = CURRENT_DATE " +
            "AND p.paymentStatus = com.huangbin.smartcommunityelderlycare.enums.PaymentStatus.SUCCESS")
    Long countTodaySuccessfulPayments();

    /**
     * 今日支付总额
     */
    @Query("SELECT SUM(p.amount) FROM PaymentRecord p " +
            "WHERE DATE(p.paymentTime) = CURRENT_DATE " +
            "AND p.paymentStatus = com.huangbin.smartcommunityelderlycare.enums.PaymentStatus.SUCCESS")
    BigDecimal sumTodaySuccessfulAmount();
}
package com.huangbin.smartcommunityelderlycare.repository;

import com.huangbin.smartcommunityelderlycare.dto.DailyStatDTO;
import com.huangbin.smartcommunityelderlycare.dto.TopServiceDTO;
import com.huangbin.smartcommunityelderlycare.entity.ServiceOrder;
import com.huangbin.smartcommunityelderlycare.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, String> {

    // ========== 基本查询方法 ==========

    // 根据用户ID查询订单（按创建时间降序）
    List<ServiceOrder> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    // 查询所有订单（按创建时间降序）
    List<ServiceOrder> findAllByOrderByCreatedAtDesc();

    // 根据订单ID查询
    Optional<ServiceOrder> findByOrderId(String orderId);

    // 检查用户预约时间冲突
    boolean existsByUser_UserIdAndScheduledTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);

    // ========== 分页查询方法 ==========

    // 根据用户ID分页查询
    Page<ServiceOrder> findByUser_UserId(Long userId, Pageable pageable);

    // 根据用户ID和状态分页查询（枚举版本）
    Page<ServiceOrder> findByUser_UserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);

    // 根据用户ID和状态分页查询（字符串版本 - 保持兼容）
    Page<ServiceOrder> findByUser_UserIdAndStatus(Long userId, String status, Pageable pageable);

    // ========== 状态统计方法 ==========

    // 按状态统计数量（枚举版本）
    long countByStatus(OrderStatus status);

    // 按状态统计数量（字符串版本 - 保持兼容）
    long countByStatus(String status);

    // 使用枚举查询的方法
    @Query("SELECT o FROM ServiceOrder o WHERE o.status = :status")
    List<ServiceOrder> findByStatus(@Param("status") OrderStatus status);

    // 使用字符串查询的方法（兼容性）
    @Query("SELECT o FROM ServiceOrder o WHERE o.status = :status")
    List<ServiceOrder> findByStatusString(@Param("status") String status);

    // 按创建时间范围统计数量
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 按多个状态统计数量
    long countByStatusIn(List<String> statuses);

    // ========== 金额统计方法 ==========

    // 按状态统计总金额（枚举版本）- 修复：重命名方法避免冲突
    @Query("SELECT SUM(o.amount) FROM ServiceOrder o WHERE o.status = :status")
    BigDecimal sumAmountByStatusEnum(@Param("status") OrderStatus status);

    // 按状态统计总金额（字符串版本）- 保留原方法名
    @Query("SELECT SUM(o.amount) FROM ServiceOrder o WHERE o.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") String status);

    // ========== StatisticsServiceImpl需要的统计方法 ==========

    /**
     * 根据日期范围查询订单数据（用于StatisticsServiceImpl）
     * 返回：[日期, 订单数, 总金额]
     */
    @Query("SELECT DATE(o.createdAt) as date, COUNT(o), SUM(o.amount) FROM ServiceOrder o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(o.createdAt) " +
            "ORDER BY DATE(o.createdAt)")
    List<Object[]> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    /**
     * 服务分布统计（用于StatisticsServiceImpl）
     * 返回：[服务名称, 订单数]
     */
    @Query("SELECT s.serviceName, COUNT(o) FROM ServiceOrder o " +
            "JOIN o.service s " +
            "GROUP BY s.serviceName " +
            "ORDER BY COUNT(o) DESC")
    List<Object[]> findServiceDistribution();

    /**
     * 订单趋势统计（用于StatisticsServiceImpl）
     * 返回：[日期, 订单数]
     */
    @Query("SELECT DATE(o.createdAt) as date, COUNT(o) FROM ServiceOrder o " +
            "WHERE DATE(o.createdAt) BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(o.createdAt) " +
            "ORDER BY DATE(o.createdAt)")
    List<Object[]> findOrderTrend(@Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);

    /**
     * 根据服务名称统计订单数
     */
    @Query("SELECT COUNT(o) FROM ServiceOrder o WHERE o.service.serviceName = :serviceName")
    Long countByServiceName(@Param("serviceName") String serviceName);

    // ========== 其他统计查询方法 ==========

    /**
     * 按服务统计订单数量 - 返回数组
     * 返回：[服务名称, 订单数]
     */
    @Query("SELECT s.serviceName, COUNT(o) FROM ServiceOrder o " +
            "JOIN o.service s " +
            "GROUP BY s.serviceId, s.serviceName " +
            "ORDER BY COUNT(o) DESC")
    List<Object[]> countOrdersByService();

    /**
     * 按服务统计订单数量 - 返回DTO
     */
    @Query("SELECT new com.huangbin.smartcommunityelderlycare.dto.ServiceDistributionDTO(" +
            "s.serviceName, COUNT(o), " +
            "COUNT(o) * 100.0 / (SELECT COUNT(*) FROM ServiceOrder)) " +
            "FROM ServiceOrder o " +
            "JOIN o.service s " +
            "GROUP BY s.serviceId, s.serviceName " +
            "ORDER BY COUNT(o) DESC")
    List<com.huangbin.smartcommunityelderlycare.dto.ServiceDistributionDTO> countOrdersByServiceWithDTO();

    /**
     * 按日期统计订单 - 返回数组
     * 返回：[日期, 订单数, 总金额]
     */
    @Query("SELECT DATE(o.createdAt), COUNT(o), SUM(o.amount) FROM ServiceOrder o " +
            "WHERE o.createdAt BETWEEN :start AND :end " +
            "GROUP BY DATE(o.createdAt) " +
            "ORDER BY DATE(o.createdAt)")
    List<Object[]> countOrdersByDate(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);

    /**
     * 按日期统计订单 - 返回DTO
     */
    @Query("SELECT new com.huangbin.smartcommunityelderlycare.dto.DailyStatDTO(" +
            "DATE(o.createdAt), COUNT(o), SUM(o.amount)) " +
            "FROM ServiceOrder o " +
            "WHERE o.createdAt BETWEEN :start AND :end " +
            "GROUP BY DATE(o.createdAt) " +
            "ORDER BY DATE(o.createdAt)")
    List<DailyStatDTO> countOrdersByDateWithDTO(@Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end);

    /**
     * 获取热门服务（前5名）- 返回数组
     * 返回：[服务名称, 订单数]
     */
    @Query("SELECT s.serviceName, COUNT(o) as orderCount FROM ServiceOrder o " +
            "JOIN o.service s " +
            "GROUP BY s.serviceId, s.serviceName " +
            "ORDER BY orderCount DESC " +
            "LIMIT 5")
    List<Object[]> findTopServices();

    /**
     * 获取热门服务（前5名）- 返回DTO
     */
    @Query("SELECT new com.huangbin.smartcommunityelderlycare.dto.TopServiceDTO(" +
            "s.serviceId, s.serviceName, COUNT(o), s.price) " +
            "FROM ServiceOrder o " +
            "JOIN o.service s " +
            "GROUP BY s.serviceId, s.serviceName, s.price " +
            "ORDER BY COUNT(o) DESC " +
            "LIMIT 5")
    List<TopServiceDTO> findTopServicesWithDTO();

    /**
     * 按服务类别统计订单数量
     * 返回：[服务类别, 订单数]
     */
    @Query("SELECT s.category, COUNT(o) FROM ServiceOrder o " +
            "JOIN o.service s " +
            "WHERE o.createdAt BETWEEN :start AND :end " +
            "GROUP BY s.category")
    List<Object[]> countOrdersByServiceCategory(@Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end);
}
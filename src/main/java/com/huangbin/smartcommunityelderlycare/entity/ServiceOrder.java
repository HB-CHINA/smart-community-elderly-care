package com.huangbin.smartcommunityelderlycare.entity;

import com.huangbin.smartcommunityelderlycare.enums.OrderStatus;
import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrder {
    @Id
    @Column(name = "order_id", length = 32)
    private String orderId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceItem service;

    @Column(nullable = false)
    private LocalDateTime scheduledTime;

    @Enumerated(EnumType.STRING)  // 添加这个注解
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;  // 使用枚举

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String review;

    @Column
    private Integer rating;

    // ========== 添加便捷访问方法 ==========

    public Long getUserId() {
        return this.user != null ? this.user.getUserId() : null;
    }

    public Long getServiceId() {
        return this.service != null ? this.service.getServiceId() : null;
    }

    public BigDecimal getOrderAmount() {
        return this.amount;
    }
}
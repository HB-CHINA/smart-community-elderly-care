package com.huangbin.smartcommunityelderlycare.entity;

import com.huangbin.smartcommunityelderlycare.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payment_record")
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "payment_method")
    private String paymentMethod = "模拟支付";

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)  // 合并了注解
    private PaymentStatus paymentStatus;  // 只保留一个字段

    @Column(name = "transaction_no")
    private String transactionNo;

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
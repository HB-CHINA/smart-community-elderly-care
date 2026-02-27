package com.huangbin.smartcommunityelderlycare.entity;

import com.huangbin.smartcommunityelderlycare.enums.ServiceStatus;  // 添加导入
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "service")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @NotBlank(message = "服务名称不能为空")
    @Column(nullable = false, unique = true, length = 100)
    private String serviceName;

    @NotBlank(message = "服务分类不能为空")
    @Column(nullable = false, length = 30)
    private String category;

    @NotBlank(message = "服务描述不能为空")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "服务价格不能为空")
    @DecimalMin(value = "0.01", message = "服务价格必须大于0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)  // 添加这个注解
    @Column(nullable = false, length = 20)
    private ServiceStatus status = ServiceStatus.ONLINE;  // 使用枚举

    @CreationTimestamp
    private LocalDateTime createdAt;
}
package com.huangbin.smartcommunityelderlycare.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ServiceDistributionDTO {
    private String serviceName;
    private Long count;
    private Double percentage;
    private String category;
    private BigDecimal totalAmount; // 该服务总金额

    // 构造方法（用于JPQL的new表达式）
    public ServiceDistributionDTO(String serviceName, Long count, Double percentage) {
        this.serviceName = serviceName;
        this.count = count;
        this.percentage = percentage;
    }

    // 无参构造函数
    public ServiceDistributionDTO() {}
}
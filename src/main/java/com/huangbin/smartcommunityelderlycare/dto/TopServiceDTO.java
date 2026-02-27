package com.huangbin.smartcommunityelderlycare.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TopServiceDTO {
    private Long serviceId;        // 服务ID
    private String serviceName;    // 服务名称
    private Long orderCount;       // 订单数量
    private BigDecimal price;      // 服务价格（如果需要）
    private Double percentage;     // 百分比
    private String category;       // 服务分类

    // 构造方法1：接受 Object 类型
    public TopServiceDTO(Object serviceId, Object serviceName, Object orderCount, Object price) {
        this.serviceId = serviceId != null ? Long.valueOf(serviceId.toString()) : null;
        this.serviceName = serviceName != null ? serviceName.toString() : null;
        this.orderCount = orderCount != null ? Long.valueOf(orderCount.toString()) : null;
        this.price = price != null ? new BigDecimal(price.toString()) : null;
    }

    // 构造方法2：原来的构造方法（保持兼容）
    public TopServiceDTO(Long serviceId, String serviceName, Long orderCount) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.orderCount = orderCount;
    }

    // 构造方法3：原来的构造方法（保持兼容）
    public TopServiceDTO(Long serviceId, String serviceName, Long orderCount, BigDecimal price) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.orderCount = orderCount;
        this.price = price;
    }

    // 构造方法4：无参构造函数
    public TopServiceDTO() {}
}
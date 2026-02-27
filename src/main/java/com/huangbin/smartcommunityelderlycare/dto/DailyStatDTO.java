package com.huangbin.smartcommunityelderlycare.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DailyStatDTO {
    private String date;           // 日期
    private Long orderCount;       // 订单数量
    private BigDecimal totalAmount; // 总金额

    // 构造方法：接受 Object 类型
    public DailyStatDTO(Object date, Long orderCount, BigDecimal totalAmount) {
        this.date = date != null ? date.toString() : null;
        this.orderCount = orderCount;
        this.totalAmount = totalAmount;
    }

    // 原来的构造方法（保持兼容）
    public DailyStatDTO(String date, Long orderCount, BigDecimal totalAmount) {
        this.date = date;
        this.orderCount = orderCount;
        this.totalAmount = totalAmount;
    }

    // 无参构造函数
    public DailyStatDTO() {}
}
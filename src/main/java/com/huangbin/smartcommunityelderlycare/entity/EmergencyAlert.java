package com.huangbin.smartcommunityelderlycare.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alarm_record")  // 对应数据库表名
@Data
public class EmergencyAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_record_id")  // 匹配数据库字段名
    private Long alertId;

    @ManyToOne
    @JoinColumn(name = "elder_id", nullable = false)  // 匹配数据库字段名
    private User elder;

    @Column(name = "alarm_type", nullable = false, length = 20)
    private String alertType;  // 手动一键求助、签到缺失预警、健康数据异常预警

    @Column(name = "trigger_time", nullable = false)
    private LocalDateTime triggerTime;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "待处理";  // 待处理、已处理

    @Column(name = "handle_notes", length = 200)
    private String handleNotes;

    @Column(name = "handled_by")
    private Long handledBy;  // 处理人ID

    @Column(name = "handled_at")
    private LocalDateTime handledAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 便捷方法
    public boolean isResolved() {
        return "已处理".equals(status);
    }
}
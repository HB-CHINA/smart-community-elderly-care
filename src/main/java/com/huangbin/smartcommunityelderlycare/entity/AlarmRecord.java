// AlarmRecord.java
package com.huangbin.smartcommunityelderlycare.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alarm_record")
@Data
public class AlarmRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")  // 修正为正确的数据库字段名
    private Long alarmId;

    @ManyToOne
    @JoinColumn(name = "elder_id", nullable = false)
    private User elder;

    @Column(name = "alarm_type", nullable = false, length = 20)
    private String alarmType;  // 手动一键求助、签到缺失预警、健康数据异常预警

    @Column(name = "description", length = 200)
    private String description;  // 报警描述

    @Column(name = "trigger_time", nullable = false)
    private LocalDateTime triggerTime;  // 触发时间

    @Column(name = "triggered_at")
    private LocalDateTime triggeredAt = LocalDateTime.now();  // 记录创建时间

    @Column(name = "status", length = 20)
    private String status = "待处理";  // 待处理、已处理

    @Column(name = "note", length = 200)
    private String note;  // 备注

    @Column(name = "handle_notes", length = 200)
    private String handleNotes;  // 处理备注

    @Column(name = "handled_by")
    private Long handledBy;  // 处理人ID

    @Column(name = "handled_at")
    private LocalDateTime handledAt;  // 处理时间

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;  // 创建时间

    // 便捷方法
    public boolean isResolved() {
        return "已处理".equals(status);
    }

    // 处理报警的方法
    public void resolve(String notes, Long handlerId) {
        this.status = "已处理";
        this.handleNotes = notes;
        this.handledBy = handlerId;
        this.handledAt = LocalDateTime.now();
    }
}
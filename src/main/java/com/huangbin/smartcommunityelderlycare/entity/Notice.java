package com.huangbin.smartcommunityelderlycare.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notice")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 方案A：直接存储管理员ID
    @Column(name = "admin_id", nullable = false)
    private Long adminId;  // 发布公告的管理员ID

    @Column(nullable = false)
    private LocalDateTime publishedAt = LocalDateTime.now();

    @Column(nullable = false)
    private Integer status = 1; // 1发布 0草稿

    // 可选：添加更新时间字段
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 可选：在更新时自动设置更新时间
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
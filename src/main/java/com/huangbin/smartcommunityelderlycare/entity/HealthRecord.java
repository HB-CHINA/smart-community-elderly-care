package com.huangbin.smartcommunityelderlycare.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @ManyToOne
    @JoinColumn(name = "elder_id", nullable = false)
    private User elder;

    @Column(nullable = false)
    private LocalDate recordDate = LocalDate.now();

    @Min(value = 50, message = "收缩压不能低于50mmHg")
    @Max(value = 250, message = "收缩压不能高于250mmHg")
    @Column
    private Integer systolicPressure; // 收缩压

    @Min(value = 30, message = "舒张压不能低于30mmHg")
    @Max(value = 150, message = "舒张压不能高于150mmHg")
    @Column
    private Integer diastolicPressure; // 舒张压

    @Min(value = 30, message = "心率不能低于30次/分钟")
    @Max(value = 200, message = "心率不能高于200次/分钟")
    @Column
    private Integer heartRate; // 心率

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
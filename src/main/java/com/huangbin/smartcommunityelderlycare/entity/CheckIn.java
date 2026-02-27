package com.huangbin.smartcommunityelderlycare.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "check_in")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long checkInId;

    @ManyToOne
    @JoinColumn(name = "elder_id", nullable = false)
    private User elder;

    @Column(name = "check_in_date")
    private LocalDate checkInDate = LocalDate.now();

    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
package com.huangbin.smartcommunityelderlycare.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    // 头像存储路径
    @Column(length = 255)
    private String avatar;

    // 在phone字段上添加这2行验证注解 ↓
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Column(unique = true, nullable = false, length = 11)
    private String phone;

    // 在password字段上添加这2行验证注解 ↓
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6位")
    @Column(nullable = false)
    private String password;

    // 在role字段上添加这2行验证注解 ↓
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "elder|family|admin", message = "角色只能是 elder/family/admin")
    @Column(nullable = false, length = 20)
    private String role; // elder, family, admin

    // 在name字段上添加这1行验证注解 ↓
    @NotBlank(message = "姓名不能为空")
    @Column(nullable = false, length = 50)
    private String name;

    // 在emergencyContact字段上添加这1行验证注解（可选）↓
    @Pattern(regexp = "^1[3-9]\\d{9}$|^$", message = "紧急联系人手机号格式不正确")
    @Column(length = 11)
    private String emergencyContact;

    @Column(nullable = false)
    private Integer status = 1; // 1启用 0禁用

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
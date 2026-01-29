package com.huangbin.smartcommunityelderlycare.repository;

import com.huangbin.smartcommunityelderlycare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    boolean existsByPhone(String phone);
    List<User> findByRole(String role);

    // 新增/确认以下方法

    // 统计特定角色的用户数量
    long countByRole(String role);

    // 查找最早的注册时间
    @Query("SELECT MIN(u.createdAt) FROM User u")
    Optional<LocalDateTime> findFirstRegistrationDate();

    // 统计今日新注册用户数
    @Query("SELECT COUNT(u) FROM User u WHERE DATE(u.createdAt) = :date")
    Long countNewRegistrationsToday(@Param("date") LocalDate date);

    // 新增：统计特定角色和状态的用户数
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.status = 1")
    Long countActiveByRole(@Param("role") String role);

    // 新增：统计今日活跃用户数（有签到或订单）
    @Query("SELECT COUNT(DISTINCT u.userId) FROM User u " +
            "WHERE u.userId IN (SELECT c.elder.userId FROM CheckIn c WHERE DATE(c.checkInDate) = CURRENT_DATE) " +
            "OR u.userId IN (SELECT o.user.userId FROM ServiceOrder o WHERE DATE(o.createdAt) = CURRENT_DATE)")
    Long countActiveUsersToday();
}
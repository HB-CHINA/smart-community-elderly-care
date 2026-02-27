package com.huangbin.smartcommunityelderlycare.repository;

import com.huangbin.smartcommunityelderlycare.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 改为 adminId，而不是 publisherId
    List<Notice> findByStatusOrderByPublishedAtDesc(Integer status);
    List<Notice> findByAdminId(Long adminId);  // 重要修改！

    @Query("SELECT COUNT(n) FROM Notice n WHERE DATE(n.publishedAt) = :date AND n.status = 1")
    Long countByCreateDate(@Param("date") LocalDate date);
}
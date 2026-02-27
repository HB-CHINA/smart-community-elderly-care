// src/main/java/com/huangbin/smartcommunityelderlycare/repository/ServiceRepository.java
package com.huangbin.smartcommunityelderlycare.repository;

import com.huangbin.smartcommunityelderlycare.entity.ServiceItem;
import com.huangbin.smartcommunityelderlycare.enums.ServiceStatus;  // 添加导入
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceItem, Long> {
    List<ServiceItem> findByCategory(String category);

    // 使用枚举的查询方法
    List<ServiceItem> findByStatus(ServiceStatus status);

    // 保留字符串查询方法用于兼容
    List<ServiceItem> findByStatus(String status);
}
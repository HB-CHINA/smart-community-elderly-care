package com.huangbin.smartcommunityelderlycare.service;

import com.huangbin.smartcommunityelderlycare.entity.EmergencyAlert;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EmergencyAlertService {

    EmergencyAlert createAlert(EmergencyAlert alert);

    EmergencyAlert updateAlert(Long id, EmergencyAlert alert);

    void deleteAlert(Long id);

    EmergencyAlert getAlertById(Long id);

    List<EmergencyAlert> getAllAlerts();

    List<EmergencyAlert> getPendingAlerts();

    List<EmergencyAlert> getAlertsByElderly(Long elderlyId);

    List<EmergencyAlert> getAlertsByStatus(String status);

    // 注释掉这个方法，因为实体类没有priority字段
    // List<EmergencyAlert> getAlertsByPriority(String priority);

    EmergencyAlert resolveAlert(Long alertId, String resolutionNotes);

    EmergencyAlert assignAlert(Long alertId, Long staffId);

    Long countTodayAlerts();

    Long countAlertsByDate(LocalDate date);

    Map<String, Long> getAlertStatistics(LocalDateTime startDate, LocalDateTime endDate);

    List<EmergencyAlert> getUrgentAlerts();

    // 添加这两个方法（在实现类中已存在）
    EmergencyAlert processAlert(Long alertId, Long staffId, String notes);

    EmergencyAlert createEmergencyHelp(Long elderId, String alertType);
}
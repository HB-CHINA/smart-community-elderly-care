package com.huangbin.smartcommunityelderlycare.service.impl;

import com.huangbin.smartcommunityelderlycare.entity.EmergencyAlert;
import com.huangbin.smartcommunityelderlycare.entity.User;
import com.huangbin.smartcommunityelderlycare.repository.EmergencyAlertRepository;
import com.huangbin.smartcommunityelderlycare.repository.UserRepository;
import com.huangbin.smartcommunityelderlycare.service.EmergencyAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class EmergencyAlertServiceImpl implements EmergencyAlertService {

    @Autowired
    private EmergencyAlertRepository emergencyAlertRepository;

    @Autowired
    private UserRepository userRepository; // 添加UserRepository

    @Override
    public EmergencyAlert createAlert(EmergencyAlert alert) {
        if (alert.getTriggerTime() == null) {
            alert.setTriggerTime(LocalDateTime.now());
        }
        return emergencyAlertRepository.save(alert);
    }

    @Override
    public EmergencyAlert updateAlert(Long id, EmergencyAlert alertDetails) {
        EmergencyAlert alert = getAlertById(id);

        if (alertDetails.getStatus() != null) {
            alert.setStatus(alertDetails.getStatus());
        }
        if (alertDetails.getAlertType() != null) {
            alert.setAlertType(alertDetails.getAlertType());
        }
        if (alertDetails.getHandleNotes() != null) {
            alert.setHandleNotes(alertDetails.getHandleNotes());
        }
        if (alertDetails.getHandledBy() != null) {
            alert.setHandledBy(alertDetails.getHandledBy());
        }
        if (alertDetails.getHandledAt() != null) {
            alert.setHandledAt(alertDetails.getHandledAt());
        }

        return emergencyAlertRepository.save(alert);
    }

    @Override
    public void deleteAlert(Long id) {
        emergencyAlertRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public EmergencyAlert getAlertById(Long id) {
        return emergencyAlertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("紧急求助记录未找到，ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmergencyAlert> getAllAlerts() {
        return emergencyAlertRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmergencyAlert> getPendingAlerts() {
        return emergencyAlertRepository.findByStatus("待处理");
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmergencyAlert> getAlertsByElderly(Long elderlyId) {
        return emergencyAlertRepository.findByElder_UserIdOrderByTriggerTimeDesc(elderlyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmergencyAlert> getAlertsByStatus(String status) {
        return emergencyAlertRepository.findByStatus(status);
    }

    // 由于接口中注释掉了这个方法，这里可以移除或保留但不加@Override
    @Transactional(readOnly = true)
    public List<EmergencyAlert> getAlertsByPriority(String priority) {
        // 由于实体类没有priority字段，暂时返回空列表
        return Collections.emptyList();
    }

    @Override
    public EmergencyAlert resolveAlert(Long alertId, String resolutionNotes) {
        EmergencyAlert alert = getAlertById(alertId);
        alert.setStatus("已处理");
        alert.setHandleNotes(resolutionNotes);
        alert.setHandledAt(LocalDateTime.now());
        return emergencyAlertRepository.save(alert);
    }

    @Override
    public EmergencyAlert assignAlert(Long alertId, Long staffId) {
        EmergencyAlert alert = getAlertById(alertId);
        alert.setHandledBy(staffId);
        alert.setStatus("处理中");
        return emergencyAlertRepository.save(alert);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countTodayAlerts() {
        return emergencyAlertRepository.countByAlertDate(LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAlertsByDate(LocalDate date) {
        return emergencyAlertRepository.countByAlertDate(date);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getAlertStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Long> statistics = new HashMap<>();

        statistics.put("totalAlerts", emergencyAlertRepository.countByTimeRange(startDate, endDate));
        statistics.put("pendingAlerts", emergencyAlertRepository.countTodayPendingAlerts());
        statistics.put("resolvedAlerts", emergencyAlertRepository.countResolvedByTimeRange(startDate, endDate));

        // 注意：以下字段需要实体类支持
        statistics.put("highPriorityAlerts", 0L);
        statistics.put("criticalAlerts", 0L);

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmergencyAlert> getUrgentAlerts() {
        // 获取今日待处理的紧急求助
        LocalDate today = LocalDate.now();
        return emergencyAlertRepository.findAll().stream()
                .filter(alert -> alert.getTriggerTime() != null &&
                        alert.getTriggerTime().toLocalDate().equals(today) &&
                        "待处理".equals(alert.getStatus()))
                .sorted(Comparator.comparing(EmergencyAlert::getTriggerTime))
                .toList();
    }

    @Override
    public EmergencyAlert processAlert(Long alertId, Long staffId, String notes) {
        EmergencyAlert alert = getAlertById(alertId);
        alert.setHandledBy(staffId);
        alert.setHandleNotes(notes);
        alert.setHandledAt(LocalDateTime.now());
        alert.setStatus("已处理");
        return emergencyAlertRepository.save(alert);
    }

    @Override
    public EmergencyAlert createEmergencyHelp(Long elderId, String alertType) {
        // 查找老人信息
        User elder = userRepository.findById(elderId)
                .orElseThrow(() -> new RuntimeException("老人信息未找到，ID: " + elderId));

        EmergencyAlert alert = new EmergencyAlert();
        alert.setElder(elder); // 设置老人信息
        alert.setTriggerTime(LocalDateTime.now());
        alert.setAlertType(alertType);
        alert.setStatus("待处理");

        return emergencyAlertRepository.save(alert);
    }
}
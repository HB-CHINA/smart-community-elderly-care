package com.huangbin.smartcommunityelderlycare.service;

import com.huangbin.smartcommunityelderlycare.entity.AlarmRecord;

import java.time.LocalDateTime;
import java.util.List;

public interface AlarmService {
    void handleMissedCheckIn(Long elderId);
    void triggerAlarm(Long elderId, String alarmType, String description);
    List<AlarmRecord> getAllAlarms();
    List<AlarmRecord> getAlarmsByElder(Long elderId);
    void resolveAlarm(Long alarmId, String handleNotes, Long handlerId);
    List<AlarmRecord> getPendingAlarms();
    long countPendingAlarms();

    // 新增的两个方法
    List<AlarmRecord> getAlarmsByType(String alarmType);
    List<AlarmRecord> getAlarmsByTimeRange(LocalDateTime start, LocalDateTime end);

    // 新增调试方法
    AlarmRecord getAlarmById(Long alarmId);
}
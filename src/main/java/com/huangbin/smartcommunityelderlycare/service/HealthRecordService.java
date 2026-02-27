package com.huangbin.smartcommunityelderlycare.service;

import com.huangbin.smartcommunityelderlycare.entity.HealthRecord;
import java.util.List;

public interface HealthRecordService {
    HealthRecord addHealthRecord(Long elderId, HealthRecord healthRecord);
    List<HealthRecord> getHealthRecordsByElderId(Long elderId);
    HealthRecord getLatestHealthRecord(Long elderId);
    HealthRecord getTodayHealthRecord(Long elderId);
}
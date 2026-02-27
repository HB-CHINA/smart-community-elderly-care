package com.huangbin.smartcommunityelderlycare.service;

import com.huangbin.smartcommunityelderlycare.entity.CheckIn;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CheckInService {
    CheckIn checkIn(Long elderId);
    boolean getTodayCheckInStatus(Long elderId);
    List<CheckIn> getCheckInRecords(Long elderId);

    Long getTodayCheckinsCount();

    List<Map<String, Object>> getCheckinsByDateRange(LocalDate startDate, LocalDate endDate);

    List<Map<String, Object>> getCheckinRanking(LocalDate startDate, LocalDate endDate);

    Long countDistinctUsersCheckinAfter(LocalDate date);
}
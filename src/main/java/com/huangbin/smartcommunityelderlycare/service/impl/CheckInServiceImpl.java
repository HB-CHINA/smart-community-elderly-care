package com.huangbin.smartcommunityelderlycare.service.impl;

import com.huangbin.smartcommunityelderlycare.entity.CheckIn;
import com.huangbin.smartcommunityelderlycare.entity.User;
import com.huangbin.smartcommunityelderlycare.repository.CheckInRepository;
import com.huangbin.smartcommunityelderlycare.repository.UserRepository;
import com.huangbin.smartcommunityelderlycare.service.AlarmService;
import com.huangbin.smartcommunityelderlycare.service.CheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CheckInServiceImpl implements CheckInService {

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlarmService alarmService;

    /**
     * 老人签到
     */
    public CheckIn checkIn(Long elderId) {
        // 1. 验证老人是否存在且角色正确
        Optional<User> elderOpt = userRepository.findById(elderId);
        if (elderOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User elder = elderOpt.get();
        if (!"elder".equals(elder.getRole())) {
            throw new RuntimeException("只有老人角色可以签到");
        }

        // 2. 检查今天是否已签到
        LocalDate today = LocalDate.now();
        Optional<CheckIn> existingCheckIn = checkInRepository
                .findByElder_UserIdAndCheckInDate(elderId, today);

        if (existingCheckIn.isPresent()) {
            throw new RuntimeException("今日已签到，请勿重复操作");
        }

        // 3. 创建签到记录
        CheckIn checkIn = new CheckIn();
        checkIn.setElder(elder);
        checkIn.setCheckInDate(today);
        checkIn.setCheckInTime(LocalTime.now());

        // 4. 保存签到记录
        return checkInRepository.save(checkIn);
    }

    /**
     * 获取老人今日签到状态
     */
    public boolean getTodayCheckInStatus(Long elderId) {
        LocalDate today = LocalDate.now();
        return checkInRepository.existsByElder_UserIdAndCheckInDate(elderId, today);
    }

    /**
     * 获取老人的签到记录
     */
    public List<CheckIn> getCheckInRecords(Long elderId) {
        return checkInRepository.findByElder_UserId(elderId);
    }

    // ============ 新增统计方法实现 ============

    /**
     * 获取今日签到数量
     */
    @Override
    public Long getTodayCheckinsCount() {
        Long count = checkInRepository.countTodayCheckins();
        return count != null ? count : 0L;
    }

    /**
     * 统计指定日期范围的签到数据（用于图表）
     */
    @Override
    public List<Map<String, Object>> getCheckinsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = checkInRepository.countCheckinsByDate(startDate, endDate);
        List<Map<String, Object>> stats = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", row[0]);  // 日期
            map.put("count", row[1]); // 签到次数
            stats.add(map);
        }
        return stats;
    }

    /**
     * 获取签到活跃用户排名
     */
    @Override
    public List<Map<String, Object>> getCheckinRanking(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = checkInRepository.countCheckinsByElder(startDate, endDate);
        List<Map<String, Object>> ranking = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0]);  // 老人姓名
            map.put("count", row[1]); // 签到次数
            ranking.add(map);
        }
        return ranking;
    }

    /**
     * 获取过去指定天数内签到过的独立用户数
     */
    @Override
    public Long countDistinctUsersCheckinAfter(LocalDate date) {
        return checkInRepository.countDistinctEldersWithCheckInAfter(date);
    }

    /**
     * 获取过去N天内签到过的独立用户数（简化接口）
     */
    public Long countDistinctUsersCheckinLastNDays(int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        return checkInRepository.countDistinctEldersWithCheckInAfter(startDate);
    }

    /**
     * 检查老人某天是否签到（自定义查询版本）
     */
    public boolean existsByElderIdAndCheckInDate(Long elderId, LocalDate checkInDate) {
        return checkInRepository.existsByElderIdAndCheckInDate(elderId, checkInDate);
    }
}
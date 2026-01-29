// src/main/java/com/huangbin/smartcommunityelderlycare/service/WarningService.java
package com.huangbin.smartcommunityelderlycare.service;

import com.huangbin.smartcommunityelderlycare.entity.AlarmRecord;
import com.huangbin.smartcommunityelderlycare.entity.User;
import com.huangbin.smartcommunityelderlycare.repository.AlarmRepository;
import com.huangbin.smartcommunityelderlycare.repository.CheckInRepository;
import com.huangbin.smartcommunityelderlycare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WarningService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private AlarmService alarmService;

    /**
     * 每日定时检查签到缺失（晚上8点执行）
     */
    @Scheduled(cron = "0 0 20 * * ?") // 每天20:00执行
    @Transactional
    public void checkDailySignIn() {
        LocalDate today = LocalDate.now();
        System.out.println("开始执行每日签到缺失检查，日期：" + today);

        // 1. 获取所有老人用户
        List<User> elders = userRepository.findByRole("elder");

        int warningCount = 0;
        for (User elder : elders) {
            // 2. 检查该老人今天是否签到
            boolean hasCheckedIn = checkInRepository.existsByElderIdAndCheckInDate(elder.getUserId(), today);

            if (!hasCheckedIn) {
                // 3. 未签到，创建预警记录
                triggerNoSignInWarning(elder.getUserId());
                warningCount++;
            }
        }

        System.out.println("签到缺失检查完成，发现 " + warningCount + " 位老人未签到");
    }

    /**
     * 手动触发签到缺失检查（供管理员调用）
     */
    @Transactional
    public int manualCheckSignIn() {
        return checkDailySignInManually();
    }

    private int checkDailySignInManually() {
        LocalDate today = LocalDate.now();
        List<User> elders = userRepository.findByRole("elder");

        int warningCount = 0;
        for (User elder : elders) {
            boolean hasCheckedIn = checkInRepository.existsByElderIdAndCheckInDate(elder.getUserId(), today);

            if (!hasCheckedIn) {
                // 修改这里：使用现有的方法，统一报警类型为中文
                boolean alreadyWarned = alarmRepository.existsTodayAlarm(
                        elder.getUserId(),
                        "签到缺失预警"  // 统一为中文
                );

                if (!alreadyWarned) {
                    triggerNoSignInWarning(elder.getUserId());
                    warningCount++;
                }
            }
        }
        return warningCount;
    }

    /**
     * 触发签到缺失预警
     */
    private void triggerNoSignInWarning(Long elderId) {
        try {
            // 修改这里：补充缺失的description参数
            alarmService.triggerAlarm(elderId, "签到缺失预警", "老人今日未完成签到");

            System.out.println("已为老人ID: " + elderId + " 创建签到缺失预警");
        } catch (Exception e) {
            System.err.println("创建签到缺失预警失败，老人ID: " + elderId + ", 错误: " + e.getMessage());
        }
    }

    /**
     * 检查老人今天是否签到
     */
    public boolean checkTodaySignInStatus(Long elderId) {
        LocalDate today = LocalDate.now();
        // 使用正确的repository方法
        return checkInRepository.existsByElderIdAndCheckInDate(elderId, today);
    }
}
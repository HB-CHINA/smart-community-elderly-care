package com.huangbin.smartcommunityelderlycare.service;

import com.huangbin.smartcommunityelderlycare.entity.User;
import com.huangbin.smartcommunityelderlycare.repository.CheckInRepository;
import com.huangbin.smartcommunityelderlycare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@EnableScheduling
public class CheckInMonitorService {

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlarmService alarmService;

    // 每晚8点检查未签到老人
    @Scheduled(cron = "0 0 20 * * ?")
    public void checkMissedCheckIns() {
        System.out.println("开始检查未签到老人...");

        List<User> elders = userRepository.findByRole("elder");
        LocalDate today = LocalDate.now();

        for (User elder : elders) {
            boolean hasCheckedIn = checkInRepository
                    .existsByElder_UserIdAndCheckInDate(elder.getUserId(), today);

            if (!hasCheckedIn) {
                alarmService.handleMissedCheckIn(elder.getUserId());
            }
        }
    }
}
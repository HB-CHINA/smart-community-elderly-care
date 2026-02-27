package com.huangbin.smartcommunityelderlycare.service.impl;

import com.huangbin.smartcommunityelderlycare.entity.AlarmRecord;
import com.huangbin.smartcommunityelderlycare.entity.User;
import com.huangbin.smartcommunityelderlycare.repository.AlarmRepository;
import com.huangbin.smartcommunityelderlycare.repository.UserRepository;
import com.huangbin.smartcommunityelderlycare.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmServiceImpl implements AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void handleMissedCheckIn(Long elderId) {
        // 检查老人是否存在并获取完整对象
        User elder = userRepository.findById(elderId)
                .orElseThrow(() -> {
                    log.error("老人不存在: {}", elderId);
                    return new RuntimeException("老人不存在: " + elderId);
                });

        // 检查今天是否已经生成过签到缺失预警
        if (!alarmRepository.existsTodayAlarm(elderId, "签到缺失预警")) {
            AlarmRecord alarm = new AlarmRecord();
            alarm.setElder(elder);  // 设置完整的User对象
            alarm.setAlarmType("签到缺失预警");
            alarm.setDescription("老人今日未完成签到");
            alarm.setTriggerTime(LocalDateTime.now());
            alarm.setStatus("待处理");  // 明确设置状态

            alarmRepository.save(alarm);
            log.info("为老人 {} 创建签到缺失预警", elderId);
        } else {
            log.info("老人 {} 今天已有签到缺失预警，跳过创建", elderId);
        }
    }

    @Override
    @Transactional
    public void triggerAlarm(Long elderId, String alarmType, String description) {
        // 检查老人是否存在
        User elder = userRepository.findById(elderId)
                .orElseThrow(() -> {
                    log.error("老人不存在: {}", elderId);
                    return new RuntimeException("老人不存在: " + elderId);
                });

        AlarmRecord alarm = new AlarmRecord();
        alarm.setElder(elder);
        alarm.setAlarmType(alarmType);
        alarm.setDescription(description);
        alarm.setTriggerTime(LocalDateTime.now());
        alarm.setStatus("待处理");  // 明确设置状态

        alarmRepository.save(alarm);
        log.info("为老人 {} 创建报警: {} - {}", elderId, alarmType, description);
    }

    @Override
    public List<AlarmRecord> getAllAlarms() {
        List<AlarmRecord> alarms = alarmRepository.findAllWithElder();
        log.debug("查询到 {} 条报警记录", alarms.size());
        return alarms;
    }

    @Override
    public List<AlarmRecord> getAlarmsByElder(Long elderId) {
        List<AlarmRecord> alarms = alarmRepository.findByElderUserId(elderId);
        log.debug("查询到老人 {} 的 {} 条报警记录", elderId, alarms.size());
        return alarms;
    }

    @Override
    @Transactional
    public void resolveAlarm(Long alarmId, String handleNotes, Long handlerId) {
        AlarmRecord alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> {
                    log.error("报警记录不存在: {}", alarmId);
                    return new RuntimeException("报警记录不存在: " + alarmId);
                });

        // 检查是否已经处理
        if ("已处理".equals(alarm.getStatus())) {
            log.warn("报警 {} 已经是已处理状态，跳过处理", alarmId);
            return;
        }

        alarm.resolve(handleNotes, handlerId);
        alarmRepository.save(alarm);
        log.info("处理报警 {}，处理人: {}", alarmId, handlerId);
    }

    @Override
    public List<AlarmRecord> getPendingAlarms() {
        List<AlarmRecord> alarms = alarmRepository.findByStatus("待处理");
        log.info("查询到 {} 条待处理报警", alarms.size());
        return alarms;
    }

    @Override
    public long countPendingAlarms() {
        long count = alarmRepository.countByStatus("待处理");
        log.debug("待处理报警数量: {}", count);
        return count;
    }

    @Override
    public List<AlarmRecord> getAlarmsByType(String alarmType) {
        List<AlarmRecord> alarms = alarmRepository.findByAlarmType(alarmType);
        log.debug("按类型 {} 查询到 {} 条报警", alarmType, alarms.size());
        return alarms;
    }

    @Override
    public List<AlarmRecord> getAlarmsByTimeRange(LocalDateTime start, LocalDateTime end) {
        List<AlarmRecord> alarms = alarmRepository.findByTriggerTimeBetween(start, end);
        log.debug("按时间范围查询到 {} 条报警", alarms.size());
        return alarms;
    }

    // 新增：获取单个报警
    public AlarmRecord getAlarmById(Long alarmId) {
        return alarmRepository.findById(alarmId).orElse(null);
    }
}
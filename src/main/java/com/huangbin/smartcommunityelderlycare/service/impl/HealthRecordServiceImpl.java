package com.huangbin.smartcommunityelderlycare.service.impl;

import com.huangbin.smartcommunityelderlycare.entity.AlarmRecord;
import com.huangbin.smartcommunityelderlycare.entity.HealthRecord;
import com.huangbin.smartcommunityelderlycare.entity.User;
import com.huangbin.smartcommunityelderlycare.repository.AlarmRepository;
import com.huangbin.smartcommunityelderlycare.repository.HealthRecordRepository;
import com.huangbin.smartcommunityelderlycare.repository.UserRepository;
import com.huangbin.smartcommunityelderlycare.service.AlarmService;
import com.huangbin.smartcommunityelderlycare.service.HealthRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class HealthRecordServiceImpl implements HealthRecordService {

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private AlarmRepository alarmRepository;

    // 健康数据异常阈值配置
    private static final int SYSTOLIC_HIGH = 140;    // 收缩压上限
    private static final int SYSTOLIC_LOW = 90;      // 收缩压下限
    private static final int DIASTOLIC_HIGH = 90;    // 舒张压上限
    private static final int DIASTOLIC_LOW = 60;     // 舒张压下限
    private static final int HEART_RATE_HIGH = 100;  // 心率上限
    private static final int HEART_RATE_LOW = 60;    // 心率下限

    /**
     * 添加健康记录
     */
    @Override
    @Transactional
    public HealthRecord addHealthRecord(Long elderId, HealthRecord healthRecord) {
        // 验证老人用户存在且角色正确
        User elder = userRepository.findById(elderId)
                .orElseThrow(() -> new RuntimeException("老人不存在"));

        if (!"elder".equals(elder.getRole())) {
            throw new RuntimeException("该用户不是老人角色");
        }

        // 验证健康数据范围
        validateHealthData(healthRecord);

        // 设置关联老人
        healthRecord.setElder(elder);

        // 保存健康记录
        HealthRecord savedRecord = healthRecordRepository.save(healthRecord);

        // 检测健康数据异常并触发报警
        checkHealthDataAndTriggerAlarm(savedRecord);

        return savedRecord;
    }

    /**
     * 检测健康数据并触发报警
     */
    private void checkHealthDataAndTriggerAlarm(HealthRecord record) {
        // 检查是否需要报警
        HealthAlertResult alertResult = checkHealthData(record);

        if (alertResult.shouldAlert()) {
            // 检查今天是否已经生成过健康数据异常预警
            boolean alreadyAlerted = alarmRepository.existsTodayAlarm(
                    record.getElder().getUserId(),
                    "健康数据异常预警"
            );

            if (!alreadyAlerted) {
                // 触发报警
                alarmService.triggerAlarm(
                        record.getElder().getUserId(),
                        "健康数据异常预警",
                        alertResult.getAlertDescription()
                );
                log.info("为老人 {} 创建健康数据异常预警: {}",
                        record.getElder().getUserId(),
                        alertResult.getAlertDescription());
            } else {
                log.info("老人 {} 今天已有健康数据异常预警，跳过创建",
                        record.getElder().getUserId());
            }
        }
    }

    /**
     * 检查健康数据是否异常
     */
    private HealthAlertResult checkHealthData(HealthRecord record) {
        StringBuilder alertDescription = new StringBuilder();
        boolean shouldAlert = false;

        // 检查血压异常
        if (record.getSystolicPressure() != null) {
            if (record.getSystolicPressure() > SYSTOLIC_HIGH) {
                alertDescription.append("收缩压偏高(")
                        .append(record.getSystolicPressure())
                        .append("mmHg) ");
                shouldAlert = true;
            } else if (record.getSystolicPressure() < SYSTOLIC_LOW) {
                alertDescription.append("收缩压偏低(")
                        .append(record.getSystolicPressure())
                        .append("mmHg) ");
                shouldAlert = true;
            }
        }

        if (record.getDiastolicPressure() != null) {
            if (record.getDiastolicPressure() > DIASTOLIC_HIGH) {
                alertDescription.append("舒张压偏高(")
                        .append(record.getDiastolicPressure())
                        .append("mmHg) ");
                shouldAlert = true;
            } else if (record.getDiastolicPressure() < DIASTOLIC_LOW) {
                alertDescription.append("舒张压偏低(")
                        .append(record.getDiastolicPressure())
                        .append("mmHg) ");
                shouldAlert = true;
            }
        }

        // 检查心率异常
        if (record.getHeartRate() != null) {
            if (record.getHeartRate() > HEART_RATE_HIGH) {
                alertDescription.append("心率过快(")
                        .append(record.getHeartRate())
                        .append("次/分钟)");
                shouldAlert = true;
            } else if (record.getHeartRate() < HEART_RATE_LOW) {
                alertDescription.append("心率过缓(")
                        .append(record.getHeartRate())
                        .append("次/分钟)");
                shouldAlert = true;
            }
        }

        return new HealthAlertResult(shouldAlert, alertDescription.toString().trim());
    }

    /**
     * 健康预警结果类
     */
    private static class HealthAlertResult {
        private final boolean shouldAlert;
        private final String alertDescription;

        public HealthAlertResult(boolean shouldAlert, String alertDescription) {
            this.shouldAlert = shouldAlert;
            this.alertDescription = alertDescription;
        }

        public boolean shouldAlert() {
            return shouldAlert;
        }

        public String getAlertDescription() {
            return alertDescription;
        }
    }

    /**
     * 获取老人的所有健康记录
     */
    @Override
    public List<HealthRecord> getHealthRecordsByElderId(Long elderId) {
        return healthRecordRepository.findByElder_UserIdOrderByRecordDateDesc(elderId);
    }

    /**
     * 获取最新的健康记录
     */
    @Override
    public HealthRecord getLatestHealthRecord(Long elderId) {
        List<HealthRecord> records = healthRecordRepository.findByElder_UserIdOrderByRecordDateDesc(elderId);
        return records.isEmpty() ? null : records.get(0);
    }

    /**
     * 验证健康数据范围
     */
    private void validateHealthData(HealthRecord record) {
        if (record.getSystolicPressure() != null &&
                (record.getSystolicPressure() < 50 || record.getSystolicPressure() > 250)) {
            throw new RuntimeException("收缩压范围应在50-250 mmHg之间");
        }

        if (record.getDiastolicPressure() != null &&
                (record.getDiastolicPressure() < 30 || record.getDiastolicPressure() > 150)) {
            throw new RuntimeException("舒张压范围应在30-150 mmHg之间");
        }

        if (record.getHeartRate() != null &&
                (record.getHeartRate() < 30 || record.getHeartRate() > 200)) {
            throw new RuntimeException("心率范围应在30-200次/分钟之间");
        }
    }

    /**
     * 获取今天的健康记录 - 优化版
     */
    @Override
    public HealthRecord getTodayHealthRecord(Long elderId) {
        return healthRecordRepository.findTodayHealthRecord(elderId).orElse(null);
    }

    /**
     * 新增：批量检测老人的健康数据
     */
    @Transactional
    public void batchCheckHealthData(Long elderId) {
        List<HealthRecord> records = healthRecordRepository.findByElder_UserIdOrderByRecordDateDesc(elderId);

        // 只检测最近7天的记录，避免历史数据过多
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);

        for (HealthRecord record : records) {
            if (record.getRecordDate().isBefore(sevenDaysAgo)) {
                break; // 超过7天的不再检测
            }
            checkHealthDataAndTriggerAlarm(record);
        }
    }
}
package com.huangbin.smartcommunityelderlycare.controller;

import com.huangbin.smartcommunityelderlycare.common.Result;
import com.huangbin.smartcommunityelderlycare.entity.AlarmRecord;
import com.huangbin.smartcommunityelderlycare.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emergency")
@CrossOrigin
public class EmergencyController {

    @Autowired
    private AlarmService alarmService;

    /**
     * 老人一键紧急求助
     */
    @PostMapping("/help/{elderId}")
    public Result<?> emergencyHelp(@PathVariable Long elderId) {
        try {
            alarmService.triggerAlarm(elderId, "手动一键求助", "老人触发紧急求助按钮");
            return Result.success("紧急求助已发送，工作人员将尽快联系您", null);
        } catch (Exception e) {
            return Result.error("求助失败: " + e.getMessage());
        }
    }

    /**
     * 获取老人的紧急求助记录
     */
    @GetMapping("/records/{elderId}")
    public Result<?> getEmergencyRecords(@PathVariable Long elderId) {
        try {
            List<AlarmRecord> records = alarmService.getAlarmsByElder(elderId);
            return Result.success("查询成功", records);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有待处理的紧急求助
     */
    @GetMapping("/pending")
    public Result<?> getPendingAlerts() {
        try {
            List<AlarmRecord> pendingAlerts = alarmService.getPendingAlarms();
            return Result.success("查询成功", pendingAlerts);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 处理紧急求助
     */
    @PostMapping("/process/{alertId}")
    public Result<?> processAlert(@PathVariable Long alertId,
                                  @RequestParam Long staffId,
                                  @RequestParam String notes) {
        try {
            alarmService.resolveAlarm(alertId, notes, staffId);
            return Result.success("求助已处理");
        } catch (Exception e) {
            return Result.error("处理失败: " + e.getMessage());
        }
    }
}
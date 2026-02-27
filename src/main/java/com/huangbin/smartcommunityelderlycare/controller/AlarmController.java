package com.huangbin.smartcommunityelderlycare.controller;

import com.huangbin.smartcommunityelderlycare.entity.AlarmRecord;
import com.huangbin.smartcommunityelderlycare.repository.AlarmRepository;
import com.huangbin.smartcommunityelderlycare.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;
    private final AlarmRepository alarmRepository;

    // 获取所有报警
    @GetMapping
    public ResponseEntity<List<AlarmRecord>> getAllAlarms() {
        return ResponseEntity.ok(alarmService.getAllAlarms());
    }

    // 获取待处理报警
    @GetMapping("/pending")
    public ResponseEntity<List<AlarmRecord>> getPendingAlarms() {
        return ResponseEntity.ok(alarmService.getPendingAlarms());
    }

    // 获取老人的报警记录
    @GetMapping("/elder/{elderId}")
    public ResponseEntity<List<AlarmRecord>> getAlarmsByElder(@PathVariable Long elderId) {
        return ResponseEntity.ok(alarmService.getAlarmsByElder(elderId));
    }

    // 手动触发报警
    @PostMapping("/trigger")
    public ResponseEntity<Map<String, Object>> triggerAlarm(@RequestBody Map<String, Object> request) {
        Long elderId = Long.valueOf(request.get("elderId").toString());
        String alarmType = request.get("alarmType").toString();
        String description = request.get("description").toString();

        alarmService.triggerAlarm(elderId, alarmType, description);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "报警触发成功");
        response.put("elderId", elderId);
        response.put("alarmType", alarmType);

        return ResponseEntity.ok(response);
    }

    // 处理报警 - 返回更详细的响应
    @PostMapping("/{alarmId}/resolve")
    public ResponseEntity<Map<String, Object>> resolveAlarm(
            @PathVariable Long alarmId,
            @RequestBody Map<String, Object> request) {

        String handleNotes = request.get("handleNotes").toString();
        Long handlerId = Long.valueOf(request.get("handlerId").toString());

        alarmService.resolveAlarm(alarmId, handleNotes, handlerId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "报警处理成功");
        response.put("alarmId", alarmId);
        response.put("handlerId", handlerId);
        response.put("handledAt", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    // 统计待处理报警数量
    @GetMapping("/count/pending")
    public ResponseEntity<Map<String, Long>> countPendingAlarms() {
        long count = alarmService.countPendingAlarms();
        return ResponseEntity.ok(Map.of("pendingCount", count));
    }

    // 按类型获取报警
    @GetMapping("/type/{alarmType}")
    public ResponseEntity<List<AlarmRecord>> getAlarmsByType(@PathVariable String alarmType) {
        return ResponseEntity.ok(alarmService.getAlarmsByType(alarmType));
    }

    // 按时间范围获取报警
    @GetMapping("/time-range")
    public ResponseEntity<List<AlarmRecord>> getAlarmsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        return ResponseEntity.ok(alarmService.getAlarmsByTimeRange(start, end));
    }

    // 触发签到缺失预警
    @PostMapping("/missed-checkin/{elderId}")
    public ResponseEntity<Map<String, Object>> triggerMissedCheckinAlarm(@PathVariable Long elderId) {
        alarmService.handleMissedCheckIn(elderId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "签到缺失预警已触发");
        response.put("elderId", elderId);

        return ResponseEntity.ok(response);
    }

    // ========== 调试接口 ==========

    // 调试接口：获取所有报警（原生SQL）
    @GetMapping("/debug/all-native")
    public ResponseEntity<List<AlarmRecord>> getAllAlarmsNative() {
        List<AlarmRecord> alarms = alarmRepository.findAllNative();
        return ResponseEntity.ok(alarms);
    }

    // 调试接口：按状态查询（原生SQL）
    @GetMapping("/debug/by-status/{status}")
    public ResponseEntity<List<AlarmRecord>> getAlarmsByStatus(@PathVariable String status) {
        List<AlarmRecord> alarms = alarmRepository.findByStatusNative(status);
        return ResponseEntity.ok(alarms);
    }

    // 调试接口：获取单个报警详情
    @GetMapping("/debug/{alarmId}")
    public ResponseEntity<AlarmRecord> getAlarmDetail(@PathVariable Long alarmId) {
        AlarmRecord alarm = alarmRepository.findById(alarmId).orElse(null);
        if (alarm == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(alarm);
    }

    // 调试接口：检查数据库连接
    @GetMapping("/debug/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        long totalCount = alarmRepository.count();
        long pendingCount = alarmRepository.countByStatus("待处理");
        long resolvedCount = alarmRepository.countByStatus("已处理");

        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", totalCount);
        result.put("pendingCount", pendingCount);
        result.put("resolvedCount", resolvedCount);
        result.put("status", "数据库连接正常");

        return ResponseEntity.ok(result);
    }
}
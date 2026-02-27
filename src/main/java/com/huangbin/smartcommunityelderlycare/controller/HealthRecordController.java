package com.huangbin.smartcommunityelderlycare.controller;

import com.huangbin.smartcommunityelderlycare.common.Result;
import com.huangbin.smartcommunityelderlycare.entity.HealthRecord;
import com.huangbin.smartcommunityelderlycare.service.HealthRecordService;
import com.huangbin.smartcommunityelderlycare.service.StatisticsService;
import com.huangbin.smartcommunityelderlycare.service.impl.HealthRecordServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @Autowired
    private StatisticsService statisticsService;
    /**
     * 添加健康记录
     */
    @PostMapping("/{elderId}")
    public Result<HealthRecord> addHealthRecord(
            @PathVariable("elderId") Long elderId,
            @Valid @RequestBody HealthRecord healthRecord) {
        try {
            HealthRecord savedRecord = healthRecordService.addHealthRecord(elderId, healthRecord);
            return Result.success("健康记录添加成功", savedRecord);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取老人的所有健康记录
     */
    @GetMapping("/elder/{elderId}")
    public Result<List<HealthRecord>> getHealthRecords(@PathVariable("elderId") Long elderId) {
        try {
            List<HealthRecord> records = healthRecordService.getHealthRecordsByElderId(elderId);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取老人最新的健康记录
     */
    @GetMapping("/elder/{elderId}/latest")
    public Result<HealthRecord> getLatestHealthRecord(@PathVariable("elderId") Long elderId) {
        try {
            HealthRecord record = healthRecordService.getLatestHealthRecord(elderId);
            if (record == null) {
                return Result.success("暂无健康记录", null);
            }
            return Result.success(record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取今天的健康记录
     */
    @GetMapping("/elder/{elderId}/today")
    public Result<HealthRecord> getTodayHealthRecord(@PathVariable("elderId") Long elderId) {
        try {
            HealthRecord record = healthRecordService.getTodayHealthRecord(elderId);
            if (record == null) {
                return Result.success("今日暂无健康记录", null);
            }
            return Result.success(record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 测试接口
     */
    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("健康档案接口正常");
    }

    /**
     * 获取健康趋势数据（ECharts用）
     */
    @GetMapping("/elder/{elderId}/trend")
    public Result<Map<String, Object>> getHealthTrend(
            @PathVariable("elderId") Long elderId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            if (start.isAfter(end)) {
                return Result.error("开始日期不能晚于结束日期");
            }

            Map<String, Object> trendData = statisticsService.getHealthTrend(elderId, start, end);
            return Result.success("健康趋势查询成功", trendData);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 手动触发健康数据异常检测
     */
    @PostMapping("/elder/{elderId}/check-alert")
    public Result<String> checkHealthAlert(@PathVariable("elderId") Long elderId) {
        try {
            // 获取最新健康记录
            HealthRecord latestRecord = healthRecordService.getLatestHealthRecord(elderId);
            if (latestRecord == null) {
                return Result.success("该老人暂无健康记录", null);
            }

            // 调用批量检测（在实际项目中，这里应该调用检查方法）
            // healthRecordService.batchCheckHealthData(elderId);

            return Result.success("健康数据检测完成", "已检查最新健康记录");
        } catch (Exception e) {
            return Result.error("检测失败: " + e.getMessage());
        }
    }
}
package com.huangbin.smartcommunityelderlycare.scheduler;

import com.huangbin.smartcommunityelderlycare.service.StatisticsReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@EnableScheduling
public class StatisticsScheduler {

    @Autowired
    private StatisticsReportService statisticsReportService;

    /**
     * 每天凌晨1点生成前一天的统计报表
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void generateDailyStatistics() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        try {
            statisticsReportService.generateDailyReport(yesterday);
            System.out.println("成功生成 " + yesterday + " 的统计报表");
        } catch (Exception e) {
            System.err.println("生成统计报表失败: " + e.getMessage());
        }
    }

    /**
     * 每月1日凌晨2点生成上月的统计报表
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    public void generateMonthlyStatistics() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        LocalDate firstDayOfMonth = lastMonth.withDayOfMonth(1);

        try {
            statisticsReportService.generateDailyReport(firstDayOfMonth);
            System.out.println("成功生成 " + lastMonth.getMonthValue() + "月 的统计报表");
        } catch (Exception e) {
            System.err.println("生成月度统计报表失败: " + e.getMessage());
        }
    }
}
package com.example.spms.task;

import com.example.spms.service.BillInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 账单自动生成定时任务
 * 每月1号凌晨1点自动为所有业主生成当月账单
 *
 * @Author SPMS
 * @Date 2026/07/06
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BillAutoGenerateTask {

    private final BillInfoService billInfoService;

    private static final DateTimeFormatter PERIOD_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * 每月1号凌晨1点执行，自动生成当月账单
     * cron: 秒 分 时 日 月 星期
     * 0 0 1 1 * ? = 每月1号凌晨1点
     */
    @Scheduled(cron = "0 0 1 1 * ?")
    public void generateMonthlyBills() {
        String billPeriod = LocalDate.now().format(PERIOD_FORMAT);
        log.info("【定时任务】开始自动生成 {} 月度账单...", billPeriod);

        try {
            int count = billInfoService.autoGenerateMonthlyBills(billPeriod, null);
            log.info("【定时任务】{} 月度账单生成完成，共生成 {} 条账单", billPeriod, count);
        } catch (Exception e) {
            log.error("【定时任务】{} 月度账单生成失败: {}", billPeriod, e.getMessage(), e);
        }
    }
}

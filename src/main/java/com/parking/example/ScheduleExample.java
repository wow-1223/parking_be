package com.parking.example;

import com.parking.handler.schedule.DynamicScheduleManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduleExample {

    @Autowired
    private DynamicScheduleManager scheduleManager;

    public void scheduleExample() {
        // 添加定时任务
        scheduleManager.addCronTask("task1", () -> {
            log.info("Execute dynamic task1");
        }, "0/5 * * * * ?");  // 每5秒执行一次

        // 更新定时任务
        scheduleManager.updateCronTask("task1", () -> {
            log.info("Execute updated task1");
        }, "0/10 * * * * ?");  // 每10秒执行一次

        // 检查任务状态
        boolean isRunning = scheduleManager.isTaskRunning("task1");
        log.info("Task1 is running: {}", isRunning);

        // 移除定时任务
        scheduleManager.removeCronTask("task1");
    }
}
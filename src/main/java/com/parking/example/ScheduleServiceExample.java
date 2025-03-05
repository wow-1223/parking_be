package com.parking.example;

import com.parking.handler.schedule.ScheduleHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduleServiceExample {

    @Autowired
    private ScheduleHandler scheduleHandler;

    public void example() {
        // 1. 执行普通定时任务 - 每5秒执行一次
        scheduleHandler.executeScheduleTask("task1", "0/5 * * * * ?");

        // 2. 执行带返回值的定时任务 - 每10秒执行一次
        scheduleHandler.executeScheduleTaskWithResult("task2", "0/10 * * * * ?");

        // 3. 执行异步定时任务 - 每15秒执行一次
        scheduleHandler.executeAsyncScheduleTask("task3", "0/15 * * * * ?");

        try {
            Thread.sleep(30000); // 等待30秒观察任务执行

            // 4. 更新任务执行周期 - 改为每20秒执行一次
            scheduleHandler.updateScheduleTask("task1", "0/20 * * * * ?");

            Thread.sleep(30000); // 再等待30秒观察更新后的执行

            // 5. 停止所有任务
            scheduleHandler.stopScheduleTask("task1");
            scheduleHandler.stopScheduleTask("task2");
            scheduleHandler.stopScheduleTask("task3");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
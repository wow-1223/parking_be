package com.parking.handler.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
public class DynamicScheduleManager {

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    // 存储定时任务
    private final Map<String, ScheduledFuture<?>> scheduleMap = new ConcurrentHashMap<>();

    /**
     * 添加定时任务
     */
    public void addCronTask(String taskId, Runnable task, String cron) {
        // 先移除已存在的任务
        removeCronTask(taskId);

        // 创建新任务
        ScheduledFuture<?> future = taskScheduler.schedule(task, new CronTrigger(cron));
        scheduleMap.put(taskId, future);
        log.info("Add cron task success, taskId: {}, cron: {}", taskId, cron);
    }

    /**
     * 移除定时任务
     */
    public void removeCronTask(String taskId) {
        ScheduledFuture<?> future = scheduleMap.get(taskId);
        if (future != null) {
            future.cancel(true);
            scheduleMap.remove(taskId);
            log.info("Remove cron task success, taskId: {}", taskId);
        }
    }

    /**
     * 更新定时任务cron表达式
     */
    public void updateCronTask(String taskId, Runnable task, String cron) {
        addCronTask(taskId, task, cron);
        log.info("Update cron task success, taskId: {}, cron: {}", taskId, cron);
    }

    /**
     * 获取定时任务状态
     */
    public boolean isTaskRunning(String taskId) {
        ScheduledFuture<?> future = scheduleMap.get(taskId);
        return future != null && !future.isCancelled();
    }
}
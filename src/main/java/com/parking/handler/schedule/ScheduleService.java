package com.parking.handler.schedule;

import com.parking.handler.task.ThreadPoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ScheduleService {

    @Autowired
    private DynamicScheduleManager scheduleManager;

    @Autowired
    private ThreadPoolService threadPoolService;

    /**
     * 执行定时任务
     */
    public void executeScheduleTask(String taskId, String cron) {
        scheduleManager.addCronTask(taskId, () -> {
            threadPoolService.executeTask(() -> {
                try {
                    log.info("Executing scheduled task: {}", taskId);
                    // 在这里执行具体的任务逻辑
                    TimeUnit.SECONDS.sleep(2); // 模拟任务执行
                } catch (Exception e) {
                    log.error("Failed to execute task: {}", taskId, e);
                }
            });
        }, cron);
    }

    /**
     * 执行带返回值的定时任务
     */
    public void executeScheduleTaskWithResult(String taskId, String cron) {
        scheduleManager.addCronTask(taskId, () -> {
            Future<String> future = threadPoolService.submitTask(() -> {
                try {
                    log.info("Executing scheduled task with result: {}", taskId);
                    TimeUnit.SECONDS.sleep(2); // 模拟任务执行
                    return "Task " + taskId + " completed";
                } catch (Exception e) {
                    log.error("Failed to execute task: {}", taskId, e);
                    throw e;
                }
            });

            try {
                String result = future.get(5, TimeUnit.SECONDS);
                log.info("Task result: {}", result);
            } catch (Exception e) {
                log.error("Failed to get task result", e);
            }
        }, cron);
    }

    /**
     * 执行异步定时任务
     */
    public void executeAsyncScheduleTask(String taskId, String cron) {
        scheduleManager.addCronTask(taskId, () -> {
            threadPoolService.executeAsync(() -> {
                try {
                    log.info("Executing async scheduled task: {}", taskId);
                    TimeUnit.SECONDS.sleep(2); // 模拟任务执行
                } catch (Exception e) {
                    log.error("Failed to execute async task: {}", taskId, e);
                }
            });
        }, cron);
    }

    /**
     * 更新定时任务
     */
    public void updateScheduleTask(String taskId, String newCron) {
        if (scheduleManager.isTaskRunning(taskId)) {
            scheduleManager.updateCronTask(taskId, () -> {
                threadPoolService.executeTask(() -> {
                    try {
                        log.info("Executing updated task: {}", taskId);
                        TimeUnit.SECONDS.sleep(2);
                    } catch (Exception e) {
                        log.error("Failed to execute updated task: {}", taskId, e);
                    }
                });
            }, newCron);
        }
    }

    /**
     * 停止定时任务
     */
    public void stopScheduleTask(String taskId) {
        scheduleManager.removeCronTask(taskId);
    }
}
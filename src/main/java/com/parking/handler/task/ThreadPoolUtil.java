package com.parking.handler.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Slf4j
@Component
public class ThreadPoolUtil {

    @Autowired
    @Qualifier("asyncExecutor")
    private ThreadPoolTaskExecutor asyncExecutor;

    @Autowired
    @Qualifier("scheduleExecutor")
    private ThreadPoolTaskExecutor scheduleExecutor;

    /**
     * 执行异步任务
     */
    public void executeAsync(Runnable task) {
        asyncExecutor.execute(() -> {
            try {
                task.run();
            } catch (Exception e) {
                log.error("Async task execution failed", e);
            }
        });
    }

    /**
     * 执行异步任务并获取结果
     */
    public <T> Future<T> submitAsync(Callable<T> task) {
        return asyncExecutor.submit(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                log.error("Async task execution failed", e);
                throw e;
            }
        });
    }

    /**
     * 执行定时任务
     */
    public void executeTask(Runnable task) {
        scheduleExecutor.execute(() -> {
            try {
                task.run();
            } catch (Exception e) {
                log.error("Task execution failed", e);
            }
        });
    }

    /**
     * 执行定时任务并获取结果
     */
    public <T> Future<T> submitTask(Callable<T> task) {
        return scheduleExecutor.submit(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                log.error("Task execution failed", e);
                throw e;
            }
        });
    }

    /**
     * 获取异步执行器活跃线程数
     */
    public int getAsyncActiveCount() {
        return asyncExecutor.getActiveCount();
    }

    /**
     * 获取任务执行器活跃线程数
     */
    public int getTaskActiveCount() {
        return scheduleExecutor.getActiveCount();
    }

    /**
     * 获取异步执行器队列大小
     */
    public int getAsyncQueueSize() {
        return asyncExecutor.getThreadPoolExecutor().getQueue().size();
    }

    /**
     * 获取任务执行器队列大小
     */
    public int getTaskQueueSize() {
        return scheduleExecutor.getThreadPoolExecutor().getQueue().size();
    }

    /**
     * 关闭线程池
     */
    public void shutdown() {
        asyncExecutor.shutdown();
        scheduleExecutor.shutdown();
    }
}
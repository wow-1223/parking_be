package com.parking.example;

import com.parking.handler.task.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Slf4j
@Component
public class ThreadPoolExample {

    @Autowired
    private ThreadPoolUtil threadPoolUtil;

    public void asyncExample() {
        // 执行异步任务
        threadPoolUtil.executeAsync(() -> {
            log.info("Executing async task");
            // 异步任务逻辑
        });

        // 执行异步任务并获取结果
        Future<String> future = threadPoolUtil.submitAsync(() -> {
            log.info("Executing async task with result");
            return "Task Result";
        });

        try {
            String result = future.get(); // 等待任务完成并获取结果
            log.info("Task result: {}", result);
        } catch (Exception e) {
            log.error("Failed to get task result", e);
        }
    }

    public void taskExample() {
        // 执行定时任务
        threadPoolUtil.executeTask(() -> {
            log.info("Executing scheduled task");
            // 定时任务逻辑
        });

        // 执行定时任务并获取结果
        Future<String> future = threadPoolUtil.submitTask(() -> {
            log.info("Executing scheduled task with result");
            return "Task Result";
        });

        try {
            String result = future.get();
            log.info("Task result: {}", result);
        } catch (Exception e) {
            log.error("Failed to get task result", e);
        }
    }
}
package com.parking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@Configuration
public class ThreadPoolConfig {

    /**
     * 核心线程池大小
     */
    private static final int CORE_POOL_SIZE = 4;

    /**
     * 最大线程池大小
     */
    private static final int MAX_POOL_SIZE = 20;

    /**
     * 队列容量
     */
    private static final int QUEUE_CAPACITY = 200;

    /**
     * 线程池维护线程所允许的空闲时间(秒)
     */
    private static final int KEEP_ALIVE_SECONDS = 300;

    /**
     * 主要用于处理异步任务，通常是一次性的、临时性的任务
     * 配合@Async注解使用，用于异步方法执行
     * 适合处理耗时的后台任务，如:
     * 发送邮件/短信
     * 异步通知
     * 数据处理
     * 文件上传/下载
     * 第三方接口调用
     * @return Executor
     */
    @Bean(name = "asyncExecutor")
    public ThreadPoolTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        executor.setThreadNamePrefix("Async-");

        // 线程池对拒绝任务的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        executor.initialize();
        return executor;
    }

    /**
     * 主要用于处理定时任务、周期性任务
     * 配合@Scheduled注解使用
     * 适合处理定时、周期性的任务，如:
     * 定时数据统计
     * 定时清理任务
     * 定时同步数据
     * 定时检查任务
     * 周期性维护任务
     * @return Executor
     */
    @Bean(name = "scheduleExecutor")
    public ThreadPoolTaskExecutor scheduleExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        executor.setThreadNamePrefix("Task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
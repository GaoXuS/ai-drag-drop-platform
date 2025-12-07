package com.aidragdrop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Value("${app.task-pool-size:10}")
    private int taskPoolSize;
    
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(taskPoolSize);
        executor.setMaxPoolSize(taskPoolSize * 2);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("task-executor-");
        executor.initialize();
        return executor;
    }
}


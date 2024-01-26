package ru.maliutin.diesel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "mailExecutor")
    public Executor mailExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // Установка базового размера пула потоков
        executor.setMaxPoolSize(2);  // Установка макс. размера пула потоков
        executor.setQueueCapacity(500); // Емкость очереди задач
        executor.setThreadNamePrefix("MailThread-"); // Имя потока
        executor.initialize(); // Инициализ. настроек пула потоков.
        return executor;
    }


}

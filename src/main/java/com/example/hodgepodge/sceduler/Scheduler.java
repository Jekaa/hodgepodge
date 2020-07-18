package com.example.hodgepodge.sceduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@Slf4j
public class Scheduler {

    @Bean
    @Scheduled(fixedRate = 3000)
    public void scheduledLogger() {
        log.info("Scheduler working");
    }
}

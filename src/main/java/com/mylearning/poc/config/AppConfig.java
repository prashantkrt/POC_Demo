package com.mylearning.poc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {
    @Bean
    public ExecutorService taskExecutor() {
        return Executors.newFixedThreadPool(10); // tune as needed
    }
}

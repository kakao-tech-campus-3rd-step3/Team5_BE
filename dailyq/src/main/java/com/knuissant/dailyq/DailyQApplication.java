package com.knuissant.dailyq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableRetry
@EnableAsync
@SpringBootApplication
public class DailyQApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyQApplication.class, args);
    }
}

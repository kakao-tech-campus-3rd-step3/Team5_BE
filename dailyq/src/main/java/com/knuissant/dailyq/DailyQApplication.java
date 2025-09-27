package com.knuissant.dailyq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class DailyQApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyQApplication.class, args);
    }
}

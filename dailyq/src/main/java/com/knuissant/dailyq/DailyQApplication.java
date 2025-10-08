package com.knuissant.dailyq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;

import com.knuissant.dailyq.config.JwtProperties;

@EnableConfigurationProperties(JwtProperties.class)
@EnableRetry
@SpringBootApplication
public class DailyQApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyQApplication.class, args);
    }
}

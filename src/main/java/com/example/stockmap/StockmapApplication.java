package com.example.stockmap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class StockmapApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockmapApplication.class, args);
    }
}

package com.lithoapp.drainage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DrainageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrainageServiceApplication.class, args);
    }
}

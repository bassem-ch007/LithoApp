package com.lithoapp.drainage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients(basePackages = "com.lithoapp.drainage.client")
public class DrainageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrainageServiceApplication.class, args);
    }
}

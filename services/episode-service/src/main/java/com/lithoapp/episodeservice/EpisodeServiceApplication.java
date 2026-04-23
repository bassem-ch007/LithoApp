package com.lithoapp.episodeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.lithoapp.episodeservice.client")
public class EpisodeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EpisodeServiceApplication.class, args);
    }
}

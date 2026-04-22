package com.lithoapp.episodeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Future: @EnableFeignClients(basePackages = "com.lithoapp.episodeservice.client")
// Future: @EnableDiscoveryClient

@SpringBootApplication
public class EpisodeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EpisodeServiceApplication.class, args);
    }
}

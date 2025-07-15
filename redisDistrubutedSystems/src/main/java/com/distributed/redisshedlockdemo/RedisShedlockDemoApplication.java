package com.distributed.redisshedlockdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RedisShedlockDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisShedlockDemoApplication.class, args);
    }
}
package com.distributed.redisshedlockdemo.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * ShedLock Configuration for Distributed Lock Management
 * 
 * This configuration ensures that scheduled tasks execute only once
 * across multiple instances of the application in a distributed environment.
 * 
 * Key Features:
 * - Uses Redis as the lock provider
 * - Prevents duplicate task execution
 * - Automatic lock cleanup on task completion
 * - Configurable lock durations and timeouts
 */
@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class ShedLockConfig {

    /**
     * Creates a Redis-based lock provider for ShedLock
     * 
     * @param connectionFactory Redis connection factory
     * @return LockProvider instance configured for Redis
     */
    @Bean
    public LockProvider lockProvider(RedisConnectionFactory connectionFactory) {
        return new RedisLockProvider(connectionFactory, "shedlock");
    }
}
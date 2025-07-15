package com.distributed.redisshedlockdemo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Monitoring Controller for ShedLock Demo
 * 
 * Provides REST endpoints to monitor:
 * - Task execution history
 * - Current lock status
 * - Redis keys and data
 * - Application health
 */
@Slf4j
@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitoringController {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Get execution history for all scheduled tasks
     */
    @GetMapping("/executions")
    public ResponseEntity<Map<String, Object>> getExecutionHistory() {
        log.info("Getting execution history for all tasks");

        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", LocalDateTime.now().format(FORMATTER));
        result.put("instance_id", System.getProperty("instance.id", "unknown"));

        // Get execution data for different tasks
        result.put("data_sync_executions", getListData("data_sync_executions", 10));
        result.put("daily_reports", getListData("daily_reports", 10));
        result.put("cleanup_executions", getListData("cleanup_executions", 10));
        result.put("health_checks", getListData("health_checks", 10));

        return ResponseEntity.ok(result);
    }

    /**
     * Get current ShedLock status and active locks
     */
    @GetMapping("/locks")
    public ResponseEntity<Map<String, Object>> getCurrentLocks() {
        log.info("Getting current ShedLock status");

        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", LocalDateTime.now().format(FORMATTER));

        // Get all keys with shedlock prefix
        Set<String> lockKeys = redisTemplate.keys("shedlock:*");
        Map<String, Object> activeLocks = new HashMap<>();

        if (lockKeys != null) {
            for (String key : lockKeys) {
                Object lockValue = redisTemplate.opsForValue().get(key);
                Long ttl = redisTemplate.getExpire(key);

                Map<String, Object> lockInfo = new HashMap<>();
                lockInfo.put("value", lockValue);
                lockInfo.put("ttl_seconds", ttl);
                lockInfo.put("expires_at",
                        ttl > 0 ? LocalDateTime.now().plusSeconds(ttl).format(FORMATTER) : "persistent");

                activeLocks.put(key, lockInfo);
            }
        }

        result.put("active_locks", activeLocks);
        result.put("total_locks", activeLocks.size());

        return ResponseEntity.ok(result);
    }

    /**
     * Get all Redis keys for debugging
     */
    @GetMapping("/redis-keys")
    public ResponseEntity<Map<String, Object>> getRedisKeys() {
        log.info("Getting all Redis keys");

        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", LocalDateTime.now().format(FORMATTER));

        // Get all keys
        Set<String> allKeys = redisTemplate.keys("*");
        result.put("all_keys", allKeys);
        result.put("total_keys", allKeys != null ? allKeys.size() : 0);

        // Categorize keys
        Map<String, Integer> keyCategories = new HashMap<>();
        if (allKeys != null) {
            for (String key : allKeys) {
                if (key.startsWith("shedlock:")) {
                    keyCategories.merge("shedlock", 1, Integer::sum);
                } else if (key.contains("_executions") || key.contains("_reports") || key.contains("_checks")) {
                    keyCategories.merge("task_data", 1, Integer::sum);
                } else {
                    keyCategories.merge("other", 1, Integer::sum);
                }
            }
        }

        result.put("key_categories", keyCategories);

        return ResponseEntity.ok(result);
    }

    /**
     * Get specific task execution details
     */
    @GetMapping("/task")
    public ResponseEntity<Map<String, Object>> getTaskDetails(@RequestParam String taskName) {
        log.info("Getting details for task: {}", taskName);

        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", LocalDateTime.now().format(FORMATTER));
        result.put("task_name", taskName);

        // Map task names to Redis keys
        String redisKey = switch (taskName.toLowerCase()) {
            case "datasync" -> "data_sync_executions";
            case "report" -> "daily_reports";
            case "cleanup" -> "cleanup_executions";
            case "health" -> "health_checks";
            default -> taskName;
        };

        List<Object> executions = getListData(redisKey, 20);
        result.put("executions", executions);
        result.put("execution_count", executions.size());

        // Check for active lock
        String lockKey = "shedlock:" + getLockName(taskName);
        Object lockValue = redisTemplate.opsForValue().get(lockKey);
        Long ttl = redisTemplate.getExpire(lockKey);

        Map<String, Object> lockInfo = new HashMap<>();
        lockInfo.put("is_locked", lockValue != null);
        lockInfo.put("lock_value", lockValue);
        lockInfo.put("ttl_seconds", ttl);
        lockInfo.put("lock_key", lockKey);

        result.put("lock_info", lockInfo);

        return ResponseEntity.ok(result);
    }

    /**
     * Application health and configuration
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", LocalDateTime.now().format(FORMATTER));
        result.put("status", "UP");
        result.put("instance_id", System.getProperty("instance.id", "unknown"));

        // Redis connectivity test
        try {
            redisTemplate.opsForValue().set("health_check_" + System.currentTimeMillis(), "OK");
            result.put("redis_status", "CONNECTED");
        } catch (Exception e) {
            result.put("redis_status", "DISCONNECTED");
            result.put("redis_error", e.getMessage());
        }

        // Get JVM info
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> jvmInfo = new HashMap<>();
        jvmInfo.put("processors", runtime.availableProcessors());
        jvmInfo.put("total_memory", runtime.totalMemory());
        jvmInfo.put("free_memory", runtime.freeMemory());
        jvmInfo.put("used_memory", runtime.totalMemory() - runtime.freeMemory());

        result.put("jvm_info", jvmInfo);

        return ResponseEntity.ok(result);
    }

    /**
     * Clear all task execution data (for testing)
     */
    @GetMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearData() {
        log.warn("Clearing all task execution data");

        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", LocalDateTime.now().format(FORMATTER));

        // Clear task execution data
        String[] keys = { "data_sync_executions", "daily_reports", "cleanup_executions", "health_checks" };
        int deletedKeys = 0;

        for (String key : keys) {
            if (Boolean.TRUE.equals(redisTemplate.delete(key))) {
                deletedKeys++;
            }
        }

        result.put("cleared_keys", deletedKeys);
        result.put("status", "DATA_CLEARED");

        return ResponseEntity.ok(result);
    }

    // Helper methods

    private List<Object> getListData(String key, int limit) {
        try {
            return redisTemplate.opsForList().range(key, 0, limit - 1);
        } catch (Exception e) {
            log.warn("Could not get list data for key: {}", key, e);
            return List.of();
        }
    }

    private String getLockName(String taskName) {
        return switch (taskName.toLowerCase()) {
            case "datasync" -> "dataSyncTask";
            case "report" -> "dailyReportGeneration";
            case "cleanup" -> "cleanupTask";
            case "health" -> "healthCheck";
            default -> taskName;
        };
    }
}
package com.distributed.redisshedlockdemo.service;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Data Processing Service demonstrating ShedLock functionality
 * 
 * This service contains multiple scheduled tasks that simulate real-world
 * scenarios:
 * 1. Data synchronization tasks
 * 2. Report generation
 * 3. Cleanup operations
 * 4. Health checks
 * 
 * Each task uses @SchedulerLock to ensure singleton execution across instances.
 */
@Slf4j
@Service
public class DataProcessingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String instanceId;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DataProcessingService(RedisTemplate<String, Object> redisTemplate,
            @Value("${app.instance.id}") String instanceId) {
        this.redisTemplate = redisTemplate;
        this.instanceId = instanceId;
        log.info("DataProcessingService initialized for instance: {}", instanceId);
    }

    /**
     * Critical data synchronization task that must run only once across all
     * instances
     * - Runs every 30 seconds
     * - Lock held for maximum 25 seconds
     * - Lock acquired for at least 5 seconds
     */
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    @SchedulerLock(name = "dataSyncTask", lockAtMostFor = "25s", lockAtLeastFor = "5s")
    public void synchronizeData() {
        log.info("🔄 [{}] Starting critical data synchronization...", instanceId);

        try {
            // Simulate data processing time (3-8 seconds)
            int processingTime = ThreadLocalRandom.current().nextInt(3000, 8000);
            Thread.sleep(processingTime);

            // Store execution info in Redis
            String executionKey = "data_sync_executions";
            String executionInfo = String.format("%s - Instance: %s, Duration: %dms",
                    LocalDateTime.now().format(FORMATTER), instanceId, processingTime);

            redisTemplate.opsForList().leftPush(executionKey, executionInfo);
            redisTemplate.expire(executionKey, 300, TimeUnit.SECONDS); // Keep for 5 minutes

            log.info("✅ [{}] Data synchronization completed successfully in {}ms", instanceId, processingTime);

        } catch (InterruptedException e) {
            log.error("❌ [{}] Data synchronization interrupted", instanceId, e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("❌ [{}] Data synchronization failed", instanceId, e);
        }
    }

    /**
     * Daily report generation task
     * - Runs every minute for demo purposes (would be daily in production)
     * - Lock held for maximum 2 minutes
     * - Lock acquired for at least 30 seconds
     */
    @Scheduled(fixedRate = 60000) // Every minute (demo frequency)
    @SchedulerLock(name = "dailyReportGeneration", lockAtMostFor = "2m", lockAtLeastFor = "30s")
    public void generateDailyReport() {
        log.info("📊 [{}] Starting daily report generation...", instanceId);

        try {
            // Simulate report generation (10-45 seconds)
            int processingTime = ThreadLocalRandom.current().nextInt(10000, 45000);
            Thread.sleep(processingTime);

            // Store report info in Redis
            String reportKey = "daily_reports";
            String reportInfo = String.format("%s - Instance: %s, Size: %dKB",
                    LocalDateTime.now().format(FORMATTER), instanceId,
                    ThreadLocalRandom.current().nextInt(500, 2000));

            redisTemplate.opsForList().leftPush(reportKey, reportInfo);
            redisTemplate.expire(reportKey, 86400, TimeUnit.SECONDS); // Keep for 24 hours

            log.info("✅ [{}] Daily report generated successfully in {}ms", instanceId, processingTime);

        } catch (InterruptedException e) {
            log.error("❌ [{}] Report generation interrupted", instanceId, e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("❌ [{}] Report generation failed", instanceId, e);
        }
    }

    /**
     * Cleanup task with shorter lock duration
     * - Runs every 2 minutes
     * - Lock held for maximum 1 minute
     * - Lock acquired for at least 10 seconds
     */
    @Scheduled(fixedRate = 120000) // Every 2 minutes
    @SchedulerLock(name = "cleanupTask", lockAtMostFor = "1m", lockAtLeastFor = "10s")
    public void performCleanup() {
        log.info("🧹 [{}] Starting cleanup operations...", instanceId);

        try {
            // Simulate cleanup operations (5-20 seconds)
            int processingTime = ThreadLocalRandom.current().nextInt(5000, 20000);
            Thread.sleep(processingTime);

            // Clean old execution records
            String cleanupKey = "cleanup_executions";
            String cleanupInfo = String.format("%s - Instance: %s, Cleaned: %d items",
                    LocalDateTime.now().format(FORMATTER), instanceId,
                    ThreadLocalRandom.current().nextInt(10, 100));

            redisTemplate.opsForList().leftPush(cleanupKey, cleanupInfo);
            redisTemplate.expire(cleanupKey, 600, TimeUnit.SECONDS); // Keep for 10 minutes

            log.info("✅ [{}] Cleanup completed successfully in {}ms", instanceId, processingTime);

        } catch (InterruptedException e) {
            log.error("❌ [{}] Cleanup interrupted", instanceId, e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("❌ [{}] Cleanup failed", instanceId, e);
        }
    }

    /**
     * Fast health check task
     * - Runs every 15 seconds
     * - Lock held for maximum 10 seconds
     * - Lock acquired for at least 2 seconds
     */
    @Scheduled(fixedRate = 15000) // Every 15 seconds
    @SchedulerLock(name = "healthCheck", lockAtMostFor = "10s", lockAtLeastFor = "2s")
    public void performHealthCheck() {
        log.info("❤️ [{}] Performing health check...", instanceId);

        try {
            // Simulate health check (1-3 seconds)
            int processingTime = ThreadLocalRandom.current().nextInt(1000, 3000);
            Thread.sleep(processingTime);

            // Store health check result
            String healthKey = "health_checks";
            String healthInfo = String.format("%s - Instance: %s, Status: %s",
                    LocalDateTime.now().format(FORMATTER), instanceId, "HEALTHY");

            redisTemplate.opsForList().leftPush(healthKey, healthInfo);
            redisTemplate.expire(healthKey, 180, TimeUnit.SECONDS); // Keep for 3 minutes

            log.info("✅ [{}] Health check completed in {}ms - HEALTHY", instanceId, processingTime);

        } catch (InterruptedException e) {
            log.error("❌ [{}] Health check interrupted", instanceId, e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("❌ [{}] Health check failed", instanceId, e);
        }
    }
}
package com.sca.smartcampusbackend.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache Configuration
 * Configures ConcurrentMapCacheManager for in-memory caching
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Create ConcurrentMapCacheManager with predefined cache names
     * Caches: courses, courseOfferings, faculties, enrollments
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "courses",
                "courseById",
                "courseByCode",
                "courseOfferings",
                "faculties",
                "enrollments",
                "events",
                "timetableEntries",
                "weather");
    }
}

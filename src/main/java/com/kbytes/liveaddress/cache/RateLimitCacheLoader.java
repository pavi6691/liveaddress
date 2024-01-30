package com.kbytes.liveaddress.cache;

import com.kbytes.liveaddress.configs.Constants;
import com.kbytes.liveaddress.persistence.sqldb.models.RateLimit;
import com.kbytes.liveaddress.persistence.sqldb.repositories.RateLimitRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import java.util.Map;

@Component
public class RateLimitCacheLoader {
    private final RateLimitRepository rateLimitRepository;
    private final CacheManager cacheManager;

    @Autowired
    public RateLimitCacheLoader(RateLimitRepository rateLimitRepository, CacheManager cacheManager) {
        this.rateLimitRepository = rateLimitRepository;
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void loadRateLimitsIntoCache() {
        Cache rateLimitCache = cacheManager.getCache(Constants.RATE_LIMIT_CACHE_NAME);
        Flux<RateLimit> rateLimits = rateLimitRepository.findAll();
        rateLimits.toIterable().forEach(rateLimit -> rateLimitCache.put(rateLimit.getClientId(), rateLimit));
    }

    /**
     * Also cron job to flush rate limit data in cache to database
     */
    @Scheduled(initialDelay = 10000, fixedRateString = "#{@flush_rate_limit_data_to_db_cron_millis}")
    @PreDestroy
    public void saveCacheDataToDatabase() {
        Cache rateLimitCache = cacheManager.getCache(Constants.RATE_LIMIT_CACHE_NAME);
        if (rateLimitCache != null) {
            Map<String, RateLimit> cache = (Map<String, RateLimit>) rateLimitCache.getNativeCache();
            for (Object key : cache.keySet()) {
                String cacheKey = key.toString();
                RateLimit rateLimit = (RateLimit) rateLimitCache.get(cacheKey).get();
                rateLimitRepository.save(rateLimit);
            }
        }
    }
}

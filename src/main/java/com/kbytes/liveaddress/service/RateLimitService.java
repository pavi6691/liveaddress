package com.kbytes.liveaddress.service;

import com.kbytes.liveaddress.configs.Constants;
import com.kbytes.liveaddress.persistence.sqldb.models.RateLimit;
import com.kbytes.liveaddress.persistence.sqldb.repositories.RateLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
public class RateLimitService {
    
    private RateLimitRepository rateLimitRepository;
    
    private CacheManager cacheManager;

    @Autowired
    public RateLimitService(RateLimitRepository rateLimitRepository, CacheManager cacheManager) {
        this.rateLimitRepository = rateLimitRepository;
        this.cacheManager = cacheManager;
    }
    
    public RateLimit update(String clientId, long maxRequestsPerDay) {
        Cache cache = cacheManager.getCache(Constants.RATE_LIMIT_CACHE_NAME);
        RateLimit rateLimit = (RateLimit) cache.get(clientId).get();
        if(rateLimit == null) {
            rateLimit = rateLimitRepository.findByClientId(clientId).subscribeOn(Schedulers.parallel()).block();
        }
        if(rateLimit == null) {
            rateLimit = new RateLimit();
            rateLimit.setClientId(clientId);
            rateLimit.setLastRequestTimestamp(new Timestamp(System.currentTimeMillis()));
        }
        rateLimit.setMaxRequestsPerDay(maxRequestsPerDay);
        cache.put(clientId,rateLimit);
        rateLimitRepository.save(rateLimit);
        return rateLimit;
    }

    public boolean exceedsRateLimit(String clientId) {
        Cache cache = cacheManager.getCache(Constants.RATE_LIMIT_CACHE_NAME);
        RateLimit rateLimit = (RateLimit) cache.get(clientId).get();
        if (rateLimit == null) {
            return true;
        }
        if (!isSameDay(rateLimit.getLastRequestTimestamp(), new Date())) {
            // Reset request count if it's a new day
            rateLimit.setRequestCount(0);
        }
        rateLimit.setRequestCount(rateLimit.getRequestCount() + 1);
        rateLimit.setLastRequestTimestamp(new Timestamp(System.currentTimeMillis()));
        cache.put(rateLimit.getClientId(),rateLimit);
        return rateLimit.getRequestCount() > rateLimit.getMaxRequestsPerDay();
    }

    private boolean isSameDay(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate1.isEqual(localDate2);
    }
 }

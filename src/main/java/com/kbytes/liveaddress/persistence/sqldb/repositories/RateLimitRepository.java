package com.kbytes.liveaddress.persistence.sqldb.repositories;

import com.kbytes.liveaddress.persistence.sqldb.models.RateLimit;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RateLimitRepository extends R2dbcRepository<RateLimit, Long> {
    Mono<RateLimit> findByClientId(String clientId);
}

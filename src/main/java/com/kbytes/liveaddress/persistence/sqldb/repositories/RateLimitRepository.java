package com.kbytes.liveaddress.persistence.sqldb.repositories;

import com.kbytes.liveaddress.persistence.sqldb.models.RateLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateLimitRepository extends JpaRepository<RateLimit, Long> {
    RateLimit findByClientId(String clientId);
}

package com.kbytes.liveaddress.persistence.sqldb.repositories;

import com.kbytes.liveaddress.persistence.sqldb.models.LiveAddress;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface LiveAddressRepo extends R2dbcRepository<LiveAddress,Integer> {

    @Query("SELECT * FROM live_addresses_imported LIMIT :limit")
    Flux<LiveAddress> findFirstN(int limit);

    @Query("SELECT * FROM live_addresses_imported WHERE id > :id LIMIT :limit")
    Flux<LiveAddress> findByIdGreaterThan(Long id, int limit);
    
    
}

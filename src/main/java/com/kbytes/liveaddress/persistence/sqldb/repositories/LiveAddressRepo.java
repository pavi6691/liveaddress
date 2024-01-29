package com.kbytes.liveaddress.persistence.sqldb.repositories;

import com.kbytes.liveaddress.persistence.sqldb.models.LiveAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveAddressRepo extends JpaRepository<LiveAddress,Integer> {
}

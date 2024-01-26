package com.kbytes.liveaddress.persistence.postgresql.repositories;

import com.kbytes.liveaddress.persistence.postgresql.models.PGLiveAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PGLiveAddressRepo extends JpaRepository<PGLiveAddress,Integer> {
}

package com.kbytes.liveaddress.persistence.elasticsearch.repositories;

import com.kbytes.liveaddress.persistence.elasticsearch.models.ESLiveAddress;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ESLiveAddressRepo extends ElasticsearchRepository<ESLiveAddress,Integer> {
    ESLiveAddress findTopByOrderByIdDesc();
}

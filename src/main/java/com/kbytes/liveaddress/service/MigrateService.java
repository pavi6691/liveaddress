package com.kbytes.liveaddress.service;
import com.kbytes.liveaddress.persistence.elasticsearch.models.ESLiveAddress;
import com.kbytes.liveaddress.persistence.elasticsearch.repositories.ESLiveAddressRepo;
import com.kbytes.liveaddress.persistence.sqldb.models.LiveAddress;
import com.kbytes.liveaddress.persistence.sqldb.repositories.LiveAddressRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service("MigrateService")
public class MigrateService {
    
    @Autowired
    LiveAddressRepo liveAddressRepo;
    
    @Autowired
    ElasticsearchOperations elasticsearchOperations;
    
    @Autowired 
    ESLiveAddressRepo esLiveAddressRepo;
    
    int duplicate = 0;
    @Value("${migrate.batchSize.write}")
    int batchSizeWrite;
    @Value("${migrate.batchSize.read}")
    int batchSizeRead;
    @Value("${indexName}")
    String indexName;

    public String migrate(int duplicate) {
        this.duplicate = duplicate;
        return migrate();
    }

    /**
     * Managed function, that sync any new data from database to elasticsearch when Scheduled / requested by API. 
     * Also it does duplicate number of entries when requested by API
     * @return Deatails with how many new entries are migrated and total entries present on elasticsearch
     */
//    @Scheduled(initialDelay = 10000, fixedRateString = "#{@migrate_cron_millis}")
    public String migrate() {
        try {
            AtomicLong id = new AtomicLong(0);
            AtomicLong dupId = new AtomicLong(0);
            Iterator<LiveAddress> entriesFromDb = null;
            try {id.set(esLiveAddressRepo.findTopByOrderByIdDesc().getId());} catch (Exception e) {}
            if(duplicate == 0) {
                entriesFromDb = liveAddressRepo.findByIdGreaterThan(id.get(),batchSizeRead).toIterable().iterator();
            } else {
                if(duplicate > batchSizeRead) {
                    entriesFromDb = liveAddressRepo.findFirstN(batchSizeRead).toIterable().iterator();
                    duplicate = duplicate - batchSizeRead;
                } else {
                    entriesFromDb = liveAddressRepo.findFirstN(duplicate).toIterable().iterator();
                }
            }
            long totalMigrated = 0;
            while (entriesFromDb.hasNext()) {
                List<ESLiveAddress> elasticsearch = new ArrayList<>();
                entriesFromDb.forEachRemaining(a -> {
                    id.getAndIncrement();
                    dupId.getAndIncrement();
                    ESLiveAddress liveAddress = new ESLiveAddress();
                    liveAddress.setId(id.get());
                    liveAddress.setIata(a.getIata());
                    liveAddress.setCode(a.getCode());
                    liveAddress.setCreated(a.getCreated());
                    liveAddress.setIcao(a.getIcao());
                    liveAddress.setCategory(a.getCategory());
                    liveAddress.setFulladdress(a.getFullAddress());
                    liveAddress.setIconpath(a.getIconPath());
                    liveAddress.setLatitude(a.getLatitude());
                    liveAddress.setOutcode(a.getOutcode());
                    liveAddress.setLine1(a.getLine1());
                    liveAddress.setLine2(a.getLine2());
                    liveAddress.setLongitude(a.getLongitude());
                    liveAddress.setPostcode(a.getPostcode());
                    liveAddress.setPost_code_id(a.getPostCodeId());
                    elasticsearch.add(liveAddress);
                });
                List<IndexQuery> indexQueries = elasticsearch.stream()
                        .map(entity -> new IndexQueryBuilder()
                                .withIndex(indexName)
                                .withId(String.valueOf(entity.getId()))
                                .withObject(entity)
                                .build())
                        .collect(Collectors.toList());
                for (int i = 0; i < indexQueries.size(); i += batchSizeWrite) {
                    List<IndexQuery> batch = indexQueries.subList(i, Math.min(i + batchSizeWrite, indexQueries.size()));
                    elasticsearchOperations.bulkIndex(batch, ESLiveAddress.class);
                }
                totalMigrated = totalMigrated + indexQueries.size();
                if(elasticsearch.size() < batchSizeRead) {
                    break;
                }
                if (duplicate > 0) {
                    if(duplicate > batchSizeRead) {
                        entriesFromDb = liveAddressRepo.findByIdGreaterThan(dupId.get(),batchSizeRead).toIterable().iterator();
                        duplicate = duplicate - batchSizeRead;
                    } else {
                        entriesFromDb = liveAddressRepo.findByIdGreaterThan(dupId.get(),batchSizeRead).toIterable().iterator();
                    }
                } else {
                    entriesFromDb = liveAddressRepo.findByIdGreaterThan(dupId.get(),batchSizeRead).toIterable().iterator();
                }
            }
            if(totalMigrated == 0) {
                return String.format("No new entries found. Total entries = %d",id.get());
            }
            return String.format("Successfully migrated entries = %d. Total = %d", totalMigrated, id.get());
        } catch (Exception e) {
            throw new RuntimeException("Error migrating", e);
        } finally {
            duplicate = 0;
        }
    }

//    public List<LiveAddress> findFirstNByIdGreaterThan(Long id, int limit) {
//        String sql = "SELECT * FROM " + indexName + " WHERE id > :id LIMIT :limit";
//        Query query = entityManager.createNativeQuery(sql, LiveAddress.class);
//        query.setParameter("id", id);
//        query.setParameter("limit", limit);
//        return query.getResultList();
//    }
//
//    List<LiveAddress> findFirstN(int limit) {
//        String sql = "SELECT * FROM " + indexName + " LIMIT :limit";
//        Query query = entityManager.createNativeQuery(sql, LiveAddress.class);
//        query.setParameter("limit", limit);
//        return query.getResultList();
//    }
}

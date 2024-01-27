package com.kbytes.liveaddress.service;
import com.kbytes.liveaddress.persistence.elasticsearch.models.ESLiveAddress;
import com.kbytes.liveaddress.persistence.elasticsearch.repositories.ESLiveAddressRepo;
import com.kbytes.liveaddress.persistence.postgresql.models.PGLiveAddress;
import com.kbytes.liveaddress.persistence.postgresql.repositories.PGLiveAddressRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service("MigrateService")
public class MigrateService {
    
    @Autowired
    PGLiveAddressRepo pgLiveAddressRepo;
    
    @Autowired
    ElasticsearchOperations elasticsearchOperations;
    
    @Autowired 
    ESLiveAddressRepo esLiveAddressRepo;

    @Autowired
    EntityManager entityManager;
    
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
//    @Scheduled(initialDelay = 10000, fixedRateString = "#{@migrate_cron_schedule_millis}")
    public String migrate() {
        try {
            AtomicLong id = new AtomicLong(0);
            AtomicLong dupId = new AtomicLong(0);
            List<PGLiveAddress> entriesFromDb = null;
            try {id.set(esLiveAddressRepo.findTopByOrderByIdDesc().getId());} catch (Exception e) {}
            if(duplicate == 0) {
                entriesFromDb = findFirstNByIdGreaterThan(id.get(),batchSizeRead);
            } else {
                if(duplicate > batchSizeRead) {
                    entriesFromDb = findFirstN(batchSizeRead);
                    duplicate = duplicate - batchSizeRead;
                } else {
                    entriesFromDb = findFirstN(duplicate);
                }
            }
            long totalMigrated = 0;
            while (entriesFromDb.size() > 0) {
                List<ESLiveAddress> elasticsearch = new ArrayList<>();
                entriesFromDb.stream().map(a -> {
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
                    return liveAddress;
                }).forEach(la -> elasticsearch.add(la));
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
                if(entriesFromDb.size() < batchSizeRead) {
                    break;
                }
                if (duplicate > 0) {
                    if(duplicate > batchSizeRead) {
                        entriesFromDb = findFirstNByIdGreaterThan(dupId.get(),batchSizeRead);
                        duplicate = duplicate - batchSizeRead;
                    } else {
                        entriesFromDb = findFirstNByIdGreaterThan(dupId.get(),duplicate);
                    }
                } else {
                    entriesFromDb = findFirstNByIdGreaterThan(id.get(),batchSizeRead);
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

    public List<PGLiveAddress> findFirstNByIdGreaterThan(Long id,int limit) {
        String sql = "SELECT * FROM " + indexName + " WHERE id > :id LIMIT :limit";
        Query query = entityManager.createNativeQuery(sql, PGLiveAddress.class);
        query.setParameter("id", id);
        query.setParameter("limit", limit);
        return query.getResultList();
    }

    List<PGLiveAddress> findFirstN(int limit) {
        String sql = "SELECT * FROM " + indexName + " LIMIT :limit";
        Query query = entityManager.createNativeQuery(sql, PGLiveAddress.class);
        query.setParameter("limit", limit);
        return query.getResultList();
    }
}

package com.kbytes.liveaddress.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${migrate.cron.millis}")
    int migrate_cron_millis;

    @Value("${flush.rate.limit.data.to.db.cron.millis}")
    int flush_rate_limit_data_to_db_cron_millis;

    @Value("${indexName}")
    String indexName;
    
    @Bean
    public int migrate_cron_millis(){
        return migrate_cron_millis;
    }
    @Bean
    public String indexName(){
        return indexName;
    }
    
    @Bean
    public int flush_rate_limit_data_to_db_cron_millis() {
        return flush_rate_limit_data_to_db_cron_millis;
    }
}

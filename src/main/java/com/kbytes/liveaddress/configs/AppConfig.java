package com.kbytes.liveaddress.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${migrate.schedule.millis}")
    int migrate_cron_schedule_millis;

    @Value("${indexName}")
    String indexName;
    
    @Bean
    public int migrate_cron_schedule_millis(){
        return migrate_cron_schedule_millis;
    }
    @Bean
    public String indexName(){
        return indexName;
    }
}

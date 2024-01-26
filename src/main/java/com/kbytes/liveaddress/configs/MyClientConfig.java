package com.kbytes.liveaddress.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class MyClientConfig extends ElasticsearchConfiguration {

    @Value("${spring.data.elasticsearch.cluster-nodes}")
    private String clusterNodes;
    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(clusterNodes)
                .build();
    }
}

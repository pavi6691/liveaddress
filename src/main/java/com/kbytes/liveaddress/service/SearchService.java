package com.kbytes.liveaddress.service;

import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.kbytes.liveaddress.persistence.elasticsearch.models.ESLiveAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("SearchService")
public class SearchService {
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    public List<ESLiveAddress> searchAsYouType(String userInput) {
        if (userInput.length() < 3) {
            return List.of(); // Return an empty list for input less than 3 characters
        }
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(new MultiMatchQuery.Builder()
                        .query(userInput)
                        .fields("postcode","fulladdress")
                        .type(TextQueryType.BestFields).build()._toQuery())
                .build();

        return elasticsearchOperations.search(searchQuery, ESLiveAddress.class).stream()
                .map(sh -> sh.getContent()).collect(Collectors.toList());
    }
}

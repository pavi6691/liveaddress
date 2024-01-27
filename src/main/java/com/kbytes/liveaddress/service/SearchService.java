package com.kbytes.liveaddress.service;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.kbytes.liveaddress.persistence.elasticsearch.models.ESLiveAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service("SearchService")
public class SearchService extends Object {
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    public List<ESLiveAddress> searchAsYouType(String userInput) {
        if (userInput.length() < 3) {
            return List.of(); // Return an empty list for input less than 3 characters
        }
        Query query = null;
        if(isPostcode(userInput)) {
            query = new MatchPhrasePrefixQuery.Builder()
                    .query(userInput)
                    .field("postcode").build()._toQuery();
        } else {
            query = new MatchPhraseQuery.Builder()
                    .query(userInput)
                    .field("fulladdress").build()._toQuery();
        }
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(query)
                .build();

        return elasticsearchOperations.search(searchQuery, ESLiveAddress.class).stream()
                .map(sh -> sh.getContent()).collect(Collectors.toList());
    }

    /**
     * logic to determine if the query is in postcode format
     * @param query
     * @return true if matches elase false
     */
    private boolean isPostcode(String query) {
        String regex = "\\b[A-Z]{1,2}\\d{1,2}(?:\\s\\d[A-Z]{1,2})?\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(query);
        return matcher.find() && query.length() <= 8;
    }
}
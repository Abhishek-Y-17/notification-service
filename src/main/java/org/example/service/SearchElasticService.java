package org.example.service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.example.entity.SmsReqElastic;
import org.example.repository.elastic.SmsElasticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchElasticService {
    @Autowired
    private SmsElasticRepository smsElasticRepository;

    @Autowired
    private RestHighLevelClient client;

    public Page<SmsReqElastic> getAllSmsRequestFromElastic(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return smsElasticRepository.findAll(pageable);
    }

    public Page<SmsReqElastic> searchByText(String text,int page,int size) {
        Pageable pageable = PageRequest.of(page, size);
        return smsElasticRepository.findAllByMessage(text,pageable);
    }

    public List<Map<String, Object>> searchMessagesByPhoneAndDate(String phoneNo, String fromDateTime, String toDateTime,int page,int size) throws IOException {

        int from  = page*size;

        SearchRequest searchRequest = new SearchRequest("smsrequest");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("phoneNo", phoneNo))
                .filter(QueryBuilders.rangeQuery("created_at")
                        .gte(fromDateTime)
                        .lte(toDateTime)
                )
        );
        sourceBuilder.from(from);
        sourceBuilder.size(size);

        System.out.println("Generated Query DSL: " + sourceBuilder.toString());

        // Set the source builder on the search request
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        List<Map<String, Object>> results = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            results.add(hit.getSourceAsMap());
        }
        return results;
    }
}

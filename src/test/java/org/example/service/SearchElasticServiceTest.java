package org.example.service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.example.entity.SmsReqElastic;
import org.example.repository.elastic.SmsElasticRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchElasticServiceTest {

    @Mock
    private SmsElasticRepository smsElasticRepository;

    @Mock
    private RestHighLevelClient client;

    @InjectMocks
    private SearchElasticService searchElasticService;

    @Test
    void testGetAllSmsRequestFromElastic() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        // Create dummy SmsReqElastic objects; adjust fields as needed.
        SmsReqElastic sms1 = new SmsReqElastic();
        SmsReqElastic sms2 = new SmsReqElastic();
        List<SmsReqElastic> smsList = Arrays.asList(sms1, sms2);
        Page<SmsReqElastic> pageResult = new PageImpl<>(smsList, pageable, smsList.size());

        when(smsElasticRepository.findAll(pageable)).thenReturn(pageResult);

        Page<SmsReqElastic> result = searchElasticService.getAllSmsRequestFromElastic(page, size);
        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertEquals(smsList, result.getContent());
    }

    @Test
    void testSearchByText() {
        int page = 0;
        int size = 10;
        String text = "sample text";
        Pageable pageable = PageRequest.of(page, size);

        SmsReqElastic sms = new SmsReqElastic();
        List<SmsReqElastic> smsList = Collections.singletonList(sms);
        Page<SmsReqElastic> pageResult = new PageImpl<>(smsList, pageable, smsList.size());

        when(smsElasticRepository.findAllByMessage(text, pageable)).thenReturn(pageResult);

        Page<SmsReqElastic> result = searchElasticService.searchByText(text, page, size);
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(smsList, result.getContent());
    }

//    @Test
//    void testSearchMessagesByPhoneAndDate() throws IOException {
//        String phoneNo = "1234567890";
//        String fromDateTime = "2025-01-01T00:00:00";
//        String toDateTime = "2025-01-02T00:00:00";
//        int page = 0;
//        int size = 10;
//
//        // Mock the SearchHit (a final class) - this works if you have added the mockito-inline dependency.
//        SearchHit hit = mock(SearchHit.class);
//        Map<String, Object> sourceMap = new HashMap<>();
//        sourceMap.put("phoneNo", phoneNo);
//        sourceMap.put("message", "Test message");
//        when(hit.getSourceAsMap()).thenReturn(sourceMap);
//
//        // Mock SearchHits and its behavior.
//        SearchHits searchHits = mock(SearchHits.class);
//        SearchHit[] hitArray = new SearchHit[]{hit};
//        when(searchHits.getHits()).thenReturn(hitArray);
//        when(searchHits.iterator()).thenReturn(Arrays.asList(hitArray).iterator());
//
//        // Mock SearchResponse to return our mocked SearchHits.
//        SearchResponse searchResponse = mock(SearchResponse.class);
//        when(searchResponse.getHits()).thenReturn(searchHits);
//
//        // Stub the client's search method.
//        when(client.search(any(SearchRequest.class), eq(RequestOptions.DEFAULT))).thenReturn(searchResponse);
//
//        // Execute the service method.
//        List<Map<String, Object>> results = searchElasticService.searchMessagesByPhoneAndDate(
//                phoneNo, fromDateTime, toDateTime, page, size);
//
//        // Verify the results.
//        Assertions.assertNotNull(results);
//        Assertions.assertEquals(1, results.size());
//        Assertions.assertEquals(phoneNo, results.get(0).get("phoneNo"));
//        Assertions.assertEquals("Test message", results.get(0).get("message"));
//
//        verify(client, times(1)).search(any(SearchRequest.class), eq(RequestOptions.DEFAULT));
//    }
}

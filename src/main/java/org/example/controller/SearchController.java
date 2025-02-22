package org.example.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Exceptions.InvalidPhoneNoException;
import org.example.constants.AppConstants;
import org.example.entity.SmsRequests;
import org.example.repository.jpa.SmsRequestRepository;
import org.example.request.DateRangeRequest;
import org.example.entity.SmsReqElastic;
import org.example.service.SearchElasticService;
import org.example.utils.HelperBlacklist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/v1/search")
public class SearchController {

    private static final Logger log = LogManager.getLogger(SearchController.class);
    @Autowired
    private SearchElasticService searchElasticService;
    @Autowired
    HelperBlacklist helperBlacklist;



    @GetMapping("/getAllFromElastic")
    public ResponseEntity<?> smsRequestsElastic(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE) int page ,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SIZE) int size) {
        Page<SmsReqElastic> smsReqElastics =  searchElasticService.getAllSmsRequestFromElastic(page,size);
        return ResponseEntity.ok(smsReqElastics);
    }

    @GetMapping("")
    public ResponseEntity<?> search(
            @RequestParam() String text,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE) int page ,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SIZE) int size) {
           Page<SmsReqElastic>  smsReqElastics = searchElasticService.searchByText(text,page,size);
           return ResponseEntity.ok(smsReqElastics);
    }

    @PostMapping("/dateAndPhoneNo")
    public ResponseEntity<?>  searchByDate(
            @RequestBody DateRangeRequest dateRange,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE) int page ,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SIZE) int size) throws IOException {

        if (dateRange.getFromDateTime() == null || dateRange.getFromDateTime().isEmpty()) {
            dateRange.setFromDateTime("2025-02-10T13:03:13.151Z");
        }
        if (dateRange.getToDateTime() == null || dateRange.getToDateTime().isEmpty()) {
            LocalDateTime date = LocalDateTime.now();
            String dated = date.toInstant(ZoneOffset.UTC).toString();
            dateRange.setToDateTime(dated);
        }
            List<Map<String, Object>> messages =  searchElasticService.searchMessagesByPhoneAndDate(dateRange.getPhoneNo(), dateRange.getFromDateTime(), dateRange.getToDateTime(),page,size);
            return  ResponseEntity.ok(messages);
    }

}

package org.example.controller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Exceptions.IdNotFoundException;
import org.example.Exceptions.InvalidPhoneNoException;
import org.example.Exceptions.MessageNotFoundException;
import org.example.Exceptions.PhoneNoNotFoundException;
import org.example.entity.SmsReqElastic;
import org.example.entity.SmsRequests;
import org.example.request.CreateSmsRequest;
import org.example.response.SmsRequestPendingResponse;
import org.example.response.SmsRequestResponse;
import org.example.service.BlacklistRedisService;
import org.example.service.SmsRequestService;
import org.example.utils.Helper;
import org.example.utils.HelperBlacklist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/sms")
public class SmsRequestController {

    private static final Logger log = LogManager.getLogger(SmsRequestController.class);
    @Autowired
    SmsRequestService smsRequestService;
    @Autowired
    Helper helper;

    @Autowired
    private BlacklistRedisService blacklistRedisService;

    @GetMapping("/id/{id}")
    public ResponseEntity<?> sms_request(@PathVariable Integer id) {
            SmsRequests smsRequests =  smsRequestService.getSmsRequestById(id)
                    .orElseThrow( () -> new IdNotFoundException("Invalid request Id " + id ));
            return new ResponseEntity<>(smsRequests, HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> smsRequests(){
        List<SmsRequests> ListOfSms =  smsRequestService.getAllSmsRequest();
        List<SmsRequestResponse> ListOfSmsResponse = new ArrayList<>();
        ListOfSms.forEach(smsRequest -> {
           ListOfSmsResponse.add( new SmsRequestResponse(smsRequest));
        });
        if(ListOfSmsResponse.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok( ListOfSmsResponse);
    }



    @PostMapping("/send")
    public ResponseEntity<?> smsRequestsPost(@Valid @RequestBody CreateSmsRequest createSmsRequest) {
           if(!helper.isPhoneNoValid(createSmsRequest.getPhoneNo())){
               throw new InvalidPhoneNoException("Invalid phone no");
           }
          SmsRequests smsRequests =  smsRequestService.createSmsRequest(createSmsRequest);
          SmsRequestPendingResponse smsRequestPendingResponse = new SmsRequestPendingResponse(smsRequests);
          return ResponseEntity.status(HttpStatus.CREATED).body(smsRequestPendingResponse);
    }



}
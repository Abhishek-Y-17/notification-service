package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.Enums.Status;
import org.example.entity.SmsReqElastic;
import org.example.repository.elastic.SmsElasticRepository;
import org.example.repository.jpa.SmsRequestRepository;
import org.example.request.CreateSmsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.entity.SmsRequests;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SmsRequestService {

    private final SmsRequestRepository smsRequestRepository;

    private final KafkaProducerService kafkaProducerService;



    public List<SmsRequests> getAllSmsRequest() {
        return smsRequestRepository.findAll();
    }



    public Optional<SmsRequests> getSmsRequestById(Integer id) {
        return smsRequestRepository.findById(id);
    }

    public SmsRequests createSmsRequest(CreateSmsRequest sms) {
      SmsRequests sms_req  = new SmsRequests(sms);
      sms_req.setStatus(Status.PENDING);
      SmsRequests msg =  smsRequestRepository.save(sms_req);
      String msgId = msg.getId().toString();
      kafkaProducerService.sendMessage(msgId);
      return msg;
    }

}
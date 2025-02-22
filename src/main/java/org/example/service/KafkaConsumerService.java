package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.Enums.Status;
import org.example.Exceptions.SmsApiException;
import org.example.constants.AppConstants;
import org.example.entity.SmsReqElastic;
import org.example.entity.SmsRequests;
import org.example.repository.elastic.SmsElasticRepository;
import org.example.repository.jpa.SmsRequestRepository;
import org.example.response.SmsResponse;
import org.example.utils.HelperBlacklist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class KafkaConsumerService {

    @Autowired
    private HelperBlacklist helperBlacklist;

    @Autowired
    private SmsRequestRepository smsRequestRepository;

    @Autowired
    private SmsElasticRepository smsElasticRepository;

    @Autowired
    private SmsClientService smsClientService;

    @KafkaListener(topics = AppConstants.KAFKA_TOPIC, groupId = AppConstants.KAFKA_GROUP_ID)
    @Retryable(
            value = { SmsApiException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = AppConstants.RETRY_DELAY, multiplier = AppConstants.RETRY_DELAY_MULTIPLIER)
    )
    public void consume(String message) {
        int id = Integer.parseInt(message);
        System.out.println(id);
        Optional<SmsRequests> sms = smsRequestRepository.findById(id);
        if (sms.isPresent()) {
            // checking  blacklisted
            if(helperBlacklist.isBlacklisted(sms.get().getPhoneNo())) {
                log.info("Phone number {} is blacklisted. Skipping SMS sending.", sms.get().getPhoneNo());
                sms.get().setStatus(Status.FAILURE);
                sms.get().setFailure_comments("blacklisted phone number");
            }
            else{
                try {
                    log.info("Calling third party API for SMS sending");
                    String correlationId = UUID.randomUUID().toString();
                    SmsResponse smsResponse = smsClientService.sendSms(sms.get().getPhoneNo(), sms.get().getMessage(), correlationId);
                    log.info("Received response: {}", smsResponse);
                    // Assuming a successful response indicates success
                    sms.get().setStatus(Status.SUCCESS);
                    sms.get().setFailure_code("SUCCESS");
                    sms.get().setFailure_comments("SMS sent successfully");
                } catch (SmsApiException ex) {
                    log.error("Error sending SMS for id {}: {}", id, ex.getMessage());
                    sms.get().setStatus(Status.FAILURE);
                    sms.get().setFailure_code(ex.getErrorCode());
                    sms.get().setFailure_comments(ex.getMessage());
                    throw ex;
                }
            }
            smsRequestRepository.save(sms.get());

            SmsReqElastic smsReqElastic = new SmsReqElastic();
            smsReqElastic.setId(sms.get().getId());
            smsReqElastic.setPhoneNo(sms.get().getPhoneNo());
            smsReqElastic.setMessage(sms.get().getMessage());
            smsReqElastic.setStatus(sms.get().getStatus());
            smsReqElastic.setFailure_code(sms.get().getFailure_code());
            smsReqElastic.setFailure_comments(sms.get().getFailure_comments());
            smsReqElastic.setCreated_at(sms.get().getCreated_at().toInstant(ZoneOffset.UTC));
            smsReqElastic.setUpdated_at(sms.get().getUpdated_at().toInstant(ZoneOffset.UTC));
            smsElasticRepository.save(smsReqElastic);
        }
        else{
            log.warn("id not found : {}", id);
        }

    }
}
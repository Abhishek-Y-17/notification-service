package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.Exceptions.SmsApiException;
import org.example.response.SmsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
public class SmsClientService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${thirdparty.sms.url}")
    private String smsApiUrl;

    @Value("${thirdparty.sms.key}")
    private String apiKey;

    public SmsResponse sendSms(String phoneNumber, String message, String correlationId) {
        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Key", apiKey);

        // Prepare the request body (adjust structure as required)
        Map<String, Object> payload = new HashMap<>();
        payload.put("deliverychannel", "sms");
        Map<String, Object> channels = new HashMap<>();
        Map<String, Object> sms = new HashMap<>();
        sms.put("text", message);
        channels.put("sms", sms);
        payload.put("channels", channels);

        Map<String, Object> destinationEntry = new HashMap<>();
        destinationEntry.put("msisdn", Arrays.asList(phoneNumber));
        destinationEntry.put("correlationId", correlationId);
        payload.put("destination", Arrays.asList(destinationEntry));

        // The API expects an array
        List<Map<String, Object>> requestBody = Arrays.asList(payload);
        System.out.println(requestBody);
        log.info("request body for third party api : {}",requestBody);
        HttpEntity<List<Map<String, Object>>> requestEntity = new HttpEntity<>(requestBody, headers);


        try {
            ResponseEntity<SmsResponse> responseEntity = restTemplate.exchange(
                    smsApiUrl, HttpMethod.POST, requestEntity, SmsResponse.class);
            return responseEntity.getBody();
        } catch (HttpClientErrorException ex) {
            log.error("Client error while calling SMS API: {}", ex.getResponseBodyAsString());
            throw new SmsApiException("CLIENT_ERROR", "Error from SMS API: " + ex.getResponseBodyAsString(), ex);
        } catch (HttpServerErrorException ex) {
            log.error("Server error while calling SMS API: {}", ex.getResponseBodyAsString());
            throw new SmsApiException("SERVER_ERROR", "Error from SMS API: " + ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            log.error("Unexpected error while calling SMS API: {}", ex.getMessage());
            throw new SmsApiException("UNKNOWN_ERROR", "Unexpected error calling SMS API", ex);
        }
    }
}

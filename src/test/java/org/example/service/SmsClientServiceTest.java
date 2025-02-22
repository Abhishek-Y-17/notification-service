package org.example.service;

import org.example.Exceptions.SmsApiException;
import org.example.response.SmsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SmsClientServiceTest {

    @InjectMocks
    private SmsClientService smsClientService;

    @Mock
    private RestTemplate restTemplate;

    private final String dummyUrl = "http://example.com/sms";
    private final String dummyApiKey = "dummyApiKey";

    @BeforeEach
    public void setUp() {
        // Set the @Value fields for smsApiUrl and apiKey using ReflectionTestUtils.
        ReflectionTestUtils.setField(smsClientService, "smsApiUrl", dummyUrl);
        ReflectionTestUtils.setField(smsClientService, "apiKey", dummyApiKey);
    }

    @Test
    public void testSendSmsSuccess() {
        String phoneNumber = "1234567890";
        String message = "Test message";
        String correlationId = "corrId123";

        // Prepare a dummy SmsResponse (customize as needed).
        SmsResponse dummyResponse = new SmsResponse();
        // Optionally set properties on dummyResponse here.

        ResponseEntity<SmsResponse> responseEntity = new ResponseEntity<>(dummyResponse, HttpStatus.OK);

        // Stub the RestTemplate.exchange() call.
        when(restTemplate.exchange(eq(dummyUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(SmsResponse.class)))
                .thenReturn(responseEntity);

        SmsResponse response = smsClientService.sendSms(phoneNumber, message, correlationId);

        assertNotNull(response);
        assertEquals(dummyResponse, response);
    }

    @Test
    public void testSendSmsClientError() {
        String phoneNumber = "1234567890";
        String message = "Test message";
        String correlationId = "corrId123";

        String errorBody = "{\"error\": \"Bad Request\"}";
        HttpClientErrorException clientError = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST, "Bad Request", HttpHeaders.EMPTY, errorBody.getBytes(), null);

        // When the exchange call is made, simulate a client error.
        when(restTemplate.exchange(eq(dummyUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(SmsResponse.class)))
                .thenThrow(clientError);

        SmsApiException exception = assertThrows(SmsApiException.class, () ->
                smsClientService.sendSms(phoneNumber, message, correlationId)
        );

        assertTrue(exception.getMessage().contains("Error from SMS API: " + errorBody));
        // If SmsApiException provides a getter for error code, you could also assert:
        // assertEquals("CLIENT_ERROR", exception.getErrorCode());
    }

    @Test
    public void testSendSmsServerError() {
        String phoneNumber = "1234567890";
        String message = "Test message";
        String correlationId = "corrId123";

        String errorBody = "{\"error\": \"Internal Server Error\"}";
        HttpServerErrorException serverError = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", HttpHeaders.EMPTY, errorBody.getBytes(), null);

        // Simulate a server error when the exchange call is made.
        when(restTemplate.exchange(eq(dummyUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(SmsResponse.class)))
                .thenThrow(serverError);

        SmsApiException exception = assertThrows(SmsApiException.class, () ->
                smsClientService.sendSms(phoneNumber, message, correlationId)
        );

        assertTrue(exception.getMessage().contains("Error from SMS API: " + errorBody));
        // If available:
        // assertEquals("SERVER_ERROR", exception.getErrorCode());
    }

    @Test
    public void testSendSmsUnknownError() {
        String phoneNumber = "1234567890";
        String message = "Test message";
        String correlationId = "corrId123";

        Exception genericException = new RuntimeException("Unexpected error");

        // Simulate a generic (unexpected) error when the exchange call is made.
        when(restTemplate.exchange(eq(dummyUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(SmsResponse.class)))
                .thenThrow(genericException);

        SmsApiException exception = assertThrows(SmsApiException.class, () ->
                smsClientService.sendSms(phoneNumber, message, correlationId)
        );

        assertTrue(exception.getMessage().contains("Unexpected error calling SMS API"));

    }
}

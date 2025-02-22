package org.example.service;

import org.example.entity.SmsRequests;
import org.example.repository.jpa.SmsRequestRepository;
import org.example.repository.elastic.SmsElasticRepository;
import org.example.request.CreateSmsRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SmsRequestServiceTest {

    @Mock
    private SmsRequestRepository smsRequestRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private SmsElasticRepository smsElasticRepository;

    @InjectMocks
    private SmsRequestService smsRequestService;

    @Test
    void createSmsRequestTest() {

        CreateSmsRequest createSmsRequest = new CreateSmsRequest("8957110385", "barca");

        // data preparation
        SmsRequests savedSmsRequest = new SmsRequests(createSmsRequest);
        savedSmsRequest.setStatus("pending");
        savedSmsRequest.setId(1);
        // mocking calls
        // When repository.save is called with any SmsRequests, return savedSmsRequest.
        Mockito.when(smsRequestRepository.save(Mockito.any(SmsRequests.class)))
                .thenReturn(savedSmsRequest);

        // calling methods
        SmsRequests returnedSmsRequest = smsRequestService.createSmsRequest(createSmsRequest);

        // Assert that repository.save was called correctly.
        ArgumentCaptor<SmsRequests> smsCaptor = ArgumentCaptor.forClass(SmsRequests.class);
        Mockito.verify(smsRequestRepository).save(smsCaptor.capture());
        SmsRequests capturedSms = smsCaptor.getValue();
        assertEquals("pending", capturedSms.getStatus(), "Status should be 'pending'");

        // Verify that kafkaProducerService.sendMessage was called with the correct message id.
        Mockito.verify(kafkaProducerService).sendMessage("1");

        // Assert the returned SmsRequests has expected values.
        assertNotNull(returnedSmsRequest, "Returned SMS Request should not be null");
        assertEquals(1, returnedSmsRequest.getId(), "Returned SMS Request should have id 1");
        assertEquals("pending", returnedSmsRequest.getStatus(), "Returned SMS Request should have status 'pending'");
    }

    @Test
    void getAllSmsRequestsTest() {
        SmsRequests sms1 = new SmsRequests(new CreateSmsRequest("8957110385", "barca"));
        sms1.setStatus("pending");
        sms1.setId(1);
        SmsRequests sms2 = new SmsRequests(new CreateSmsRequest("8888888888", "real"));
        sms2.setStatus("pending");
        sms2.setId(2);

        Mockito.when(smsRequestRepository.findAll()).thenReturn(Arrays.asList(sms1, sms2));

        List<SmsRequests> listOfRequests = smsRequestService.getAllSmsRequest();

        assertEquals(2, listOfRequests.size(), "List of requests should be 2");
        assertEquals(sms1, listOfRequests.get(0));
        assertEquals(sms2, listOfRequests.get(1));
    }

    @Test
    void getSmsRequestByIdTest() {
        SmsRequests sms1 = new SmsRequests(new CreateSmsRequest("8957110385", "barca"));
        sms1.setId(1);
        Mockito.when(smsRequestRepository.findById(1)).thenReturn(Optional.of(sms1));

        Optional<SmsRequests> result =  smsRequestService.getSmsRequestById(1);
        assertEquals(result, Optional.of(sms1));
    }


}

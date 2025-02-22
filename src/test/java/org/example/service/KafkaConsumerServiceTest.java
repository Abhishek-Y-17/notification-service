package org.example.service;

import org.example.entity.SmsReqElastic;
import org.example.entity.SmsRequests;
import org.example.repository.elastic.SmsElasticRepository;
import org.example.repository.jpa.SmsRequestRepository;
import org.example.response.SmsResponse;
import org.example.utils.HelperBlacklist;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @Mock
    private BlacklistRedisService blacklistRedisService;

    @Mock
    private BlacklistService blacklistService;

    @Mock
    private HelperBlacklist helperBlacklist;

    @Mock
    private SmsRequestRepository smsRequestRepository;

    @Mock
    private SmsElasticRepository smsElasticRepository;

    @Mock
    private SmsClientService smsClientService;

    @Mock
    private SmsRequestService smsRequestService; // Although not directly used in consume()

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    private SmsRequests createSmsRequestEntity() {
        SmsRequests sms = new SmsRequests();
        sms.setId(1);
        sms.setPhoneNo("8957110385");
        sms.setMessage("Test Message");
        // Assuming these fields are LocalDateTime or similar.
        LocalDateTime now = LocalDateTime.now();
        sms.setCreated_at(now);
        sms.setUpdated_at(now);
        return sms;
    }

    @Test
    void consume_nonBlacklisted() {
        // Arrange
        int id = 1;
        String message = String.valueOf(id);
        SmsRequests smsEntity = createSmsRequestEntity();

        // When repository is queried, return the smsEntity.
        when(smsRequestRepository.findById(id)).thenReturn(Optional.of(smsEntity));
        // Simulate that the phone is NOT blacklisted.
        when(helperBlacklist.isBlacklisted(smsEntity.getPhoneNo())).thenReturn(false);

        // Prepare a fake SMS response from the third-party service.
        SmsResponse fakeResponse = new SmsResponse();
        // (Set properties on fakeResponse if needed)
        when(smsClientService.sendSms(eq(smsEntity.getPhoneNo()), eq(smsEntity.getMessage()), any(String.class)))
                .thenReturn(fakeResponse);

        // Act
        kafkaConsumerService.consume(message);

        // Assert: Verify that the smsEntity was updated to "success"
        assertEquals("SUCCESS", smsEntity.getStatus(), "Status should be 'success'");
        assertEquals("SUCCESS", smsEntity.getFailure_code(), "NO FAILURE CODE should be 'SUCCESS'");
        assertEquals("SMS sent successfully", smsEntity.getFailure_comments(), "Failure comments should be 'success'");

        // Verify that the repository save was called with our updated smsEntity.
        verify(smsRequestRepository).save(smsEntity);

        // Capture the SmsReqElastic object that was saved to the elastic repository.
        ArgumentCaptor<SmsReqElastic> elasticCaptor = ArgumentCaptor.forClass(SmsReqElastic.class);
        verify(smsElasticRepository).save(elasticCaptor.capture());
        SmsReqElastic capturedElastic = elasticCaptor.getValue();

        // Verify that the elastic object has values derived from the smsEntity.
        assertEquals(smsEntity.getId(), capturedElastic.getId());
        assertEquals(smsEntity.getPhoneNo(), capturedElastic.getPhoneNo());
        assertEquals(smsEntity.getMessage(), capturedElastic.getMessage());
        assertEquals(smsEntity.getStatus(), capturedElastic.getStatus());
        assertEquals(smsEntity.getFailure_code(), capturedElastic.getFailure_code());
        assertEquals(smsEntity.getFailure_comments(), capturedElastic.getFailure_comments());
        // Also verify that created_at and updated_at are correctly converted.
        assertEquals(smsEntity.getCreated_at().toInstant(ZoneOffset.UTC), capturedElastic.getCreated_at());
        assertEquals(smsEntity.getUpdated_at().toInstant(ZoneOffset.UTC), capturedElastic.getUpdated_at());

        // Optionally verify that the third-party SMS service was called.
        verify(smsClientService).sendSms(eq(smsEntity.getPhoneNo()), eq(smsEntity.getMessage()), any(String.class));
    }

    @Test
    void consume_blacklisted() {
        // Arrange
        int id = 2;
        String message = String.valueOf(id);
        SmsRequests smsEntity = createSmsRequestEntity();
        smsEntity.setId(id);
        // Set a different phone number if desired.
        smsEntity.setPhoneNo("1234567890");

        // When repository is queried, return the smsEntity.
        when(smsRequestRepository.findById(id)).thenReturn(Optional.of(smsEntity));
        // Simulate that the phone IS blacklisted.
        when(helperBlacklist.isBlacklisted(smsEntity.getPhoneNo())).thenReturn(true);

        // Act
        kafkaConsumerService.consume(message);

        // Assert: In the blacklisted branch, the status is set to "400" and failure_comments is "blacklisted phone number"
        assertEquals("BLACKLISTED", smsEntity.getStatus(), "Status should be set to '400' for blacklisted phone numbers");
        assertEquals("blacklisted phone number", smsEntity.getFailure_comments(), "Failure comments should indicate blacklisting");

        // Verify that the smsRequestRepository.save was called.
        verify(smsRequestRepository).save(smsEntity);

        // Capture the SmsReqElastic object that was saved.
        ArgumentCaptor<SmsReqElastic> elasticCaptor = ArgumentCaptor.forClass(SmsReqElastic.class);
        verify(smsElasticRepository).save(elasticCaptor.capture());
        SmsReqElastic capturedElastic = elasticCaptor.getValue();

        // Verify that the elastic object has the blacklisted status.
        assertEquals(smsEntity.getId(), capturedElastic.getId());
        assertEquals(smsEntity.getStatus(), capturedElastic.getStatus());
        assertEquals(smsEntity.getFailure_comments(), capturedElastic.getFailure_comments());

        // Verify that the third-party SMS service was NOT called.
        verify(smsClientService, never()).sendSms(anyString(), anyString(), anyString());
    }

    @Test
    void consume_smsNotFound() {
        // Arrange
        int id = 999;
        String message = String.valueOf(id);
        // Simulate repository not finding the smsEntity.
        when(smsRequestRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        kafkaConsumerService.consume(message);

        // Assert: Since the sms request is not found, no save should occur.
        verify(smsRequestRepository, never()).save(any());
        verify(smsElasticRepository, never()).save(any());
        // Optionally, you could verify that no interactions with smsClientService occur.
        verify(smsClientService, never()).sendSms(anyString(), anyString(), anyString());
    }
}

package org.example.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private Logger log;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    private static final String TOPIC = "my_topic";

    @Test
    void testSendMessage() {
        String message = "Test Message";
        kafkaProducerService.sendMessage(message);

        verify(kafkaTemplate, times(1)).send(TOPIC, message);
    }
}

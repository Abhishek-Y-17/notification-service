package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.constants.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KafkaProducerService {

    private static final String TOPIC = AppConstants.KAFKA_TOPIC;
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message) {
        kafkaTemplate.send(TOPIC, message);
        log.info("Message sent to topic {}", message);
    }
}

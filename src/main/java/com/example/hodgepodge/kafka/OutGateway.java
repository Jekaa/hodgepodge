package com.example.hodgepodge.kafka;

import com.example.hodgepodge.kafka.model.KafkaMessage;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;

@MessagingGateway
public interface OutGateway {

    String CHANNEL = "OutChannel";

    @Gateway(requestChannel = CHANNEL)
    void sendResponse(@Headers MessageHeaders headers, KafkaMessage message);
}

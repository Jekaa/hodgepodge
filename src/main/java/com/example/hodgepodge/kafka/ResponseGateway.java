package com.example.hodgepodge.kafka;

import com.example.hodgepodge.kafka.model.KafkaMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResponseGateway {

    private final OutGateway outGateway;

    void send(MessageHeaders headers, KafkaMessage message) {
        KafkaMessage out = KafkaMessage.builder()
                .id(message.getId())
                .build();
        outGateway.sendResponse(headers, out);
    }
}


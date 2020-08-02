package com.example.hodgepodge.kafka;

import com.example.hodgepodge.kafka.model.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class MessageHandler {

    private final ResponseGateway out;

    MessageHandler(ResponseGateway out) {
        this.out = out;
    }

    void handle(MessageHeaders headers, KafkaMessage message) {
        out.send(headers, message);
    }

}


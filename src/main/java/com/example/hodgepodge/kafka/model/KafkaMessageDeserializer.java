package com.example.hodgepodge.kafka.model;

public class KafkaMessageDeserializer extends LaxMessageDeserializer<KafkaMessage> {
    public KafkaMessageDeserializer() {
        super(KafkaMessage.class);
    }
}

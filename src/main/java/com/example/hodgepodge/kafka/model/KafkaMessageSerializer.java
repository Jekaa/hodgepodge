package com.example.hodgepodge.kafka.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vavr.jackson.datatype.VavrModule;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageSerializer extends JsonSerializer<KafkaMessage>  {

    public KafkaMessageSerializer() {
        setAddTypeInfo(false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new VavrModule());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public KafkaMessageSerializer(ObjectMapper objectMapper) {
        super(objectMapper);
        setAddTypeInfo(false);
    }
}

package com.example.hodgepodge.kafka.model;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Slf4j
public abstract class LaxMessageDeserializer<T> extends JsonDeserializer<T> {
    public LaxMessageDeserializer(Class<T> cls) {
        super(cls);
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        return Try.of(() -> super.deserialize(topic, headers, data))
                .onFailure(ex -> log.error("Error deserializing message", ex))
                .getOrElse((T) null);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        return Try.of(() -> super.deserialize(topic, data))
                .onFailure(ex -> log.error("Error deserializing message", ex))
                .getOrElse((T) null);
    }
}

package com.example.hodgepodge.kafka.model;

import io.vavr.collection.Map;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Builder
public class KafkaMessage {
    @PositiveOrZero
    @Getter
    private final long id;
    @NotNull
    @Getter
    private final Map<String, Object> payload;
}

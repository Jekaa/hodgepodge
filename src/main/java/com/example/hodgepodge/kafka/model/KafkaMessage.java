package com.example.hodgepodge.kafka.model;

import com.example.hodgepodge.Utils;
import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "payload"})
public class KafkaMessage {

    @JsonProperty("id")
    @PositiveOrZero
    @Getter
    private final long id;
    @JsonProperty("payload")
    @NotNull
    @Getter
    private final Map<String, Object> payload;

    @JsonCreator
    public KafkaMessage(@JsonProperty("id") long id, @JsonProperty("payload") Map<String, Object> payload) {
        this.id = id;
        this.payload = payload;
    }

    public static KafkaMessageBuilder builder() {
        return new KafkaMessageBuilder();
    }

    @Override
    public String toString() {
        return Utils.toJson(this);
    }

    @NoArgsConstructor
    public static class KafkaMessageBuilder {
        private long id;
        private Map<String, Object> payload;

        public KafkaMessageBuilder id(long id) {
            this.id = id;
            return this;
        }

        public KafkaMessageBuilder payload(Map<String, Object> payload) {
            this.payload = payload;
            return this;
        }

        public KafkaMessage build() {
            if (this.payload == null) {
                this.payload = new HashMap<>();
            }
            return new KafkaMessage(this.id, this.payload);
        }
    }
}

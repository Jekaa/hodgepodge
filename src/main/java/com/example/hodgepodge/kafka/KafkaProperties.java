package com.example.hodgepodge.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "kafka")
@Validated
public class KafkaProperties {

    @Setter
    @Getter
    @NotBlank
    private String bootstrapServer;
    @Setter
    @Getter
    @NotBlank
    private String groupId;
    @Setter
    @Getter
    @Min(10)
    @Max(1000)
    private int kafkaMaxPollRecords;
    @Setter
    @Getter
    private boolean kafkaAutocommitEnabled;
    @Setter
    @Getter
    @Min(300)
    @Max(5000)
    private int kafkaAutocommitIntervalMs;
    @Setter
    @Getter
    @Min(5)
    @Max(60)
    private int kafkaConsumerSessionTimeoutSec;
    @Setter
    @Getter
    @Min(1)
    @Max(20)
    private int kafkaListenerConcurrency;
    @Setter
    @Getter
    @Min(0)
    @Max(200)
    private int kafkaProducerLingerMs;
    @Setter
    @Getter
    @Min(1000)
    @Max(60000)
    private int kafkaProducerMaxBlockMs;
    @Setter
    @Getter
    @NotBlank
    private String inTopic;
    @Setter
    @Getter
    @NotBlank
    private String outTopic;
}

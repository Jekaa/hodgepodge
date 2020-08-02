package com.example.hodgepodge.kafka;

import com.example.hodgepodge.kafka.model.KafkaMessage;
import com.example.hodgepodge.kafka.model.KafkaMessageDeserializer;
import com.example.hodgepodge.kafka.model.KafkaMessageSerializer;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.dsl.KafkaProducerMessageHandlerSpec;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

import java.util.Map;

@EnableKafka
@Configuration
@Slf4j
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConfig {

    private static final String CONSUMER_FACTORY = "consumerFactory";
    private static final String RESPONSE_KAFKA_HANDLER = "responseKafkaHandler";

    private final KafkaProperties kafkaProperties;

    @Autowired
    public KafkaConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public Map<String, Object> defaultConsumerProps() {
        return ImmutableMap.<String, Object>builder()
                .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServer())
                .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaMessageDeserializer.class)
                .put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, RoundRobinAssignor.class.getName())
                .put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaProperties.getKafkaMaxPollRecords())
                .put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProperties.isKafkaAutocommitEnabled())
                .put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, kafkaProperties.getKafkaAutocommitIntervalMs())
                .put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 1000 * kafkaProperties.getKafkaConsumerSessionTimeoutSec())
                .build();
    }

    @Bean(CONSUMER_FACTORY)
    public ConsumerFactory<String, KafkaMessage> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                ImmutableMap.<String, Object>builder()
                        .putAll(defaultConsumerProps())
                        .put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId())
                        .build());
    }

    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(true);
        factory.setMessageConverter(new BatchMessagingMessageConverter(converter()));
        return factory;
    }

    @Bean
    public StringJsonMessageConverter converter() {
        return new StringJsonMessageConverter();
    }

    @Bean(RESPONSE_KAFKA_HANDLER)
    public KafkaProducerMessageHandlerSpec<String, KafkaMessage, ?> responseKafkaHandler() {
        ProducerFactory<String, KafkaMessage> producerFactory =
                new DefaultKafkaProducerFactory<>(ImmutableMap.<String, Object>builder()
                        .put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServer())
                        .put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
                        .put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaMessageSerializer.class)
                        .put(ProducerConfig.LINGER_MS_CONFIG, kafkaProperties.getKafkaProducerLingerMs())
                        .put(ProducerConfig.MAX_BLOCK_MS_CONFIG, kafkaProperties.getKafkaProducerMaxBlockMs()).build());
        return Kafka.outboundChannelAdapter(producerFactory)
                .<KafkaMessage>messageKey(m -> m.getPayload().getId())
                .headerMapper(mapper())
                .<KafkaMessage>topic(m -> kafkaProperties.getOutTopic())
                .configureKafkaTemplate(t -> t.id(RESPONSE_KAFKA_HANDLER));
    }

    @Bean
    public DefaultKafkaHeaderMapper mapper() {
        return new DefaultKafkaHeaderMapper();
    }
}
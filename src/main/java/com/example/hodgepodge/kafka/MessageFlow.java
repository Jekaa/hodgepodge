package com.example.hodgepodge.kafka;

import com.example.hodgepodge.kafka.model.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.advice.ErrorMessageSendingRecoverer;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.dsl.KafkaProducerMessageHandlerSpec;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.integration.kafka.support.RawRecordHeaderErrorMessageStrategy;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.messaging.MessageChannel;

import static org.springframework.messaging.MessageHeaders.ERROR_CHANNEL;

@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@ComponentScan
@IntegrationComponentScan
@Slf4j
public class MessageFlow {

    private static final String CONSUMER_FACTORY = "consumerFactory";
    private static final String IN_RQ_LISTENER_CONTAINER = "inRqListenerContainer";

    @Bean
    public IntegrationFlow inFlow(@Qualifier(CONSUMER_FACTORY) ConsumerFactory<String, KafkaMessage> consumerFactory,
                                  @Qualifier(ERROR_CHANNEL) MessageChannel errorChannel,
                                  KafkaProperties properties,
                                  MessageHandler handler) {
        return IntegrationFlows
                .from(Kafka.messageDrivenChannelAdapter(
                        consumerFactory,
                        KafkaMessageDrivenChannelAdapter.ListenerMode.record,
                        properties.getInTopic())
                        .configureListenerContainer(c -> c.id(IN_RQ_LISTENER_CONTAINER)
                                .ackMode(ContainerProperties.AckMode.TIME)
                                .ackTime(properties.getKafkaAutocommitIntervalMs())
                                .concurrency(properties.getKafkaListenerConcurrency()))
                        .recoveryCallback(new ErrorMessageSendingRecoverer(errorChannel,
                                new RawRecordHeaderErrorMessageStrategy())))
                .wireTap(sf -> sf.handle(msg -> log.info("Request(s) received:{}", msg)))
                .filter(KafkaMessage.class, this::validateRequest,
                        f -> f.discardFlow(sf -> sf.handle(msg -> {
                            log.debug("Invalid dto, ignoring: {}", msg);
                        })))
                .handle(msg -> handler.handle(msg.getHeaders(), (KafkaMessage) msg.getPayload()))
                .get();
    }

    @Bean
    public IntegrationFlow outFlow(KafkaProducerMessageHandlerSpec<String, KafkaMessage, ?> handler) {
        return IntegrationFlows.from(OutGateway.CHANNEL)
                .wireTap(sf -> sf.handle(msg -> log.info("Response sent: {}", msg)))
                .handle(handler)
                .get();
    }

    private boolean validateRequest(KafkaMessage message) {
        if (message == null) {
            log.error("Unparsable message");
            return false;
        }
        return true;
    }
}


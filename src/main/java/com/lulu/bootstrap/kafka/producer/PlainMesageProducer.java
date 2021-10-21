package com.lulu.bootstrap.kafka.producer;

import com.lulu.bootstrap.kafka.api.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@Slf4j
public class PlainMesageProducer {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private KafkaTemplate<String, String> kafkaPlainTemplate;

    @Value("${topic-name}")
    String topicName;

    public MessageResponse produceJsonMessage(String jsonMessage) throws IOException {
        log.info("Sending plain json message to kafka");
        kafkaPlainTemplate.send(topicName,jsonMessage);
        log.info("Json message sent successfully to kafka");
        return new MessageResponse("Message successfully sent to kafka");
    }

}
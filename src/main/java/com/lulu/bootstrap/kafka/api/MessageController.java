package com.lulu.bootstrap.kafka.api;

import com.lulu.bootstrap.kafka.producer.AvroMessageProducer;
import com.lulu.bootstrap.kafka.producer.PlainMesageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
public class MessageController {

    @Autowired
    private AvroMessageProducer avroProducer;

    @Autowired
    private PlainMesageProducer plainMesageProducer;

    @PostMapping("/publish-avro")
    public MessageResponse producerAvroSpecificMessage(@RequestBody MessageRequest messageRequest) {
        log.info("Received publish request for AVRO using specific record class for Product {} with name {}",messageRequest.getId(), messageRequest.getName());
        MessageResponse response = avroProducer.produceAvroMessage(messageRequest);
        log.info("Finished publishing new AVRO message using specific record class for Product {} with name {}",messageRequest.getId(), messageRequest.getName());
        return response;
    }

    @PostMapping("/publish-plain")
    public MessageResponse producerPlainMessage(@RequestBody String messageRequest) throws IOException {
        log.info("Received publish request for plain json {}",messageRequest);
        MessageResponse response = plainMesageProducer.produceJsonMessage(messageRequest);
        log.info("Finished publishing new plain json message");
        return response;
    }

    @PostMapping("/publish-avro-generic")
    public MessageResponse producerAvroGEnericMessage(@RequestBody String messageRequest) throws IOException {
        log.info("Received publish request for AVRO using generic record class. Request json {}",messageRequest);
        MessageResponse response = avroProducer.produceGenericAvroMessage(messageRequest);
        log.info("Finished publishing new AVRO message using generic record class");
        return response;
    }

}

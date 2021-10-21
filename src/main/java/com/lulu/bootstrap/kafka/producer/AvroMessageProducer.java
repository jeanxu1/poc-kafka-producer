package com.lulu.bootstrap.kafka.producer;

import com.lulu.bootstrap.kafka.api.MessageRequest;
import com.lulu.bootstrap.kafka.api.MessageResponse;
import com.lulu.bootstrap.kafka.avro.schema.Color;
import com.lulu.bootstrap.kafka.avro.schema.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class AvroMessageProducer {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private KafkaTemplate<String, Product> kafkaProductTemplate;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private KafkaTemplate<String, GenericRecord> kafkaGenericTemplate;

    @Value("${topic-name}")
    String topicName;

    public MessageResponse produceAvroMessage(MessageRequest request) {
        log.info("Compiling new AVRO message for product {}",request.getId());
        Product product = new Product();
        List<Color> productColors = new ArrayList<>();
        for(MessageRequest.Color requestColor:request.getColors()){
            Color productColor = new Color();
            productColor.setColorId(requestColor.getId());
            productColor.setColorName(requestColor.getName());
            productColors.add(productColor);
        }
        product.setProductId(request.getId());
        product.setProductName(request.getName());
        product.setProductDescription(request.getDescription());
        product.setColors(productColors);

        log.info("Sending AVRO message for product {} to kafka",request.getId());
        kafkaProductTemplate.send(topicName,product);
        log.info("AVRO message for product {} sent to kafka successfully",request.getId());

        return new MessageResponse("Message successfully sent to kafka");
    }

    public MessageResponse produceGenericAvroMessage(String jsonMessage) throws IOException {
        log.info("Compiling AVRO message using generic record");
        Schema schema = new Schema.Parser().parse(this.getClass().getClassLoader().getResourceAsStream("avro/product.avsc"));
        DatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);

        DecoderFactory decoderFactory = new DecoderFactory();
        Decoder decoder = decoderFactory.jsonDecoder(schema, jsonMessage);
        GenericRecord genericRecord = reader.read(null, decoder);

        log.info("Sending AVRO message to kafka");
        kafkaGenericTemplate.send(topicName,genericRecord);
        log.info("AVRO message sent to kafka successfully");
        return new MessageResponse("Message successfully sent to kafka");
    }

}
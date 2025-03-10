package com.example.demo.producer;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProducer {

    private static final Logger logger = LogManager.getLogger(OrderProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "orders";

    public void sendOrderEvent(String orderEvent) {
        kafkaTemplate.send(TOPIC, orderEvent);
        logger.info("Order event published: {}", orderEvent);
    }
}

package com.example.demo.consumer;

import com.example.demo.entity.Shipment;
import com.example.demo.entity.ShipmentEvent;
import com.example.demo.repository.ShipmentRepository;
import com.example.demo.repository.ShipmentEventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentEventConsumer {

    private static final Logger logger = LogManager.getLogger(ShipmentEventConsumer.class);
    private final ObjectMapper objectMapper;
    private final ShipmentRepository shipmentRepository;
    private final ShipmentEventRepository shipmentEventRepository;

    private static final DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @KafkaListener(topics = "orders", groupId = "order-group")
    public void consumeOrderEvent(String message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);

            Long orderId = rootNode.get("id").asLong();
            String customerName = rootNode.get("customerName").asText();
            JsonNode createdAtNode = rootNode.get("createdAt");
            JsonNode trackingNumberNode = rootNode.get("trackingNumber");

            // Use tracking number from event if present, otherwise generate a new one
            String trackingNumber = (trackingNumberNode != null && !trackingNumberNode.asText().isEmpty())
                ? trackingNumberNode.asText()
                : "TRK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            OffsetDateTime createdAt = null;
            if (createdAtNode != null) {
                createdAt = OffsetDateTime.of(
                    LocalDateTime.parse(createdAtNode.asText(), CUSTOM_FORMATTER),
                    ZoneOffset.UTC
                );
            }

            Shipment shipment = Shipment.builder()
                .orderId(orderId)
                .trackingNumber(trackingNumber)
                .status("Processing")
                .createdAt(LocalDateTime.now())
                .build();

            shipmentRepository.save(shipment);


            ShipmentEvent shipmentEvent = ShipmentEvent.builder()
                .shipment(shipment)
                .eventType("CREATED")
                .eventDescription("Shipment created for order " + orderId)
                .updatedAt(LocalDateTime.now()) 
                .build();

            shipmentEventRepository.save(shipmentEvent);

            logger.info("Shipment created for Order ID: {}, Tracking Number: {}", orderId, trackingNumber);

        } catch (Exception e) {
            logger.error("Failed to process Kafka message: {}", e.getMessage(), e);
        }
    }
}

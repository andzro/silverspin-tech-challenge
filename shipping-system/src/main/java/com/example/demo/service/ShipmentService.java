package com.example.demo.service;

import com.example.demo.entity.Shipment;
import com.example.demo.entity.ShipmentEvent;
import com.example.demo.generated.model.ShipmentRequest;
import com.example.demo.generated.model.ShipmentResponse;
import com.example.demo.generated.model.ShipmentUpdateRequest;
import com.example.demo.generated.model.PaginatedShipmentResponse;
import com.example.demo.repository.ShipmentRepository;
import com.example.demo.repository.ShipmentEventRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private static final Logger logger = LogManager.getLogger(ShipmentService.class);
    private final ShipmentRepository shipmentRepository;
    private final ShipmentEventRepository shipmentEventRepository;

    public PaginatedShipmentResponse getShipments(int page, int size, String status, String trackingNumber) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Shipment> shipmentPage;

        if (status != null && trackingNumber != null) {
            shipmentPage = shipmentRepository.findByStatusAndTrackingNumber(status, trackingNumber, pageable);
        } else if (status != null) {
            shipmentPage = shipmentRepository.findByStatus(status, pageable);
        } else if (trackingNumber != null) {
            shipmentPage = shipmentRepository.findByTrackingNumber(trackingNumber, pageable);
        } else {
            shipmentPage = shipmentRepository.findAll(pageable);
        }

        return new PaginatedShipmentResponse()
                .content(shipmentPage.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .totalPages(shipmentPage.getTotalPages())
                .totalElements((int) shipmentPage.getTotalElements())
                .size(shipmentPage.getSize())
                .number(shipmentPage.getNumber());
    }

    public ShipmentResponse createShipment(ShipmentRequest shipmentRequest) {
        String trackingNumber = "TRK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Shipment shipment = Shipment.builder()
                .orderId(shipmentRequest.getOrderId().longValue())
                .trackingNumber(trackingNumber)
                .status("Processing")
                .createdAt(LocalDateTime.now())
                .build();

        shipmentRepository.save(shipment);

        ShipmentEvent shipmentEvent = ShipmentEvent.builder()
                .shipment(shipment)
                .eventType("Shipment Created")
                .eventDescription("New shipment created with tracking number " + trackingNumber)
                .updatedAt(LocalDateTime.now())
                .build();

        shipmentEventRepository.save(shipmentEvent);

        logger.info("Shipment created for Order ID: {}, Tracking Number: {}", shipmentRequest.getOrderId(), trackingNumber);

        return mapToResponse(shipment);
    }

    public Optional<ShipmentResponse> getShipmentById(Long id) {
        return shipmentRepository.findById(id).map(this::mapToResponse);
    }

    public Optional<ShipmentResponse> getShipmentByTrackingNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber)
                .map(this::mapToResponse);
    }
    

    public ShipmentResponse updateShipmentStatus(Long id, ShipmentUpdateRequest updateRequest) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        if ("Delivered".equalsIgnoreCase(shipment.getStatus())) {
            throw new RuntimeException("Cannot update status if already delivered");
        }

        shipment.setStatus(updateRequest.getStatus());
        shipmentRepository.save(shipment);

        ShipmentEvent shipmentEvent = ShipmentEvent.builder()
                .shipment(shipment)
                .eventType(updateRequest.getStatus())
                .eventDescription("Shipment status updated to " + updateRequest.getStatus())
                .updatedAt(LocalDateTime.now())
                .build();

        shipmentEventRepository.save(shipmentEvent);

        logger.info("Shipment status updated for ID: {} to {}", id, updateRequest.getStatus());

        return mapToResponse(shipment);
    }

    public void cancelShipment(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        if ("Shipped".equalsIgnoreCase(shipment.getStatus())) {
            throw new RuntimeException("Cannot cancel shipment if already shipped");
        }

        shipmentRepository.delete(shipment);

        ShipmentEvent shipmentEvent = ShipmentEvent.builder()
                .shipment(shipment)
                .eventType("Canceled")
                .eventDescription("Shipment canceled")
                .updatedAt(LocalDateTime.now())
                .build();

        shipmentEventRepository.save(shipmentEvent);

        logger.info("Shipment canceled for ID: {}", id);
    }

    private ShipmentResponse mapToResponse(Shipment shipment) {
        return new ShipmentResponse()
                .id(shipment.getId().intValue())
                .orderId(shipment.getOrderId().intValue())
                .trackingNumber(shipment.getTrackingNumber())
                .status(shipment.getStatus())
                .createdAt(shipment.getCreatedAt().atOffset(ZoneOffset.UTC));
    }
}

package com.example.demo.controller;

import com.example.demo.generated.model.ShipmentRequest;
import com.example.demo.generated.model.ShipmentResponse;
import com.example.demo.generated.model.ShipmentUpdateRequest;
import com.example.demo.generated.model.PaginatedShipmentResponse;
import com.example.demo.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping
    public ResponseEntity<PaginatedShipmentResponse> getShipments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String trackingNumber) {
        return ResponseEntity.ok(shipmentService.getShipments(page, size, status, trackingNumber));
    }

    @PostMapping
    public ResponseEntity<ShipmentResponse> createShipment(@RequestBody ShipmentRequest shipmentRequest) {
        return ResponseEntity.ok(shipmentService.createShipment(shipmentRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponse> getShipmentById(@PathVariable Long id) {
        Optional<ShipmentResponse> shipmentResponse = shipmentService.getShipmentById(id);
        return shipmentResponse.map(ResponseEntity::ok)
                               .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShipmentResponse> updateShipmentStatus(
            @PathVariable Long id,
            @RequestBody ShipmentUpdateRequest updateRequest) {
        return ResponseEntity.ok(shipmentService.updateShipmentStatus(id, updateRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelShipment(@PathVariable Long id) {
        shipmentService.cancelShipment(id);
        return ResponseEntity.noContent().build();
    }

}

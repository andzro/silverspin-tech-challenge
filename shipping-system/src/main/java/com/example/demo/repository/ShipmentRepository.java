package com.example.demo.repository;

import com.example.demo.entity.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Page<Shipment> findByStatus(String status, Pageable pageable);

    Page<Shipment> findByTrackingNumber(String trackingNumber, Pageable pageable);

    Page<Shipment> findByStatusAndTrackingNumber(String status, String trackingNumber, Pageable pageable);

    Optional<Shipment> findByTrackingNumber(String trackingNumber);
}

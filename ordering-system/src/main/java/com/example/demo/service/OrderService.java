package com.example.demo.service;

import com.example.demo.generated.model.OrderRequest;
import com.example.demo.generated.model.OrderResponse;
import com.example.demo.generated.model.PaginatedOrderResponse;
import com.example.demo.entity.Order;
import com.example.demo.repository.OrderRepository;
import com.example.demo.producer.OrderProducer;
import com.example.demo.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger logger = LogManager.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;
    private final ObjectMapper objectMapper;

    public OrderResponse createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setCustomerName(orderRequest.getCustomerName());
        order.setStatus("PENDING");
        order.setTrackingNumber(generateTrackingNumber());

        Order savedOrder = orderRepository.save(order);

        try {
            String orderJson = objectMapper.writeValueAsString(savedOrder);
            orderProducer.sendOrderEvent(orderJson);
            logger.info("Order created and published: {}", orderJson);
        } catch (Exception e) {
            logger.error("Failed to send Kafka event: {}", e.getMessage(), e);
        }

        return mapToResponse(savedOrder);
    }

    public PaginatedOrderResponse getOrders(int page, int size, String status, String customerName) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Order> spec = Specification.where(null);
        if (status != null) {
            spec = spec.and(OrderSpecification.hasStatus(status));
        }
        if (customerName != null) {
            spec = spec.and(OrderSpecification.hasCustomerName(customerName));
        }

        Page<Order> orderPage = orderRepository.findAll(spec, pageable);

        List<OrderResponse> orderResponses = orderPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        PaginatedOrderResponse response = new PaginatedOrderResponse();
        response.setContent(orderResponses);
        response.setTotalPages(orderPage.getTotalPages());
        response.setTotalElements(Math.toIntExact(orderPage.getTotalElements()));
        response.setSize(orderPage.getSize());
        response.setNumber(orderPage.getNumber());

        return response;
    }

    private String generateTrackingNumber() {
        return "TRK" + new Random().nextInt(100000);
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId() != null ? Math.toIntExact(order.getId()) : null);
        response.setCustomerName(order.getCustomerName());
        response.setStatus(order.getStatus());
        response.setTrackingNumber(order.getTrackingNumber());
        return response;
    }
}

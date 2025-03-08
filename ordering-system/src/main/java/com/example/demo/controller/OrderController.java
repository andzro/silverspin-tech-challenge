package com.example.demo.controller;

import com.example.demo.generated.api.OrdersApi;
import com.example.demo.generated.model.OrderRequest;
import com.example.demo.generated.model.OrderResponse;
import com.example.demo.generated.model.PaginatedOrderResponse;
import com.example.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrdersApi {

    private final OrderService orderService;

    @GetMapping("/api/orders")
    public ResponseEntity<PaginatedOrderResponse> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String customerName) {
        return ResponseEntity.ok(orderService.getOrders(page, size, status, customerName));
    }

    @PostMapping("/api/orders")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
        OrderResponse response = orderService.createOrder(orderRequest);
        return ResponseEntity.ok(response);
    }
}

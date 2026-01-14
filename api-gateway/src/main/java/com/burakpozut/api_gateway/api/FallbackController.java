package com.burakpozut.api_gateway.api;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/customer")
    public ResponseEntity<Map<String, Object>> customerFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE) // TODO: make this response typed
                .body(Map.of(
                        "error", "Service Unavailable",
                        "message", "Customer service is currently unavailable.",
                        "service", "customer-service",
                        "status", 503));
    }

    @GetMapping("/product")
    public ResponseEntity<Map<String, Object>> productFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Service Unavailable",
                        "message", "Product service is currently unavailable. Circuit breaker is open.",
                        "service", "product-service",
                        "status", 503));
    }

    @GetMapping("/order")
    public ResponseEntity<Map<String, Object>> orderFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Service Unavailable",
                        "message", "Order service is currently unavailable. Circuit breaker is open.",
                        "service", "order-service",
                        "status", 503));
    }

    @GetMapping("/payment")
    public ResponseEntity<Map<String, Object>> paymentFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Service Unavailable",
                        "message", "Payment service is currently unavailable. Circuit breaker is open.",
                        "service", "payment-service",
                        "status", 503));
    }

    @GetMapping("/notification")
    public ResponseEntity<Map<String, Object>> notificationFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Service Unavailable",
                        "message", "Notification service is currently unavailable. Circuit breaker is open.",
                        "service", "notification-service",
                        "status", 503));
    }
}

package com.burakpozut.api_gateway.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "API Gateway", description = "API Gateway Information")
public class GatewayInfoController {

    @GetMapping("/")
    @Operation(summary = "Get API Gateway Information")
    public ResponseEntity<Map<String, Object>> getGatewayInfo() {
        return ResponseEntity.ok(Map.of(
                "name", "API Gateway",
                "status", "UP",
                "services", Map.of(
                        "customers", "/api/customers",
                        "products", "/api/products",
                        "orders", "/api/orders",
                        "payments", "/api/payments",
                        "notifications", "/api/notifications"),
                "swagger", "/swagger-ui.html"));
    }
}
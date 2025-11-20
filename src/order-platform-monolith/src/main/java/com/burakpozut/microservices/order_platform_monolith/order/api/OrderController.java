package com.burakpozut.microservices.order_platform_monolith.order.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.microservices.order_platform_monolith.order.api.dto.OrderResponse;
import com.burakpozut.microservices.order_platform_monolith.order.application.query.GetOrderDetailsQuery;
import com.burakpozut.microservices.order_platform_monolith.order.application.service.GetOrderDetailsService;
import com.burakpozut.microservices.order_platform_monolith.order.domain.Order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("api/orders")
public class OrderController {

  private final GetOrderDetailsService getOrderDetailsService;

  @GetMapping("/{id}")
  public ResponseEntity<OrderResponse> getById(@PathVariable("id") UUID id) {
    var query = new GetOrderDetailsQuery(id);
    Order order = getOrderDetailsService.handle(query);

    OrderResponse response = OrderResponse.builder().id(order.getId()).customerId(order.getCustomerId())
        .status(order.getOrderStatus()).build();

    return ResponseEntity.ok(response);
  }

}

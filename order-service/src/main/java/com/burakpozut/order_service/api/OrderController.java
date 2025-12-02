package com.burakpozut.order_service.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.order_service.api.dto.response.OrderResponse;
import com.burakpozut.order_service.app.service.GetAllOrdersService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
  private final GetAllOrdersService getAllOrders;

  @GetMapping()
  public ResponseEntity<List<OrderResponse>> getAll() {
    var orders = getAllOrders.handle();
    var body = orders.stream().map(OrderResponse::from).collect(Collectors.toList());
    return ResponseEntity.ok(body);
  }

}

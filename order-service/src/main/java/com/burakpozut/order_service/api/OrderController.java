package com.burakpozut.order_service.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.order_service.api.dto.request.CreateOrderRequest;
import com.burakpozut.order_service.api.dto.request.UpdateOrderRequest;
import com.burakpozut.order_service.api.dto.response.OrderResponse;
import com.burakpozut.order_service.app.command.CreateOrderCommand;
import com.burakpozut.order_service.app.command.UpdateOrderCommand;
import com.burakpozut.order_service.app.service.CreateOrderService;
import com.burakpozut.order_service.app.service.GetAllOrdersService;
import com.burakpozut.order_service.app.service.GetOrderByIdService;
import com.burakpozut.order_service.app.service.UpdateOrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
  private final GetAllOrdersService getAllOrders;
  private final GetOrderByIdService getOrderByIdService;
  private final CreateOrderService createOrderService;
  private final UpdateOrderService updateOrderService;

  @GetMapping()
  public ResponseEntity<List<OrderResponse>> getAll() {
    var orders = getAllOrders.handle();
    var body = orders.stream().map(OrderResponse::from).collect(Collectors.toList());
    return ResponseEntity.ok(body);
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderResponse> getById(@PathVariable UUID id) {
    var order = getOrderByIdService.handle(id);
    var body = OrderResponse.from(order);
    return ResponseEntity.ok(body);
  }

  @PostMapping()
  public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
    var command = CreateOrderCommand.of(request.customerId(), request.status(),
        request.currency(), request.items());

    var savedOrder = createOrderService.handle(command);
    return ResponseEntity.ok(OrderResponse.from(savedOrder));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<OrderResponse> updateOrder(@PathVariable UUID id,
      @Valid @RequestBody UpdateOrderRequest request) {
    var command = UpdateOrderCommand.of(request.customerId(), request.status(), request.currency());
    var order = updateOrderService.handle(id, command);
    var body = OrderResponse.from(order);
    return ResponseEntity.ok(body);
  }

}

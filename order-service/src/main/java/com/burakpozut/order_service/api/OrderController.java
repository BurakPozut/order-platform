package com.burakpozut.order_service.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.order_service.api.dto.request.CreateOrderRequest;
import com.burakpozut.order_service.api.dto.request.UpdateOrderItemRequest;
import com.burakpozut.order_service.api.dto.request.UpdateOrderRequest;
import com.burakpozut.order_service.api.dto.response.OrderResponse;
import com.burakpozut.order_service.api.mapper.OrderMapper;
import com.burakpozut.order_service.app.service.CreateOrderService;
import com.burakpozut.order_service.app.service.GetAllOrdersService;
import com.burakpozut.order_service.app.service.GetOrderByIdService;
import com.burakpozut.order_service.app.service.UpdateOrderItemService;
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
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
  private final GetAllOrdersService getAllOrders;
  private final GetOrderByIdService getOrderByIdService;
  private final CreateOrderService createOrderService;
  private final UpdateOrderService updateOrderService;
  private final UpdateOrderItemService updateOrderItemService;

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

    var command = OrderMapper.toCommand(request);

    var savedOrder = createOrderService.handle(command);
    return ResponseEntity.ok(OrderResponse.from(savedOrder));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<OrderResponse> updateOrder(@PathVariable UUID id,
      @Valid @RequestBody UpdateOrderRequest request) {

    var command = OrderMapper.toCommand(request);
    var order = updateOrderService.handle(id, command);
    var body = OrderResponse.from(order);
    return ResponseEntity.ok(body);
  }

  @PutMapping("{orderId}/items/{itemId}")
  public ResponseEntity<OrderResponse> updateOrderItem(@PathVariable UUID orderId,
      @PathVariable UUID itemId, @Valid @RequestBody UpdateOrderItemRequest request) {

    var command = OrderMapper.toCommand(request);

    var saved = updateOrderItemService.handle(orderId, itemId, command);
    var body = OrderResponse.from(saved);

    return ResponseEntity.ok(body);

  }

}

package com.burakpozut.order_service.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.order_service.api.dto.request.CreateOrderRequest;
import com.burakpozut.order_service.api.dto.request.UpdateOrderItemRequest;
import com.burakpozut.order_service.api.dto.request.UpdateOrderRequest;
import com.burakpozut.order_service.api.dto.response.OrderResponse;
import com.burakpozut.order_service.app.service.GetAllOrdersService;
import com.burakpozut.order_service.app.service.GetOrderByIdService;
import com.burakpozut.order_service.app.service.UpdateOrderItemService;
import com.burakpozut.order_service.app.service.UpdateOrderService;
import com.burakpozut.order_service.app.service.create.CreateOrderOrchestrator;
// import com.burakpozut.order_service.app.service.create.OrderCreationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
  private final UpdateOrderService updateOrderService;
  private final UpdateOrderItemService updateOrderItemService;
  private final CreateOrderOrchestrator createOrderOrchestrator;

  @GetMapping()
  public ResponseEntity<Slice<OrderResponse>> getAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    Pageable pagealbe = PageRequest.of(page, size);
    var orderSlice = getAllOrders.handle(pagealbe);
    var body = orderSlice.map(OrderResponse::from);
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

    // var savedOrder = createOrderService.create(command);
    var savedOrder = createOrderOrchestrator.handle(command);
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

package com.burakpozut.microservices.order_platform.order.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.microservices.order_platform.order.api.dto.CreateOrderRequest;
import com.burakpozut.microservices.order_platform.order.api.dto.OrderResponse;
import com.burakpozut.microservices.order_platform.order.application.command.CreateOrderCommand;
import com.burakpozut.microservices.order_platform.order.application.query.GetOrderDetailsQuery;
import com.burakpozut.microservices.order_platform.order.application.service.CreateOrderService;
import com.burakpozut.microservices.order_platform.order.application.service.GetAllOrdersService;
import com.burakpozut.microservices.order_platform.order.application.service.GetOrderDetailsService;
import com.burakpozut.microservices.order_platform.order.domain.Order;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("api/orders")
public class OrderController {

  private final GetOrderDetailsService getOrderDetailsService;
  private final CreateOrderService createOrderService;
  private final GetAllOrdersService getAllOrdersService;

  @GetMapping
  public ResponseEntity<List<OrderResponse>> getAll() {
    var orders = getAllOrdersService.handle();
    var response = orders.stream().map(OrderResponse::from).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderResponse> getById(
      @Parameter(description = "Order Id", required = true, schema = @Schema(type = "string", format = "uuid", example = "4e0d25e9-cc74-4462-8ddc-e9a9d43f067a")) @PathVariable("id") UUID id) {
    var query = new GetOrderDetailsQuery(id);
    Order order = getOrderDetailsService.handle(query);

    return ResponseEntity.ok(OrderResponse.from(order));
  }

  @PostMapping()
  public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
    // Map DTO items to Command items
    var commandItems = request.items().stream()
        .map(item -> new CreateOrderCommand.OrderItemData(item.productId(), item.quantity()))
        .collect(Collectors.toList());
    // request.items().add(new
    // CreateOrderRequest.OrderItemRequest(UUID.randomUUID(), 5));

    var command = new CreateOrderCommand(request.customerId(), request.status(),
        request.currency(), commandItems);
    var order = createOrderService.handle(command);
    var response = new OrderResponse(order.getId(), order.getCustomerId(), order.getOrderStatus());

    return ResponseEntity.ok(response);
  }

}

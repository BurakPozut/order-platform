package com.burakpozut.order_service.app.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.order_service.api.dto.request.OrderItemRequest;
import com.burakpozut.order_service.app.command.CreateOrderCommand;
import com.burakpozut.order_service.app.exception.customer.CustomerNotFoundException;
import com.burakpozut.order_service.app.exception.product.ProductNotFoundException;
import com.burakpozut.order_service.domain.Order;
import com.burakpozut.order_service.domain.OrderItem;
import com.burakpozut.order_service.domain.OrderRepository;
import com.burakpozut.order_service.domain.ProductInfo;
import com.burakpozut.order_service.domain.gateway.CustomerGateway;
import com.burakpozut.order_service.domain.gateway.ProductGateway;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateOrderService {
  private final OrderRepository orderRepository;
  private final CustomerGateway customerGateway;
  private final ProductGateway productGateway;

  @Transactional
  public Order handle(CreateOrderCommand command) {
    if (!customerGateway.validateCustomerExists(command.customerId())) {
      throw new CustomerNotFoundException(command.customerId());
    }

    List<UUID> productIds = command.items().stream().map(OrderItemRequest::productId).distinct().toList();

    // Fetch all in parallel
    Map<UUID, ProductInfo> productsMap = productGateway.getProductsByIds(productIds);

    // Validate all products were found
    for (UUID productId : productIds) {
      if (!productsMap.containsKey(productId)) {
        throw new ProductNotFoundException(productId);
      }
    }

    List<OrderItem> orderItems = command.items().stream()
        .map(itemRequest -> {
          ProductInfo productInfo = productsMap.get(itemRequest.productId());
          return OrderItem.rehydrate(
              UUID.randomUUID(),
              productInfo.productId(), productInfo.name(), productInfo.price(), itemRequest.quantity());
        }).toList();

    // Calculate total amount
    BigDecimal totalAmount = orderItems.stream()
        .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    Order order = Order.of(command.customerId(), command.status(), totalAmount, command.currency(), orderItems);
    return orderRepository.save(order, true);
  }

}

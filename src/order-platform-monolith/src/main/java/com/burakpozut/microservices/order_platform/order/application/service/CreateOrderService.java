package com.burakpozut.microservices.order_platform.order.application.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.microservices.order_platform.common.exception.DomainValidationException;
import com.burakpozut.microservices.order_platform.customer.application.exception.CustomerNotFoundException;
import com.burakpozut.microservices.order_platform.order.application.command.CreateOrderCommand;
import com.burakpozut.microservices.order_platform.order.domain.Order;
import com.burakpozut.microservices.order_platform.order.domain.OrderItem;
import com.burakpozut.microservices.order_platform.order.domain.OrderItemRepository;
import com.burakpozut.microservices.order_platform.order.domain.OrderRepository;
import com.burakpozut.microservices.order_platform.order.domain.port.CustomerGateway;
import com.burakpozut.microservices.order_platform.order.domain.port.ProductGateway;
import com.burakpozut.microservices.order_platform.product.application.exception.ProductNotAvailbaleException;
import com.burakpozut.microservices.order_platform.product.application.exception.ProductNotFoundException;
import com.burakpozut.microservices.order_platform.product.domain.Product;
import com.burakpozut.microservices.order_platform.product.domain.ProductStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateOrderService {
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final CustomerGateway customerGateway;
  private final ProductGateway productGateway;

  @Transactional
  public Order handle(CreateOrderCommand command) {
    if (!customerGateway.customerExists(command.customerId()))
      throw new CustomerNotFoundException(command.customerId());

    Set<UUID> productIds = command.items().stream().map(CreateOrderCommand.OrderItemData::productId)
        .collect(Collectors.toSet());

    // Fetching all products in one query no N queries
    Map<UUID, Product> products = productGateway.getProductsById(productIds);

    BigDecimal totalAmount = BigDecimal.ZERO;

    // Validating all products exist
    for (var item : command.items()) {
      Product product = products.get(item.productId());

      if (product == null)
        throw new ProductNotFoundException(item.productId());

      if (product.getStatus() != ProductStatus.ACTIVE)
        throw new ProductNotAvailbaleException(product.getId());
      if (product.getCurrency() != command.currency())
        throw new DomainValidationException(
            "Product currentcy " + product.getCurrency() + " does not match the order currency " + command.currency());

      totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.quantity())));

    }

    var order = Order.createNew(command.customerId(), command.status(), totalAmount,
        command.currency());
    var savedOrder = orderRepository.save(order, true);

    // saving order items
    List<OrderItem> orderItems = command.items().stream().map(item -> {
      Product product = products.get(item.productId());
      return OrderItem.createNew(savedOrder.getId(), item.productId(), product.getName(), product.getPrice(),
          item.quantity());
    }).collect(Collectors.toList());

    orderItemRepository.saveAll(orderItems, true);

    return savedOrder;
  }

}

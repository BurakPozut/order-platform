package com.burakpozut.microservices.order_platform_monolith.order.application.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.microservices.order_platform_monolith.customer.application.exception.CustomerNotFoundException;
import com.burakpozut.microservices.order_platform_monolith.order.application.command.CreateOrderCommand;
import com.burakpozut.microservices.order_platform_monolith.order.domain.Order;
import com.burakpozut.microservices.order_platform_monolith.order.domain.OrderItem;
import com.burakpozut.microservices.order_platform_monolith.order.domain.OrderItemRepository;
import com.burakpozut.microservices.order_platform_monolith.order.domain.OrderRepository;
import com.burakpozut.microservices.order_platform_monolith.order.domain.port.CustomerGateway;
import com.burakpozut.microservices.order_platform_monolith.order.domain.port.ProductGateway;
import com.burakpozut.microservices.order_platform_monolith.product.application.exception.ProductNotFoundException;
import com.burakpozut.microservices.order_platform_monolith.product.domain.Product;
import com.burakpozut.microservices.order_platform_monolith.product.domain.ProductStatus;

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
    if (!customerGateway.customerExists(command.getCustomerId()))
      throw new CustomerNotFoundException(command.getCustomerId());

    Set<UUID> productIds = command.getItems().stream().map(CreateOrderCommand.OrderItemData::getProductId)
        .collect(Collectors.toSet());

    // Fetching all products in one query no N queries
    Map<UUID, Product> products = productGateway.getProductsById(productIds);

    // Validating all products exist
    for (var item : command.getItems()) {
      if (!products.containsKey(item.getProductId())) {
        throw new ProductNotFoundException(item.getProductId());
      }

      // validate status

      var product = products.get(item.getProductId());
      if (product.getStatus() != ProductStatus.ACTIVE) {
        throw new ProductNotFoundException(product.getId());// TODO add product not available exception
      }
    }

    var order = Order.createNew(command.getCustomerId(), command.getStatus(), command.getTotalAmount(),
        command.getCurrency());
    var savedOrder = orderRepository.save(order, true);

    // saving order items
    List<OrderItem> orderItems = command.getItems().stream().map(item -> {
      Product product = products.get(item.getProductId());
      return OrderItem.createNew(savedOrder.getId(), item.getProductId(), product.getName(), product.getPrice(),
          item.getQuantity());
    }).collect(Collectors.toList());

    orderItemRepository.saveAll(orderItems, true);

    // TODO: add good error handling when I send bad product id it jsut showd 500
    // but it was user error
    // TODO: and also calculate the totalt amount using the proce and quantity in
    // the products
    return savedOrder;
  }

}

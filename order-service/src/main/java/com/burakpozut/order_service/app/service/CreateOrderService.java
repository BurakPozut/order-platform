package com.burakpozut.order_service.app.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.order_service.app.command.CreateOrderCommand;
import com.burakpozut.order_service.app.command.OrderItemData;
import com.burakpozut.order_service.app.exception.customer.CustomerNotFoundException;
import com.burakpozut.order_service.app.exception.product.ProductNotFoundException;
import com.burakpozut.order_service.domain.Order;
import com.burakpozut.order_service.domain.OrderItem;
import com.burakpozut.order_service.domain.OrderRepository;
import com.burakpozut.order_service.domain.ProductInfo;
import com.burakpozut.order_service.domain.gateway.CustomerGateway;
import com.burakpozut.order_service.domain.gateway.NotificationGateway;
import com.burakpozut.order_service.domain.gateway.PaymentGateway;
import com.burakpozut.order_service.domain.gateway.ProductGateway;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateOrderService {
  private static final int BUCKET_MINUTES = 5;

  private final OrderRepository orderRepository;
  private final CustomerGateway customerGateway;// TODO: calling externals like this is not good use outbox pattern or
                                                // event based communication
  private final ProductGateway productGateway;
  private final PaymentGateway paymentGateway;
  private final NotificationGateway notificationGateway;

  // TODO: create payment as wel if the save is successfull
  @Transactional
  public Order handle(CreateOrderCommand command) {
    if (!customerGateway.validateCustomerExists(command.customerId())) {
      throw new CustomerNotFoundException(command.customerId());
    }

    String idempotencyKey = deriveKey(command.customerId(), command.items());

    Optional<Order> existing = orderRepository.findByIdempotencyKey(idempotencyKey);
    if (existing.isPresent()) {
      return existing.get();// TODO: how does this get work or why do we return exiting get
    }

    List<UUID> productIds = command.items().stream().map(OrderItemData::productId).distinct().toList();

    // Fetch all in parallel
    Map<UUID, ProductInfo> productsMap = productGateway.getProductsByIds(productIds);

    // Validate all products were found
    for (UUID productId : productIds) {
      if (!productsMap.containsKey(productId)) {
        throw new ProductNotFoundException(productId);
      }
    }

    List<OrderItem> orderItems = command.items().stream()
        .map(itemData -> {
          ProductInfo productInfo = productsMap.get(itemData.productId());
          return OrderItem.rehydrate(
              UUID.randomUUID(),
              productInfo.productId(),
              productInfo.name(), productInfo.price(), itemData.quantity());
        }).toList();

    // Calculate total amount
    BigDecimal totalAmount = orderItems.stream()
        .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    log.debug("Idempotency key : " + idempotencyKey);
    Order order = Order.of(command.customerId(), command.status(), totalAmount, command.currency(), orderItems,
        idempotencyKey);
    var savedOrder = orderRepository.save(order, true);

    paymentGateway.createPayment(order.id(), totalAmount,
        order.currency(), "stripe", "stp-123"); // TODO: this provider is not good practice I believe

    notificationGateway.sendNotification(order.customerId(), order.id());

    return savedOrder;
  }

  private String deriveKey(UUID customerId, List<OrderItemData> items) {
    Instant now = Instant.now();
    long bucketStart = now.truncatedTo(ChronoUnit.MINUTES)
        .minus(now.getEpochSecond() / 60 % BUCKET_MINUTES, ChronoUnit.MINUTES)
        .getEpochSecond();

    String productPart = items.stream()
        .map(OrderItemData::productId)
        .sorted()
        .map(UUID::toString)
        .reduce("", (a, b) -> a.isEmpty() ? b : a + ":" + b);

    return customerId + ":" + productPart + ":" + items.size() + ":" + bucketStart;
  }
}

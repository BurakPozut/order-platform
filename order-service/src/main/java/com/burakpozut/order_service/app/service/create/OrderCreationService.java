package com.burakpozut.order_service.app.service.create;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.burakpozut.common.exception.ExternalServiceNotFoundException;
import com.burakpozut.order_service.app.command.CreateOrderCommand;
import com.burakpozut.order_service.app.command.OrderItemData;
import com.burakpozut.order_service.app.exception.customer.CustomerNotFoundException;
import com.burakpozut.order_service.domain.Order;
import com.burakpozut.order_service.domain.OrderItem;
import com.burakpozut.order_service.domain.ProductInfo;
import com.burakpozut.order_service.domain.gateway.CustomerGateway;
import com.burakpozut.order_service.domain.gateway.ProductGateway;
import com.burakpozut.order_service.domain.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCreationService {
    private static final int BUCKET_MINUTES = 5;

    private final OrderRepository orderRepository;
    private final CustomerGateway customerGateway;
    private final PlatformTransactionManager transactionManager;

    private final ProductGateway productGateway;

    public record CreationResult(Order order, boolean isNew) {
    }

    public CreationResult create(CreateOrderCommand command) {
        long currentBucket = System.currentTimeMillis() / (1000 * 60 * BUCKET_MINUTES);

        // Current bucket, most likely to get hit
        String currentKey = deriveKey(command.customerId(), command.items(), currentBucket);
        Optional<Order> existing = orderRepository.findByIdempotencyKey(currentKey);

        if (existing.isPresent()) {
            log.info("order.create.idempotent_hit bucket=current orderId={} customerId={}",
                    existing.get().id(), existing.get().customerId());
            return new CreationResult(existing.get(), false);
        }
        // Check Previous bucket (handles the boundry case)
        String prevKey = deriveKey(command.customerId(), command.items(), currentBucket - 1);
        Optional<Order> existingPrev = orderRepository.findByIdempotencyKey(prevKey);

        // This is where we need freshness check
        if (existingPrev.isPresent() && isRecent(existingPrev.get())) {
            log.info("order.create.idempotent_hit bucket=previous orderId={} customerId={}",
                    existingPrev.get().id(), existingPrev.get().customerId());

            return new CreationResult(existing.get(), false);
        }

        log.debug("order.create.validation.start customerId={} items={}",
                command.customerId(), command.items() != null ? command.items().size() : 0);

        var validationResult = validateAndFetchData(command);

        return new TransactionTemplate(transactionManager).execute(status -> {
            List<OrderItem> orderItems = createOrderItmes(command.items(),
                    validationResult.productsMap);

            Order order = Order.createFrom(
                    command.customerId(),
                    command.status(),
                    command.currency(), orderItems, currentKey);
            var savedOrder = orderRepository.save(order, true);

            log.info("order.create.persisted orderId={} customerId={} itemCount={}",
                    savedOrder.id(), savedOrder.customerId(), savedOrder.items().size());

            return new CreationResult(savedOrder, true);
        });
    }

    // #region Helper
    private String deriveKey(UUID customerId, List<OrderItemData> items, long bucket) {
        String productPart = items.stream()
                .sorted(Comparator.comparing(OrderItemData::productId))
                .map(i -> i.productId() + ":" + i.quantity())
                .collect(Collectors.joining("|"));

        return customerId + "::" + productPart + "::" + bucket;
    }

    private boolean isRecent(Order order) {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(BUCKET_MINUTES);
        return order.updatedAt().isAfter(deadline);
    }

    private ValidationResult validateAndFetchData(CreateOrderCommand command) {
        validateCustomerExists(command.customerId());

        List<UUID> productIds = command.items().stream()
                .map(OrderItemData::productId).distinct().toList();
        Map<UUID, ProductInfo> productsMap = productGateway.getProductsByIds(productIds);

        // Validation is handling from the product service from now on. This is more
        // event driven
        // for (UUID productId : productIds) {
        // if (!productsMap.containsKey(productId))
        // throw new ProductNotFoundException(productId);
        // }
        return new ValidationResult(productsMap);
    }

    private List<OrderItem> createOrderItmes(List<OrderItemData> itemsData, Map<UUID, ProductInfo> proudctsMap) {
        return itemsData.stream()
                .map(itemData -> {
                    ProductInfo productInfo = proudctsMap.get(itemData.productId());
                    return OrderItem.of(productInfo.productId(),
                            productInfo.name(), productInfo.price(), itemData.quantity());
                }).toList();
    }

    private void validateCustomerExists(UUID customerId) {
        try {
            customerGateway.validateCustomerExists(customerId);
        } catch (ExternalServiceNotFoundException e) {
            throw new CustomerNotFoundException(customerId);
        }
    }

    private record ValidationResult(Map<UUID, ProductInfo> productsMap) {
    }
    // #endregion
}

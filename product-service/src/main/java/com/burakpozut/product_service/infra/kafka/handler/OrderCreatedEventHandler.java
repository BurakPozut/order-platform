package com.burakpozut.product_service.infra.kafka.handler;

import com.burakpozut.common.domain.ServiceName;
import com.burakpozut.common.event.order.OrderCreatedEvent;
import com.burakpozut.common.event.order.OrderItemEvent;
import com.burakpozut.product_service.app.command.ReserveInventoryCommand;
import com.burakpozut.product_service.app.exception.InsufficentInventoryException;
import com.burakpozut.product_service.app.exception.ProductNotFoundException;
import com.burakpozut.product_service.app.service.RerserveInventoryService;
import com.burakpozut.product_service.infra.kafka.OrderCompensationPublisher;
import com.burakpozut.product_service.infra.kafka.ServiceCompletionPublisher;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedEventHandler {
    private final RerserveInventoryService rerserveInventoryService;
    private final OrderCompensationPublisher orderCompensationPublisher;
    private final ServiceCompletionPublisher serviceCompletionPublisher;

    public void handle(OrderCreatedEvent createdEvent) {
        log.info("product.orderCreated.received orderId={} customerId={} itemCount={}",
                createdEvent.orderId(), createdEvent.customerId(),
                createdEvent.items() != null ? createdEvent.items().size() : 0);

        try {
            for (OrderItemEvent item : createdEvent.items()) {
                var command = ReserveInventoryCommand.of(item.productId(), item.quantity());
                rerserveInventoryService.handle(command);
                log.debug("product.orderCreated.inventory_reserved orderId={} productId={} quantity={}",
                        createdEvent.orderId(), item.productId(), item.quantity());
            }

            serviceCompletionPublisher.publish(createdEvent.orderId(), ServiceName.PRODUCT);
            log.debug("product.orderCreated.completion_published orderId={} serviceName=PRODUCT",
                    createdEvent.orderId());
        } catch (ProductNotFoundException | InsufficentInventoryException e) {
            // Business validation failures - expected scenarios
            log.warn("product.orderCreated.validation_failed orderId={} reason={} action=compensate",
                    createdEvent.orderId(), e.getMessage());
            orderCompensationPublisher.publish(createdEvent.orderId(),
                    createdEvent.customerId(),
                    createdEvent.items(),
                    "Inventory reservation failed: " + e.getMessage());
        } catch (DataAccessException e) {
            // Technical database errors - might be retryable
            log.error("product.orderCreated.database_error orderId={} message={} action=compensate",
                    createdEvent.orderId(), e.getMessage(), e);
            orderCompensationPublisher.publish(createdEvent.orderId(),
                    createdEvent.customerId(),
                    createdEvent.items(),
                    "Database error during inventory reservation: " + e.getMessage());
            // Consider rethrowing if you want Kafka to retry
            throw e;
        } catch (Exception e) {
            // Unexpected technical errors
            log.error("product.orderCreated.unexpected_error orderId={} message={} action=compensate",
                    createdEvent.orderId(), e.getMessage(), e);
            orderCompensationPublisher.publish(createdEvent.orderId(),
                    createdEvent.customerId(),
                    createdEvent.items(),
                    "Unexpected error during inventory reservation: " + e.getMessage());
            // Consider rethrowing for Kafka retry mechanism
            throw e;
        }
    }
}
package com.burakpozut.payment_service.infra.persistance.kafka.handler;

import com.burakpozut.common.event.order.OrderCreatedEvent;
import com.burakpozut.common.exception.DomainValidationException;
import com.burakpozut.common.exception.ExternalServiceException;
import com.burakpozut.payment_service.app.command.CreatePaymentCommand;
import com.burakpozut.payment_service.app.exception.OrderNotFoundException;
import com.burakpozut.payment_service.app.service.CreatePaymentService;
import com.burakpozut.payment_service.domain.PaymentStatus;
import com.burakpozut.payment_service.infra.persistance.kafka.OrderCompensationPublisher;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedEventHandler {

  private final CreatePaymentService createPaymentService;
  private final OrderCompensationPublisher orderCompensationPublisher;

  public void handle(OrderCreatedEvent createdEvent) {
    log.info("Processing OrderCreated for order: {}", createdEvent.orderId());

    try {
      var command = CreatePaymentCommand.of(
          createdEvent.orderId(),
          createdEvent.totalAmount(),
          createdEvent.currency(), PaymentStatus.PENDING,
          "Paypal", "1234");

      createPaymentService.handle(command);
      log.info("Successfully created payment for order: {}", createdEvent.orderId());

    } catch (OrderNotFoundException | DomainValidationException e) {
      // Business validation failures - expected scenarios
      log.warn("Business validation failed for order: {}. Reason: {}",
          createdEvent.orderId(), e.getMessage());
      orderCompensationPublisher.publish(
          createdEvent.orderId(),
          createdEvent.customerId(),
          createdEvent.items(),
          "Payment creation failed: " + e.getMessage());
    } catch (ExternalServiceException e) {
      // External service errors (order service unavailable, network issues)
      log.error("External service error while creating payment for order: {}. Reason: {}",
          createdEvent.orderId(), e.getMessage(), e);
      orderCompensationPublisher.publish(
          createdEvent.orderId(),
          createdEvent.customerId(),
          createdEvent.items(),
          "Order service unavailable: " + e.getMessage());
      // Consider rethrowing if you want Kafka to retry
      throw e;
    } catch (DataAccessException e) {
      // Technical database errors - might be retryable
      log.error("Database error while creating payment for order: {}. Reason: {}",
          createdEvent.orderId(), e.getMessage(), e);
      orderCompensationPublisher.publish(
          createdEvent.orderId(),
          createdEvent.customerId(),
          createdEvent.items(),
          "Database error during payment creation: " + e.getMessage());
      // Consider rethrowing if you want Kafka to retry
      throw e;
    } catch (Exception e) {
      // Unexpected technical errors
      log.error("Unexpected error while creating payment for order: {}. Reason: {}",
          createdEvent.orderId(), e.getMessage(), e);
      orderCompensationPublisher.publish(
          createdEvent.orderId(),
          createdEvent.customerId(),
          createdEvent.items(),
          "Unexpected error during payment creation: " + e.getMessage());
      // Consider rethrowing for Kafka retry mechanism
      throw e;
    }
  }
}

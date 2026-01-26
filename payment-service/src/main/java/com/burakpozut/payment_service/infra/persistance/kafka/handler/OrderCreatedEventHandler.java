package com.burakpozut.payment_service.infra.persistance.kafka.handler;

import com.burakpozut.common.domain.ServiceName;
import com.burakpozut.common.event.order.OrderCreatedEvent;
import com.burakpozut.common.exception.DomainValidationException;
import com.burakpozut.common.exception.ExternalServiceException;
import com.burakpozut.payment_service.app.command.CreatePaymentCommand;
import com.burakpozut.payment_service.app.exception.OrderNotFoundException;
import com.burakpozut.payment_service.app.service.CreatePaymentService;
import com.burakpozut.payment_service.domain.PaymentStatus;
import com.burakpozut.payment_service.infra.persistance.kafka.OrderCompensationPublisher;
import com.burakpozut.payment_service.infra.persistance.kafka.ServiceCompletionPublisher;

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
    private final ServiceCompletionPublisher serviceCompletionPublisher;

    public void handle(OrderCreatedEvent createdEvent) {
        log.info("payment.orderCreated.received orderId={} customerId={} amount={}",
                createdEvent.orderId(), createdEvent.customerId(), createdEvent.totalAmount());

        try {
            var command = CreatePaymentCommand.of(
                    createdEvent.orderId(),
                    createdEvent.totalAmount(),
                    createdEvent.currency(), PaymentStatus.PENDING,
                    "Paypal", "1234");

            var payment = createPaymentService.handle(command);
            log.info("payment.orderCreated.payment_created paymentId={} orderId={} amount={}",
                    payment.id(), createdEvent.orderId(), payment.amount());

            serviceCompletionPublisher.publish(createdEvent.orderId(), ServiceName.PAYMENT);
            log.debug("payment.orderCreated.completion_published orderId={} serviceName=PAYMENT",
                    createdEvent.orderId());

        } catch (OrderNotFoundException | DomainValidationException e) {
            // Business validation failures - expected scenarios
            log.warn("payment.orderCreated.validation_failed orderId={} reason={} action=compensate",
                    createdEvent.orderId(), e.getMessage());
            orderCompensationPublisher.publish(
                    createdEvent.orderId(),
                    createdEvent.customerId(),
                    createdEvent.items(),
                    "Payment creation failed: " + e.getMessage());
        } catch (ExternalServiceException e) {
            // External service errors (order service unavailable, network issues)
            log.error("payment.orderCreated.external_service_error orderId={} message={} action=compensate",
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
            log.error("payment.orderCreated.database_error orderId={} message={} action=compensate",
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
            log.error("payment.orderCreated.unexpected_error orderId={} message={} action=compensate",
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

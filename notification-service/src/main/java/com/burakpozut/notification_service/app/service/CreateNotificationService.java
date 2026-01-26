package com.burakpozut.notification_service.app.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

import com.burakpozut.common.exception.DomainValidationException;
import com.burakpozut.common.exception.ExternalServiceException;
import com.burakpozut.common.exception.ExternalServiceNotFoundException;
import com.burakpozut.notification_service.app.command.CreateNotificationCommand;
import com.burakpozut.notification_service.app.exception.CustomerNotFoundException;
import com.burakpozut.notification_service.app.exception.OrderNotFoundException;
import com.burakpozut.notification_service.domain.Notification;
import com.burakpozut.notification_service.domain.NotificationRepository;
import com.burakpozut.notification_service.domain.gateway.CustomerGateway;
import com.burakpozut.notification_service.domain.gateway.OrderGateway;
import com.burakpozut.notification_service.infra.elasticsearch.NotificationDocument;
import com.burakpozut.notification_service.infra.elasticsearch.NotificationElasticsearchRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateNotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationElasticsearchRepository elasticsearchRepository;

    private final CustomerGateway customerGateway;
    private final OrderGateway orderGateway;

    public Notification handle(CreateNotificationCommand command) {
        log.info("notification.create.start orderId={} customerId={} type={} channel={} status={}",
                command.orderId(), command.customerId(), command.type(), command.channel(), command.status());

        try {
            UUID orderCustomerId = orderGateway.getOrderCustomerId(command.orderId());
            log.debug("notification.create.order_validated orderId={} orderCustomerId={} commandCustomerId={}",
                    command.orderId(), orderCustomerId, command.customerId());

            if (!orderCustomerId.equals(command.customerId())) {
                log.warn("notification.create.customer_mismatch orderId={} orderCustomerId={} commandCustomerId={}",
                        command.orderId(), orderCustomerId, command.customerId());
                throw new DomainValidationException("Order "
                        + command.orderId() + " does not belong to the customer "
                        + command.customerId());
            }
            validateCustomerExists(orderCustomerId);
        } catch (ExternalServiceNotFoundException e) {
            log.error("notification.create.order_not_found orderId={} message={}",
                    command.orderId(), e.getMessage());
            throw new OrderNotFoundException(command.orderId());
        } catch (ExternalServiceException e) {
            log.error("notification.create.external_service_error orderId={} message={}",
                    command.orderId(), e.getMessage(), e);
            throw e;
        }

        var notification = Notification.of(command.customerId(), command.orderId(),
                command.type(), command.channel(), command.status());
        var saved = notificationRepository.save(notification);
        log.info("notification.create.persisted notificationId={} orderId={} customerId={}",
                saved.id(), saved.orderId(), saved.customerId());

        NotificationDocument document = NotificationDocument.from(notification);
        elasticsearchRepository.save(document);
        log.debug("notification.create.indexed notificationId={} orderId={}",
                saved.id(), saved.orderId());

        return saved;
    }

    private void validateCustomerExists(UUID customerId) {
        log.debug("notification.create.validating_customer customerId={}", customerId);
        try {
            customerGateway.validateCustomerExists(customerId);
        } catch (ExternalServiceNotFoundException e) {
            log.error("notification.create.customer_not_found customerId={} message={}",
                    customerId, e.getMessage());
            throw new CustomerNotFoundException(customerId);
        }
    }
}

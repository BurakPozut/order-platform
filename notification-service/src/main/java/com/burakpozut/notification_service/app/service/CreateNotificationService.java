package com.burakpozut.notification_service.app.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

import com.burakpozut.common.exception.DomainValidationException;
import com.burakpozut.common.exception.ExternalServiceException;
import com.burakpozut.common.exception.ExternalServiceNotFoundException;
import com.burakpozut.notification_service.app.command.CreateNotificationCommand;
import com.burakpozut.notification_service.app.exception.OrderNotFoundException;
import com.burakpozut.notification_service.domain.Notification;
import com.burakpozut.notification_service.domain.NotificationRepository;
import com.burakpozut.notification_service.domain.gateway.CustomerGateway;
import com.burakpozut.notification_service.domain.gateway.OrderGateway;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateNotificationService {
  private final NotificationRepository notificationRepository;
  private final CustomerGateway customerGateway;
  private final OrderGateway orderGateway;

  public Notification handle(CreateNotificationCommand command) {

    try {
      UUID orderCustomerId = orderGateway.getOrderCustomerId(command.orderId());
      if (!orderCustomerId.equals(command.customerId())) {
        throw new DomainValidationException("Order "
            + command.orderId() + " does not belong to the customer "
            + command.customerId());
      }
      if (!customerGateway.validateCustomerExists(command.customerId())) {
        throw new DomainValidationException("Customer "
            + command.customerId() + " does not exists");
      }
    } catch (ExternalServiceNotFoundException e) {
      throw new OrderNotFoundException(command.orderId());
    } catch (ExternalServiceException e) {
      throw e;
    }

    var notification = Notification.of(command.customerId(), command.orderId(),
        command.type(), command.channel(), command.status());
    var saved = notificationRepository.save(notification);
    return saved;
  }

}

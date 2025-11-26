package com.burakpozut.microservices.order_platform.notification.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.microservices.order_platform.customer.application.exception.CustomerNotFoundException;
import com.burakpozut.microservices.order_platform.notification.application.command.CreateNotificationCommand;
import com.burakpozut.microservices.order_platform.notification.domain.Notification;
import com.burakpozut.microservices.order_platform.notification.domain.NotificationRepository;
import com.burakpozut.microservices.order_platform.notification.domain.port.CustomerGateway;
import com.burakpozut.microservices.order_platform.notification.domain.port.OrderGateway;
import com.burakpozut.microservices.order_platform.order.application.exception.OrderNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateNotificationService {
  private final NotificationRepository notificationRepository;
  private final CustomerGateway customerGateway;
  private final OrderGateway orderGateway;

  @Transactional
  public Notification handle(CreateNotificationCommand command) {
    if (!customerGateway.customerExists(command.customerId()))
      throw new CustomerNotFoundException(command.customerId());
    if (!orderGateway.orderExists(command.orderId()))
      throw new OrderNotFoundException(command.orderId());

    var notification = Notification.createNew(command.customerId(), command.orderId(), command.type(),
        command.channel(), command.status());
    return notificationRepository.save(notification);
  }

}

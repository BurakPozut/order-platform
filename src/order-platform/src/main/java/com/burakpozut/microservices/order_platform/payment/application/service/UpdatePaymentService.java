package com.burakpozut.microservices.order_platform.payment.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.microservices.order_platform.notification.domain.NotificationType;
import com.burakpozut.microservices.order_platform.order.application.exception.OrderNotFoundException;
import com.burakpozut.microservices.order_platform.payment.application.command.CreatePaymentCommand;
import com.burakpozut.microservices.order_platform.payment.application.exception.PaymentNotFoundWithOrderId;
import com.burakpozut.microservices.order_platform.payment.domain.Payment;
import com.burakpozut.microservices.order_platform.payment.domain.PaymentRepository;
import com.burakpozut.microservices.order_platform.payment.domain.port.NotificationGateway;
import com.burakpozut.microservices.order_platform.payment.domain.port.OrderGateway;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdatePaymentService {
  private final PaymentRepository paymentRepository;
  private final OrderGateway orderGateway;
  private final NotificationGateway notificationGateway;

  @Transactional
  public Payment handle(CreatePaymentCommand command) {
    var orderDetails = orderGateway.getOrderDetails(command.orderId())
        .orElseThrow(() -> new OrderNotFoundException(command.orderId()));

    var existingPayment = paymentRepository.findByOrderId(command.orderId())
        .orElseThrow(() -> new PaymentNotFoundWithOrderId(command.orderId()));

    var updatedPayment = Payment.rehydrate(existingPayment.getId(), existingPayment.getOrderId(),
        existingPayment.getAmount(), command.status(), command.provider(), command.provideRef());

    var paymentSaved = paymentRepository.save(updatedPayment);

    NotificationType notificationType = switch (command.status()) {
      case COMPLETED -> NotificationType.PAYMENT_CONFIRMED;
      case FAILED -> NotificationType.PAYMENT_DUE;
      case CANCELLED -> NotificationType.ORDER_CANCELLATION;
      case PENDING, REFUNDED -> NotificationType.ORDER_CANCELLATION;
      default -> null;
    };

    notificationGateway.createNotificationForPayment(orderDetails.customerId(), command.orderId(),
        notificationType.toString(),
        "email", "PENDING");
    return paymentSaved;
  }

}

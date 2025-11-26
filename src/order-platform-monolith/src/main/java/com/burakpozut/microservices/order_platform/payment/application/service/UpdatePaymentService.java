package com.burakpozut.microservices.order_platform.payment.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.microservices.order_platform.notification.domain.NotificationType;
import com.burakpozut.microservices.order_platform.order.application.exception.OrderNotFoundException;
import com.burakpozut.microservices.order_platform.payment.application.command.CreatePaymentCommand;
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
    var payment = Payment.createNew(command.orderId(), orderDetails.amount(), command.status(), command.provider(),
        command.provideRef());
    var paymentSaved = paymentRepository.save(payment);

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
    // TODO: Get the customer on the order id and create the notification
    // And also add if or swtich statement if the payment is confirmed then proceed
    return paymentSaved;
  }

}

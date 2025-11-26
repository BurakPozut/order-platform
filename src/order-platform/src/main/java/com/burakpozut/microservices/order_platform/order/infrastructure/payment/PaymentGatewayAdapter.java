package com.burakpozut.microservices.order_platform.order.infrastructure.payment;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform.order.domain.port.PaymentGateway;
import com.burakpozut.microservices.order_platform.payment.domain.Payment;
import com.burakpozut.microservices.order_platform.payment.domain.PaymentRepository;
import com.burakpozut.microservices.order_platform.payment.domain.PaymentStatus;

import lombok.RequiredArgsConstructor;

@Repository("orderPaymentGatewayAdapter")
@RequiredArgsConstructor
public class PaymentGatewayAdapter implements PaymentGateway {
  private final PaymentRepository paymentRepository;

  @Override
  public void createPaymentForOrder(UUID orderId, BigDecimal amount, String provider,
      String providerRef) {

    var payment = Payment.createNew(orderId, amount, PaymentStatus.PENDING, provider, providerRef);
    paymentRepository.save(payment);
  }

}

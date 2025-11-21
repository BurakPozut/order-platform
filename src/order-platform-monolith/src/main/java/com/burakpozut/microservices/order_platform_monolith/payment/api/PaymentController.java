package com.burakpozut.microservices.order_platform_monolith.payment.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.microservices.order_platform_monolith.payment.api.dto.CreatePaymentRequest;
import com.burakpozut.microservices.order_platform_monolith.payment.api.dto.PaymentResponse;
import com.burakpozut.microservices.order_platform_monolith.payment.application.command.CreatePaymentCommand;
import com.burakpozut.microservices.order_platform_monolith.payment.application.service.CreatePaymentService;
import com.burakpozut.microservices.order_platform_monolith.payment.application.service.GetAllPaymentsService;
import com.burakpozut.microservices.order_platform_monolith.payment.application.service.GetPaymentByIdService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/payments")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

  private final GetPaymentByIdService getPaymentByIdService;
  private final GetAllPaymentsService getAllPaymentsService;
  private final CreatePaymentService createPaymentService;

  @GetMapping
  public ResponseEntity<List<PaymentResponse>> getAll() {
    var payments = getAllPaymentsService.handle();
    var response = payments.stream().map(PaymentResponse::from).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PaymentResponse> getById(@PathVariable UUID id) {
    var payment = getPaymentByIdService.handle(id);
    return ResponseEntity.ok(PaymentResponse.from(payment));

  }

  @PostMapping
  public ResponseEntity<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request) {
    var command = new CreatePaymentCommand(request.orderId(), request.status(), request.provider(),
        request.providerRef());
    var payment = createPaymentService.handle(command);
    return ResponseEntity.ok(PaymentResponse.from(payment));

  }
}

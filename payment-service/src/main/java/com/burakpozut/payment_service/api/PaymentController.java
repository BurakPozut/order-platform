package com.burakpozut.payment_service.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.payment_service.api.dto.request.CreatePaymentRequest;
import com.burakpozut.payment_service.api.dto.request.PatchPaymentRequest;
import com.burakpozut.payment_service.api.dto.response.PaymentResponse;
import com.burakpozut.payment_service.app.service.CreatePaymentService;
import com.burakpozut.payment_service.app.service.GetAllPaymentsService;
import com.burakpozut.payment_service.app.service.GetPaymentByIdService;
import com.burakpozut.payment_service.app.service.PatchPaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
  private final GetAllPaymentsService getAllPaymentsService;
  private final GetPaymentByIdService getPaymentByIdService;
  private final CreatePaymentService createPaymentService;
  private final PatchPaymentService patchPaymentService;

  @GetMapping
  public ResponseEntity<List<PaymentResponse>> getAll() {
    log.info("api.payment.getAll.start");
    var payments = getAllPaymentsService.handle();
    var body = payments.stream().map(PaymentResponse::from).toList();
    log.info("api.payment.getAll.completed count={}", body.size());
    return ResponseEntity.ok(body);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PaymentResponse> getById(@PathVariable UUID id) {
    log.info("api.payment.getById.start paymentId={}", id);
    var payment = getPaymentByIdService.handle(id);
    var body = PaymentResponse.from(payment);
    log.info("api.payment.getById.completed paymentId={}", id);
    return ResponseEntity.ok(body);
  }

  @PostMapping()
  public ResponseEntity<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request) {
    log.info("api.payment.create.start orderId={} amount={} currency={}",
            request.orderId(), request.amount(), request.currency());
    var command = PaymentMapper.toCommand(request);
    var saved = createPaymentService.handle(command);
    var body = PaymentResponse.from(saved);
    log.info("api.payment.create.completed paymentId={} orderId={}", saved.id(), saved.orderId());
    return ResponseEntity.ok(body);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<PaymentResponse> patch(@PathVariable UUID id,
      @Valid @RequestBody PatchPaymentRequest request) {
    log.info("api.payment.patch.start paymentId={} status={}", id, request.status());
    var command = PaymentMapper.toCommand(request);
    var payment = patchPaymentService.handle(id, command);
    var body = PaymentResponse.from(payment);
    log.info("api.payment.patch.completed paymentId={}", id);
    return ResponseEntity.ok(body);
  }

}

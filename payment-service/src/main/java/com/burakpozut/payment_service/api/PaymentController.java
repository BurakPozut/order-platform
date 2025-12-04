package com.burakpozut.payment_service.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.payment_service.api.dto.request.CreatePaymentRequest;
import com.burakpozut.payment_service.api.dto.response.PaymentResponse;
import com.burakpozut.payment_service.api.mapper.PaymentMapper;
import com.burakpozut.payment_service.app.service.CreatePaymentService;
import com.burakpozut.payment_service.app.service.GetAllPaymentsService;
import com.burakpozut.payment_service.app.service.GetPaymentByIdService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
  private final GetAllPaymentsService getAllPaymentsService;
  private final GetPaymentByIdService getPaymentByIdService;
  private final CreatePaymentService createPaymentService;

  @GetMapping
  public ResponseEntity<List<PaymentResponse>> getAll() {
    var payments = getAllPaymentsService.handle();
    var body = payments.stream().map(PaymentResponse::from).toList();
    return ResponseEntity.ok(body);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PaymentResponse> getById(@PathVariable UUID id) {
    var payemnt = getPaymentByIdService.handle(id);
    var body = PaymentResponse.from(payemnt);
    return ResponseEntity.ok(body);
  }

  @PostMapping()
  public ResponseEntity<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request) {
    var command = PaymentMapper.toCommand(request);
    var saved = createPaymentService.handle(command);
    var body = PaymentResponse.from(saved);
    return ResponseEntity.ok(body);
  }

}

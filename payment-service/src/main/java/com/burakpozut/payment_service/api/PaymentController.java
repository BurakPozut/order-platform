package com.burakpozut.payment_service.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.payment_service.api.dto.response.PaymentResponse;
import com.burakpozut.payment_service.app.service.GetAllPaymentsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
  private final GetAllPaymentsService getAllPaymentsService;

  @GetMapping
  public ResponseEntity<List<PaymentResponse>> getAll() {
    var payments = getAllPaymentsService.handle();
    var body = payments.stream().map(PaymentResponse::from).toList();
    return ResponseEntity.ok(body);
  }

}

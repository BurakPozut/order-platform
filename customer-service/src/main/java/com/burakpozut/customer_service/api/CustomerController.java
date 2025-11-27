package com.burakpozut.customer_service.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.customer_service.api.dto.CustomerResponse;
import com.burakpozut.customer_service.app.service.GetCustomerByIdService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("api/customers")
@Slf4j
@RequiredArgsConstructor
public class CustomerController {
  private final GetCustomerByIdService getCustomerByIdService;

  @GetMapping("/{id}")
  public ResponseEntity<CustomerResponse> getById(@PathVariable UUID id) {
    var customer = getCustomerByIdService.handle(id);
    var response = CustomerResponse.from(customer);
    return ResponseEntity.ok(response);
  }

}

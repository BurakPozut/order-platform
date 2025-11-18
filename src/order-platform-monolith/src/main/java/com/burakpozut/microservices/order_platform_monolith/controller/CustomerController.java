package com.burakpozut.microservices.order_platform_monolith.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.microservices.order_platform_monolith.dto.CreateCustomerRequest;
import com.burakpozut.microservices.order_platform_monolith.entity.Customer;
import com.burakpozut.microservices.order_platform_monolith.service.CustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("api/customers")
public class CustomerController {
  private final CustomerService customerService;

  @GetMapping()
  public ResponseEntity<List<Customer>> getAllCustomers() {
    return ResponseEntity.ok(customerService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Customer> getCustomerById(@PathVariable UUID id) {
    return ResponseEntity.ok(customerService.findById(id));
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<Customer> getCustomerByEmail(@PathVariable String email) {
    return ResponseEntity.ok(customerService.findByEmail(email));
  }

  @PostMapping()
  public ResponseEntity<Customer> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
    return ResponseEntity.ok(customerService.save(request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
    return ResponseEntity.noContent().build();
  }

}

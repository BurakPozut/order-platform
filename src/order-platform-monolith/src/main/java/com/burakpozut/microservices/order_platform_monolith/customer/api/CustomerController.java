package com.burakpozut.microservices.order_platform_monolith.customer.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.microservices.order_platform_monolith.customer.api.dto.CreateCustomerRequest;
import com.burakpozut.microservices.order_platform_monolith.customer.api.dto.CustomerResponse;
import com.burakpozut.microservices.order_platform_monolith.customer.application.query.GetCusotmerDetailsQuery;
import com.burakpozut.microservices.order_platform_monolith.customer.application.service.CreateCustomerService;
import com.burakpozut.microservices.order_platform_monolith.customer.application.service.GetCustomerByEmailService;
import com.burakpozut.microservices.order_platform_monolith.customer.application.service.GetCustomerByIdService;
import com.burakpozut.microservices.order_platform_monolith.customer.domain.Customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("api/customers")
public class CustomerController {

  private final GetCustomerByIdService getCustomerDetailsService;
  private final GetCustomerByEmailService getCustomerByEmailService;
  private final CreateCustomerService createCustomerService;

  @GetMapping("/{id}")
  public ResponseEntity<CustomerResponse> getById(@PathVariable("id")  UUID id) {
    var query = new GetCusotmerDetailsQuery(id);
    Customer customer = getCustomerDetailsService.handle(query);

    CustomerResponse response = new CustomerResponse(id, customer.getFullName(), customer.getEmail());
    return ResponseEntity.ok(response);

  }

  @GetMapping("/email/{email}")
  public ResponseEntity<CustomerResponse> getByEmail(@PathVariable String email) {
    Customer customer = getCustomerByEmailService.handle(email);
    var response = new CustomerResponse(customer.getId(), customer.getFullName(), customer.getEmail());
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest request) {
    var customer = createCustomerService.hande(request.fullName(), request.email());
    var response = new CustomerResponse(customer.getId(), customer.getFullName(), customer.getEmail());
    return ResponseEntity.ok(response);
  }

}

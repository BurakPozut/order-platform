package com.burakpozut.microservices.order_platform_monolith.customer.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.microservices.order_platform_monolith.customer.api.dto.CustomerResponse;
import com.burakpozut.microservices.order_platform_monolith.customer.application.query.GetCusotmerDetailsQuery;
import com.burakpozut.microservices.order_platform_monolith.customer.application.service.GetCustomerDetailsService;
import com.burakpozut.microservices.order_platform_monolith.customer.domain.Customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("api/customers")
public class CustomerController {

  private final GetCustomerDetailsService getCustomerDetailsService;

  @GetMapping("/{id}")
  public ResponseEntity<CustomerResponse> getById(@PathVariable("id") @NonNull UUID id) {
    var query = new GetCusotmerDetailsQuery(id);
    Customer customer = getCustomerDetailsService.handle(query);

    CustomerResponse response = CustomerResponse.builder().id(customer.getId()).fullName(customer.getFullName())
        .build();
    return ResponseEntity.ok(response);

  }

}

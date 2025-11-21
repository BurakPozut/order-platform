package com.burakpozut.microservices.order_platform_monolith.customer.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.microservices.order_platform_monolith.customer.api.dto.CreateCustomerRequest;
import com.burakpozut.microservices.order_platform_monolith.customer.api.dto.CustomerResponse;
import com.burakpozut.microservices.order_platform_monolith.customer.application.GetAllCustomersService;
import com.burakpozut.microservices.order_platform_monolith.customer.application.query.GetCusotmerDetailsQuery;
import com.burakpozut.microservices.order_platform_monolith.customer.application.service.CreateCustomerService;
import com.burakpozut.microservices.order_platform_monolith.customer.application.service.GetCustomerByEmailService;
import com.burakpozut.microservices.order_platform_monolith.customer.application.service.GetCustomerByIdService;
import com.burakpozut.microservices.order_platform_monolith.customer.domain.Customer;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
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

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("api/customers")
public class CustomerController {

  private final GetCustomerByIdService getCustomerDetailsService;
  private final GetCustomerByEmailService getCustomerByEmailService;
  private final CreateCustomerService createCustomerService;
  private final GetAllCustomersService getAllCustomersService;

  @GetMapping
  public ResponseEntity<List<CustomerResponse>> getAll() {
    var customers = getAllCustomersService.handle();
    var response = customers.stream().map(CustomerResponse::from).collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CustomerResponse> getById(
      @Parameter(description = "Customer ID", required = true, schema = @Schema(type = "string", format = "uuid", example = "550e8400-e29b-41d4-a716-446655440000")) @PathVariable("id") UUID id) {
    var query = new GetCusotmerDetailsQuery(id);
    Customer customer = getCustomerDetailsService.handle(query);

    return ResponseEntity.ok(CustomerResponse.from(customer));

  }

  @GetMapping("/email/{email}")
  public ResponseEntity<CustomerResponse> getByEmail(@PathVariable String email) {
    Customer customer = getCustomerByEmailService.handle(email);
    return ResponseEntity.ok(CustomerResponse.from(customer));
  }

  @PostMapping
  public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest request) {
    var customer = createCustomerService.hande(request.fullName(), request.email());
    return ResponseEntity.ok(CustomerResponse.from(customer));
  }

}

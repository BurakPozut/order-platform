package com.burakpozut.customer_service.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.customer_service.api.dto.CreateCustomerRequest;
import com.burakpozut.customer_service.api.dto.CustomerResponse;
import com.burakpozut.customer_service.app.command.CreateCustomerCommand;
import com.burakpozut.customer_service.app.service.CreateCustomerService;
import com.burakpozut.customer_service.app.service.GetAllCustomersService;
import com.burakpozut.customer_service.app.service.GetCustomerByIdService;

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
@RequestMapping("api/customers")
@Slf4j
@RequiredArgsConstructor
public class CustomerController {
  private final GetCustomerByIdService getCustomerByIdService;
  private final GetAllCustomersService getAllCustomersService;
  private final CreateCustomerService createCustomerService;

  @GetMapping
  public ResponseEntity<List<CustomerResponse>> getAll() {
    var customers = getAllCustomersService.handle();
    var body = customers.stream().map(CustomerResponse::from).collect(Collectors.toList());
    return ResponseEntity.ok(body);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CustomerResponse> getById(@PathVariable UUID id) {
    var customer = getCustomerByIdService.handle(id);
    var body = CustomerResponse.from(customer);
    return ResponseEntity.ok(body);
  }

  @PostMapping()
  public ResponseEntity<CustomerResponse> create(@RequestBody @Valid CreateCustomerRequest request) {
    var command = CreateCustomerCommand.of(request.email(), request.fullName());
    var customer = createCustomerService.handle(command);
    var body = CustomerResponse.from(customer);

    return ResponseEntity.ok(body);
  }

}

package com.burakpozut.customer_service.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.customer_service.api.dto.request.CreateCustomerRequest;
import com.burakpozut.customer_service.api.dto.request.PatchCustomerRequest;
import com.burakpozut.customer_service.api.dto.request.UpdateCustomerRequest;
import com.burakpozut.customer_service.api.dto.response.CustomerResponse;
import com.burakpozut.customer_service.app.command.CreateCustomerCommand;
import com.burakpozut.customer_service.app.command.PatchCustomerCommand;
import com.burakpozut.customer_service.app.command.UpdateCustomerCommand;
import com.burakpozut.customer_service.app.service.CreateCustomerService;
import com.burakpozut.customer_service.app.service.GetAllCustomersService;
import com.burakpozut.customer_service.app.service.GetCustomerByIdService;
import com.burakpozut.customer_service.app.service.PatchCustomerService;
import com.burakpozut.customer_service.app.service.UpdateCustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("api/customers")
@Slf4j
@RequiredArgsConstructor
public class CustomerController {
  private final GetCustomerByIdService getCustomerByIdService;
  private final GetAllCustomersService getAllCustomersService;
  private final CreateCustomerService createCustomerService;
  private final UpdateCustomerService updateCustomerService;
  private final PatchCustomerService patchCustomerService;

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

  @PutMapping("/{id}")
  public ResponseEntity<CustomerResponse> update(@PathVariable UUID id,
      @Valid @RequestBody UpdateCustomerRequest request) {
    var command = UpdateCustomerCommand.of(request.fullName(), request.email());
    var customer = updateCustomerService.handle(id, command);

    return ResponseEntity.ok(CustomerResponse.from(customer));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<CustomerResponse> patch(@PathVariable UUID id,
      @Valid @RequestBody PatchCustomerRequest request) {
    var command = PatchCustomerCommand.of(request.fullName(), request.email());
    var customer = patchCustomerService.handle(id, command);
    return ResponseEntity.ok(CustomerResponse.from(customer));
  }
}

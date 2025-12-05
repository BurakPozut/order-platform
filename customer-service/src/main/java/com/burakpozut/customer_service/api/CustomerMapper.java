package com.burakpozut.customer_service.api;

import com.burakpozut.customer_service.api.dto.request.CreateCustomerRequest;
import com.burakpozut.customer_service.api.dto.request.PatchCustomerRequest;
import com.burakpozut.customer_service.api.dto.request.UpdateCustomerRequest;
import com.burakpozut.customer_service.app.command.CreateCustomerCommand;
import com.burakpozut.customer_service.app.command.PatchCustomerCommand;
import com.burakpozut.customer_service.app.command.UpdateCustomerCommand;

public class CustomerMapper {

  public static CreateCustomerCommand toCommand(CreateCustomerRequest request) {
    return CreateCustomerCommand.of(
        request.email(),
        request.fullName());
  }

  public static UpdateCustomerCommand toCommand(UpdateCustomerRequest request) {
    return UpdateCustomerCommand.of(request.fullName(),
        request.email());
  }

  public static PatchCustomerCommand toCommand(PatchCustomerRequest request) {
    return PatchCustomerCommand.of(request.fullName(),
        request.email());
  }
}

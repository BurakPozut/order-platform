package com.burakpozut.customer_service.app.command;

public record CreateCustomerCommand(
    String email,
    String fullName) {
  public static CreateCustomerCommand of(String email, String fullName) {
    return new CreateCustomerCommand(email, fullName);
  }

}

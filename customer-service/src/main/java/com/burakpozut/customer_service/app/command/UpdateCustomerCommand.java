package com.burakpozut.customer_service.app.command;

public record UpdateCustomerCommand(
    String fullName,
    String email) {
  public static UpdateCustomerCommand of(String fullName, String email) {
    return new UpdateCustomerCommand(fullName, email);
  }

}

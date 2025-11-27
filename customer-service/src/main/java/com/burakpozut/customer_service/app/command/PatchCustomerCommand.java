package com.burakpozut.customer_service.app.command;

public record PatchCustomerCommand(
    String fullName,
    String email) {
  public static PatchCustomerCommand of(String fullName, String email) {
    return new PatchCustomerCommand(fullName, email);
  }

}

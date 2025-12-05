package com.burakpozut.product_service.api;

import com.burakpozut.product_service.api.dto.request.PatchProductRequest;
import com.burakpozut.product_service.api.dto.request.UpdateProductRequest;
import com.burakpozut.product_service.app.command.PatchProductCommand;
import com.burakpozut.product_service.app.command.UpdateProductCommand;

public class ProductMapper {
  public static PatchProductCommand toCommand(PatchProductRequest request) {
    return PatchProductCommand.of(request.name(),
        request.price(), request.currency(),
        request.status());
  }

  public static UpdateProductCommand toCommand(UpdateProductRequest request) {
    return UpdateProductCommand.of(request.name(),
        request.price(),
        request.currency(),
        request.status());
  }

}

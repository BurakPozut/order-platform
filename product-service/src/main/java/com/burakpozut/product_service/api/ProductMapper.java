package com.burakpozut.product_service.api;

import java.util.UUID;

import com.burakpozut.product_service.api.dto.request.PatchProductRequest;
import com.burakpozut.product_service.api.dto.request.ReserveInventoryRequest;
import com.burakpozut.product_service.api.dto.request.UpdateProductRequest;
import com.burakpozut.product_service.app.command.PatchProductCommand;
import com.burakpozut.product_service.app.command.ReserveInventoryCommand;
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

  public static ReserveInventoryCommand toCommand(UUID productId, ReserveInventoryRequest request) {
    return ReserveInventoryCommand.of(productId, request.quantity());
  }

}

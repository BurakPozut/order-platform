package com.burakpozut.order_service.domain.gateway;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.burakpozut.order_service.domain.ProductInfo;

public interface ProductGateway {

  /**
   * Fetches product information by Id
   * 
   * @param prouctId The product Id
   * @return Optional containing ProductInfo if found, empty otherwise
   */
  Optional<ProductInfo> getProductById(UUID productId);

  /**
   * Fetches multiple products by their Ids in parallel
   * 
   * @param productIds
   * @return
   */
  Map<UUID, ProductInfo> getProductsByIds(List<UUID> productIds);
}

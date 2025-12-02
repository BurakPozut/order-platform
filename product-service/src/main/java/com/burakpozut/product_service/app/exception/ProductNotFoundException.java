package com.burakpozut.product_service.app.exception;

import java.util.UUID;

import com.burakpozut.common.exception.NotFoundException;

public class ProductNotFoundException extends NotFoundException{
  
  public ProductNotFoundException(UUID id){
    super("Product not found with id: "+id);
  }
  public ProductNotFoundException(String name){
    super("Product not found with name: "+name);
  }

}

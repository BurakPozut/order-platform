package com.burakpozut.product_service.app.exception;

import com.burakpozut.common.exception.BusinessException;

public class NameAlreadyInUseException extends BusinessException{
  
  public NameAlreadyInUseException(String name){
    super("name is already in use : "+name);
  }
}

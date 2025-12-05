package com.burakpozut.common.exception;

public class ExternalServiceException extends AppException {
  public ExternalServiceException(String message) {
    super(message);
  }

  public ExternalServiceException(String message, Throwable cause) {
    super(message, cause);
  }

}

package com.scalar.clocker.exception;

public class ClockerCliException extends RuntimeException {

  public ClockerCliException(String message) {
    super(message);
  }

  public ClockerCliException(String message, Throwable cause) {
    super(message, cause);
  }


}

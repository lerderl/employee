package com.example.employee.exception;

public class InvalidFileFormatException extends RuntimeException {
  public InvalidFileFormatException(String message) {
    super(message);
  }
}

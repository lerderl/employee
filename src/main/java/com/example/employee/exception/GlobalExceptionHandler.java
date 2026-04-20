package com.example.employee.exception;

import org.springframework.http.HttpStatus;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;
import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {
//  Validation errors from @valid body
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleValidationErrors(MethodArgumentNotValidException ex) {
    Map<String, String> fieldErrors = new HashMap<>();

    ex.getBindingResult().getFieldErrors().forEach(error ->
        fieldErrors.put(error.getField(), error.getDefaultMessage())
    );

    Map<String, Object> response = new HashMap<>();
    response.put("status", 400);
    response.put("errors", fieldErrors);

    return response;
  }

  // Validation errors from @Validated (service layer)
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleConstraintViolation(ConstraintViolationException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("status", 400);
    response.put("message", ex.getMessage());
    return response;
  }

  @ExceptionHandler(EmployeeNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, Object> handleNotFound(EmployeeNotFoundException ex) {
    return Map.of("status", 404, "message", ex.getMessage());
  }

  @ExceptionHandler(DuplicateEmailException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public Map<String, Object> handleDuplicate(DuplicateEmailException ex) {
    return Map.of("status", 409, "message", ex.getMessage());
  }

  @ExceptionHandler(InvalidFileFormatException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleInvalidFile(InvalidFileFormatException ex) {
    return Map.of("status", 400, "message", ex.getMessage());
  }

  @ExceptionHandler(ExcelProcessingException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, Object> handleExcel(ExcelProcessingException ex) {
    return Map.of("status", 500, "message", ex.getMessage());
  }

  // Fallback
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, Object> handleGeneral(Exception ex) {
    return Map.of("status", 500, "message", ex.getMessage());
  }
}

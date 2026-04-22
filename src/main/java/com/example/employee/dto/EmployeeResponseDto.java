package com.example.employee.dto;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeResponseDto {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String department;
  private BigDecimal salary;
  private LocalDate dateOfJoining;
  private Boolean active;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}

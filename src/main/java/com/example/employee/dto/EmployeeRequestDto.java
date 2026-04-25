package com.example.employee.dto;

import lombok.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeRequestDto {
  @NotBlank
  @Size(max = 50)
  private String firstName;

  @NotBlank
  @Size(max = 50)
  private String lastName;

  @NotBlank
  @Email
  private String email;

  @NotBlank
  @Size(max = 100)
  private String department;

  @NotNull
  @DecimalMin("0.00")
  private BigDecimal salary;

  @NotNull
  @PastOrPresent
  private LocalDate dateOfJoining;

  @NotNull
  private Boolean active;
}

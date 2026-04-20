package com.example.employee.entity;

import lombok.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;


@Entity
@Table(name = "employees")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotBlank
  @Size(max = 50)
  private String firstName;

  @NotBlank
  @Size(max = 50)
  private String lastName;

  @NotBlank
  @Email
  @Column(unique = true)
  private String email;

  @NotBlank
  @Size(max = 100)
  private String department;

  @NotNull
  @DecimalMin(value = "0.00")
  private BigDecimal salary;

  @NotNull
  @PastOrPresent
  private LocalDate dateOfJoining;

  @NotNull
  private Boolean active = true;

  @Column(updatable = false)
  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}

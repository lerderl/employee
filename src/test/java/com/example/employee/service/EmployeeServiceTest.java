package com.example.employee.service;

import com.example.employee.entity.Employee;
import com.example.employee.repository.EmployeeRepository;
import com.example.employee.exception.DuplicateEmailException;
import com.example.employee.service.implementation.EmployeeServiceImplementation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeServiceTest {

  @Mock
  private EmployeeRepository employeeRepository;

  @InjectMocks
  private EmployeeServiceImplementation employeeService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldRejectDuplicateEmail() {

    Employee existing = new Employee();
    existing.setId(1L);
    existing.setEmail("test@mail.com");

    Mockito.when(employeeRepository.findByEmail("test@mail.com"))
        .thenReturn(Optional.of(existing));

    Employee e = new Employee();
    e.setEmail("test@mail.com");

    assertThrows(DuplicateEmailException.class, () ->
        employeeService.createEmployee(e));
  }

  @Test
  void shouldRejectInvalidSalary() {

    Employee e = new Employee();
    e.setDepartment("IT");
    e.setSalary(new BigDecimal("10000"));

    assertThrows(IllegalArgumentException.class, () ->
        employeeService.createEmployee(e));
  }

  @Test
  void shouldSoftDelete() {

    Employee e = new Employee();
    e.setId(1L);
    e.setActive(true);

    Mockito.when(employeeRepository.findById(1L))
        .thenReturn(Optional.of(e));

    employeeService.softDeleteEmployee(1L);

    assertFalse(e.getActive());
  }
}
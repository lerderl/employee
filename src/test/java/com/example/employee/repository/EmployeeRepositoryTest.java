package com.example.employee.repository;

import com.example.employee.entity.Employee;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EmployeeRepositoryTest {
  @Autowired
  private EmployeeRepository employeeRepository;

  private Employee buildValidEmployee() {
    Employee e = new Employee();
    e.setFirstName("John");
    e.setLastName("Doe");
    e.setEmail("repo@test.com");
    e.setDepartment("IT");
    e.setSalary(new BigDecimal("50000"));
    e.setDateOfJoining(LocalDate.now());
    e.setActive(true);
    return e;
  }

  @Test
  void shouldFindByEmail() {

    Employee e = buildValidEmployee();

    employeeRepository.save(e);

    assertThat(employeeRepository.findByEmail("repo@test.com")).isPresent();
  }

  @Test
  void shouldFindByDepartment() {
    Employee e = buildValidEmployee();

    employeeRepository.save(e);

    assertThat(employeeRepository.findByDepartment("IT")).isNotEmpty();
  }
}

package com.example.employee.service;

import jakarta.validation.Valid;
import com.example.employee.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.employee.dto.EmployeeRequestDto;

import java.util.List;
import java.math.BigDecimal;

public interface EmployeeService {
  Employee createEmployee(@Valid Employee employee);
  Employee updateEmployee(Long id, @Valid Employee employee);
  Employee patchEmployee(Long id, EmployeeRequestDto dto);
  Employee getEmployeeById(Long id);
  Page<Employee> getAllEmployees(Pageable pageable, String department, Boolean active);
  void softDeleteEmployee(Long id);
  void hardDeleteIfInactive(Long id);
  List<Employee> getEmployeesByDepartment(String department);
  List<Employee> getActiveEmployees();
  List<Employee> getEmployeesBySalaryRange(BigDecimal min, BigDecimal max);
}

package com.example.employee.service;

import jakarta.validation.Valid;
import com.example.employee.entity.Employee;

import java.util.List;
import java.math.BigDecimal;

public interface EmployeeService {
  Employee createEmployee(@Valid Employee employee);
  Employee updateEmployee(Long id, @Valid Employee employee);
  Employee getEmployeeById(Long id);
  List<Employee> getAllEmployees();
  void softDeleteEmployee(Long id);
  void purgeInactiveEmployees();
  List<Employee> getEmployeesByDepartment(String department);
  List<Employee> getActiveEmployees();
  List<Employee> getEmployeeBySalaryRange(BigDecimal min, BigDecimal max);
}

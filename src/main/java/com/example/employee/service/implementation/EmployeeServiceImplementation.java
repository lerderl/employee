package com.example.employee.service.implementation;

import com.example.employee.entity.Employee;
import com.example.employee.exception.DuplicateEmailException;
import com.example.employee.exception.EmployeeNotFoundException;
import com.example.employee.repository.EmployeeRepository;
import com.example.employee.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class EmployeeServiceImplementation implements EmployeeService {
  private final EmployeeRepository employeeRepository;

//  Duplicate email check
  private void validateDuplicateEmail(String email, Long currentId) {
    employeeRepository.findByEmail(email).ifPresent(employee -> {
      if (currentId == null || !employee.getId().equals(currentId)) {
        throw new DuplicateEmailException(email);
      }
    });
  }

//  Salary validation check
  private void validateSalary(String department, BigDecimal salary) {
    if  (department == null || salary == null) return;

    if (department.equalsIgnoreCase("Intern")) {
      if (salary.compareTo(new BigDecimal("15000")) < 0) {
        throw new IllegalArgumentException("Intern Salary cannot be lower than 15000");
      }
    } else {
      if (salary.compareTo(new BigDecimal("30000")) < 0) {
        throw new IllegalArgumentException("Salary cannot be lower than 30000");
      }
    }
  }

//  CREATE
  @Override
  public Employee createEmployee(@Valid Employee employee) {
    validateDuplicateEmail(employee.getEmail(), null);
    validateSalary(employee.getDepartment(), employee.getSalary());
    return employeeRepository.save(employee);
  }

//  READ
  @Override
  public Employee getEmployeeById(Long id) {
    return employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
  }

  @Override
  public List<Employee> getAllEmployees() {
    return employeeRepository.findAll();
  }

//  UPDATE
  @Override
  public Employee updateEmployee(Long id, @Valid Employee employee) {
    Employee existingEmployee = getEmployeeById(id);

    validateDuplicateEmail(employee.getEmail(), id);
    validateSalary(employee.getDepartment(), employee.getSalary());

    existingEmployee.setFirstName(employee.getFirstName());
    existingEmployee.setLastName(employee.getLastName());
    existingEmployee.setEmail(employee.getEmail());
    existingEmployee.setDepartment(employee.getDepartment());
    existingEmployee.setSalary(employee.getSalary());
    existingEmployee.setDateOfJoining(employee.getDateOfJoining());
    existingEmployee.setActive(employee.getActive());

    return employeeRepository.save(existingEmployee);
  }

//  SOFT DELETE
  @Override
  public void softDeleteEmployee(Long id) {
    Employee employee = getEmployeeById(id);
    employee.setActive(false);
    employeeRepository.save(employee);
  }

//  HARD DELETE (PURGE)
  @Override
  public void purgeInactiveEmployees() {
    List<Employee> inactive = employeeRepository.findAll()
        .stream()
        .filter(e -> Boolean.FALSE.equals(e.getActive()))
        .toList();

    employeeRepository.deleteAll(inactive);
  }

//  CUSTOM QUERIES
  @Override
  public List<Employee> getEmployeesByDepartment(String department) {
    return employeeRepository.findByDepartment(department);
  }

  @Override
  public List<Employee> getActiveEmployees() {
    return employeeRepository.findByActiveTrue();
  }

  @Override
  public List<Employee> getEmployeeBySalaryRange(BigDecimal min, BigDecimal max) {
    return employeeRepository.findBySalaryRange(min, max);
  }
}

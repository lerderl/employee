package com.example.employee.service.implementation;

import com.example.employee.dto.EmployeeRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.*;
import com.example.employee.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import com.example.employee.service.EmployeeService;
import com.example.employee.repository.EmployeeRepository;
import org.springframework.validation.annotation.Validated;
import com.example.employee.exception.DuplicateEmailException;
import com.example.employee.exception.EmployeeNotFoundException;

import java.util.List;
import java.math.BigDecimal;

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
//  public List<Employee> getAllEmployees() {
//    return employeeRepository.findAll();
//  }
  public Page<Employee> getAllEmployees(Pageable pageable, String department, Boolean active) {
    Page<Employee> page = employeeRepository.findAll(pageable);

    return (Page<Employee>) page.map(e -> e)
        .filter(e -> department == null || e.getDepartment().equalsIgnoreCase(department))
        .filter(e -> active == null || e.getActive().equals(active));
  }

//  FULL UPDATE
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

//  PARTIAL UPDATE
  @Override
  public Employee patchEmployee(Long id, EmployeeRequestDto dto) {
    Employee existing = getEmployeeById(id);

    if (dto.getSalary() != null) {
      validateSalary(existing.getDepartment(), dto.getSalary());
      existing.setSalary(dto.getSalary());
    }

    if (dto.getDepartment() != null) {
      validateSalary(dto.getDepartment(), existing.getSalary());
      existing.setDepartment(dto.getDepartment());
    }

    if (dto.getActive() != null) {
      existing.setActive(dto.getActive());
    }

    return employeeRepository.save(existing);
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
//  public void purgeInactiveEmployees() {
//    List<Employee> inactive = employeeRepository.findAll()
//        .stream()
//        .filter(e -> Boolean.FALSE.equals(e.getActive()))
//        .toList();
//
//    employeeRepository.deleteAll(inactive);
//  }
  public void hardDeleteIfInactive(Long id) {
    Employee employee = getEmployeeById(id);

    if (Boolean.TRUE.equals(employee.getActive())) {
      throw new IllegalStateException("Cannot hard delete active employee");
    }

    employeeRepository.delete(employee);
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
  public List<Employee> getEmployeesBySalaryRange(BigDecimal min, BigDecimal max) {
    return employeeRepository.findBySalaryRange(min, max);
  }
}

package com.example.employee.service;

import jakarta.validation.Valid;
import com.example.employee.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.employee.dto.EmployeeRequestDto;

import java.util.List;
import java.math.BigDecimal;

/**
 * Core service interface for managing Employee operations.
 * <p>
 * This layer encapsulates all business rules including:
 * <ul>
 *     <li>Email uniqueness validation</li>
 *     <li>Salary constraints based on department</li>
 *     <li>Soft delete (active flag) and conditional hard delete</li>
 *     <li>Data retrieval with filtering and pagination</li>
 * </ul>
 */
public interface EmployeeService {

  /**
   * Creates a new employee after applying validation and business rules.
   *
   * @param employee the employee entity to be created (must pass validation)
   * @return the persisted employee entity
   *
   * @throws com.example.employee.exception.DuplicateEmailException
   *         if the email already exists for another employee
   *
   * @throws IllegalArgumentException
   *         if salary constraints are violated
   */
  Employee createEmployee(@Valid Employee employee);

  /**
   * Updates an existing employee with full replacement of fields.
   *
   * @param id the ID of the employee to update
   * @param employee the updated employee data (must pass validation)
   * @return the updated employee entity
   *
   * @throws com.example.employee.exception.EmployeeNotFoundException
   *         if no employee exists with the given ID
   *
   * @throws com.example.employee.exception.DuplicateEmailException
   *         if the email conflicts with another employee
   */
  Employee updateEmployee(Long id, @Valid Employee employee);

  /**
   * Partially updates an employee's mutable fields.
   * <p>
   * Only salary, department, and active status can be modified.
   *
   * @param id the ID of the employee to update
   * @param dto the DTO containing fields to update
   * @return the updated employee entity
   *
   * @throws com.example.employee.exception.EmployeeNotFoundException
   *         if no employee exists with the given ID
   */
  Employee patchEmployee(Long id, EmployeeRequestDto dto);

  /**
   * Retrieves an employee by their unique ID.
   *
   * @param id the employee ID
   * @return the found employee
   *
   * @throws com.example.employee.exception.EmployeeNotFoundException
   *         if no employee exists with the given ID
   */
  Employee getEmployeeById(Long id);

  /**
   * Retrieves a paginated list of employees with optional filtering.
   *
   * @param pageable pagination and sorting configuration
   * @param department optional department filter
   * @param active optional active status filter
   * @return a paginated list of employees matching the criteria
   */
  Page<Employee> getAllEmployees(Pageable pageable, String department, Boolean active);

  /**
   * Performs a soft delete by marking an employee as inactive.
   *
   * @param id the employee ID
   *
   * @throws com.example.employee.exception.EmployeeNotFoundException
   *         if no employee exists with the given ID
   */
  void softDeleteEmployee(Long id);

  /**
   * Permanently deletes an employee if they are already inactive.
   *
   * @param id the employee ID
   *
   * @throws com.example.employee.exception.EmployeeNotFoundException
   *         if no employee exists with the given ID
   *
   * @throws IllegalStateException
   *         if the employee is still active
   */
  void hardDeleteIfInactive(Long id);

  /**
   * Retrieves all employees belonging to a specific department.
   *
   * @param department the department name
   * @return list of employees in the given department
   */
  List<Employee> getEmployeesByDepartment(String department);

  /**
   * Retrieves all active employees.
   *
   * @return list of active employees
   */
  List<Employee> getActiveEmployees();

  /**
   * Retrieves employees whose salaries fall within the specified range.
   *
   * @param min minimum salary (inclusive)
   * @param max maximum salary (inclusive)
   * @return list of employees within the salary range
   */
  List<Employee> getEmployeesBySalaryRange(BigDecimal min, BigDecimal max);
}
package com.example.employee.repository;

import com.example.employee.entity.Employee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
//  Find Employees by department
  List<Employee> findByDepartment(String department);

//  Find Employee by email
  Optional<Employee> findByEmail(String email);

//  Find all active employees
  List<Employee> findByActiveTrue();

//  Find employees within salary range
  @Query("SELECT e FROM Employee e WHERE e.salary BETWEEN :min AND :max")
  List<Employee> findBySalaryRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);
}

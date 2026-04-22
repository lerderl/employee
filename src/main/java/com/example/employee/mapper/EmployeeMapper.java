package com.example.employee.mapper;

import com.example.employee.entity.Employee;
import com.example.employee.dto.EmployeeRequestDto;
import com.example.employee.dto.EmployeeResponseDto;

public class EmployeeMapper {
  public static Employee toEntity(EmployeeRequestDto dto) {
    return Employee.builder()
        .firstName(dto.getFirstName())
        .lastName(dto.getLastName())
        .email(dto.getEmail())
        .department(dto.getDepartment())
        .salary(dto.getSalary())
        .dateOfJoining(dto.getDateOfJoining())
        .active(dto.getActive())
        .build();
  }

  public static EmployeeResponseDto toDto(Employee e) {
    return EmployeeResponseDto.builder()
        .id(e.getId())
        .firstName(e.getFirstName())
        .lastName(e.getLastName())
        .email(e.getEmail())
        .department(e.getDepartment())
        .salary(e.getSalary())
        .dateOfJoining(e.getDateOfJoining())
        .active(e.getActive())
        .createdAt(e.getCreatedAt())
        .updatedAt(e.getUpdatedAt())
        .build();
  }
}

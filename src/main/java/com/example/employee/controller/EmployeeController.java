package com.example.employee.controller;

import com.example.employee.service.*;
import com.example.employee.entity.Employee;
import com.example.employee.dto.ImportResultDto;
import com.example.employee.mapper.EmployeeMapper;
import com.example.employee.dto.EmployeeRequestDto;
import com.example.employee.dto.EmployeeResponseDto;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.math.BigDecimal;

@Validated
@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {
  private final EmailService emailService;
  private final EmployeeService employeeService;
  private final EmployeePdfService employeePdfService;
  private final EmployeeImportService employeeImportService;
  private final EmployeeExportService employeeExportService;

//  Create endpoint
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public EmployeeResponseDto create(@Valid @RequestBody EmployeeRequestDto dto) {
    Employee employee = employeeService.createEmployee(EmployeeMapper.toEntity(dto));
    return EmployeeMapper.toDto(employee);
  }

//  Pagination
  @GetMapping
  public Page<EmployeeResponseDto> getAll(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "id,asc") String[] sort,
    @RequestParam(required = false) String department,
    @RequestParam(required = false) Boolean active
  ) {
    Sort.Direction direction = sort[1].equalsIgnoreCase("desc")
        ? Sort.Direction.DESC : Sort.Direction.ASC;

    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

    Page<Employee> employees = employeeService.getAllEmployees(pageable, department, active);

    return employees.map(EmployeeMapper::toDto);
  }

//  Get by ID
  @GetMapping("/{id}")
  public EmployeeResponseDto getById(@PathVariable Long id) {
    return EmployeeMapper.toDto(employeeService.getEmployeeById(id));
  }

//  Full update
  @PutMapping("/{id}")
  public EmployeeResponseDto update(
      @PathVariable Long id,
      @Valid @RequestBody EmployeeRequestDto dto) {
    Employee updated = employeeService.updateEmployee(id, EmployeeMapper.toEntity(dto));
    return EmployeeMapper.toDto(updated);
  }

//  Partial update
  @PatchMapping("/{id}")
  public EmployeeResponseDto patch(
      @PathVariable Long id,
      @RequestBody EmployeeRequestDto dto) {
    Employee updated = employeeService.patchEmployee(id, dto);
    return EmployeeMapper.toDto(updated);
  }

//  Soft delete
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void softDelete(@PathVariable Long id) {
    employeeService.softDeleteEmployee(id);
  }

//  Hard delete
  @DeleteMapping("/{id}/hard")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void hardDelete(@PathVariable Long id) {
    employeeService.hardDeleteIfInactive(id);
  }

//  salary range
  @GetMapping("/salary-range")
  public List<EmployeeResponseDto> salaryRange(
      @RequestParam BigDecimal min,
      @RequestParam BigDecimal max) {
    return employeeService.getEmployeesBySalaryRange(min, max)
        .stream()
        .map(EmployeeMapper::toDto)
        .toList();
  }

//  Excel import
  @PostMapping(value = "/import", consumes = "multipart/form-data")
  public ImportResultDto importEmployees(@RequestParam("file") MultipartFile file) {
    return employeeImportService.importEmployees(file);
  }

//  Excel export
  @GetMapping("/export/excel")
  public void exportEmployeesToExcel(
      @RequestParam(required = false) String department,
      @RequestParam(required = false) Boolean active,
      HttpServletResponse response) {
    employeeExportService.exportToExcel(department, active, response);
  }

//  Pdf report generation
  @GetMapping("/export/pdf")
  public void exportEmployeesToPdf(HttpServletResponse response) {
    employeePdfService.exportToPdf(response);
  }

//  Send files to mail
  @PostMapping("/export/email")
  public ResponseEntity<String> sendReportsByEmail(
      @NotBlank(message = "Email is required")
      @Email(message = "Invalid email format")
      @RequestParam String email,
      @RequestParam(required = false) String department,
      @RequestParam(required = false) Boolean active) {

    byte[] excel = employeeExportService.sendExcelToMail(department, active);
    byte[] pdf = employeePdfService.sendPdfToMail();

    emailService.sendEmployeeReport(email, excel, pdf);

    return ResponseEntity.ok("Email sent successfully");
  }
}

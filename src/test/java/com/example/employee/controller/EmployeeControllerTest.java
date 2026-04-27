package com.example.employee.controller;

import com.example.employee.dto.*;
import com.example.employee.service.*;
import com.example.employee.entity.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

import org.mockito.Mockito;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.time.LocalDate;
import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest
public class EmployeeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean  private EmployeeService employeeService;
  @MockitoBean private EmployeeImportService importService;
  @MockitoBean private EmployeeExportService exportService;
  @MockitoBean private EmployeePdfService pdfService;

//  @Autowired
  private final ObjectMapper objectMapper = new ObjectMapper()
    .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

  // ---------------- CREATE ----------------
  @Test
  void createEmployee_shouldReturn201() throws Exception {

    Employee emp = buildEmployee();

    Mockito.when(employeeService.createEmployee(Mockito.any()))
        .thenReturn(emp);

    mockMvc.perform(post("/api/v1/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(emp)))
        .andExpect(status().isCreated());
  }

  // ---------------- GET ALL ----------------
  @Test
  void getAllEmployees_shouldReturnPage() throws Exception {

    Page<Employee> page = new PageImpl<>(List.of(buildEmployee()));

    Mockito.when(employeeService.getAllEmployees(Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(page);

    mockMvc.perform(get("/api/v1/employees"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").exists());
  }

  // ---------------- GET BY ID ----------------
  @Test
  void getEmployeeById_shouldReturnEmployee() throws Exception {

    Mockito.when(employeeService.getEmployeeById(1L))
        .thenReturn(buildEmployee());

    mockMvc.perform(get("/api/v1/employees/1"))
        .andExpect(status().isOk());
  }

  // ---------------- UPDATE ----------------
  @Test
  void updateEmployee_shouldReturn200() throws Exception {

    Employee emp = buildEmployee();

    Mockito.when(employeeService.updateEmployee(Mockito.eq(1L), Mockito.any()))
        .thenReturn(emp);

    mockMvc.perform(put("/api/v1/employees/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(emp)))
        .andExpect(status().isOk());
  }

  // ---------------- PATCH ----------------
  @Test
  void patchEmployee_shouldReturn200() throws Exception {

    EmployeeRequestDto dto = new EmployeeRequestDto();
    dto.setSalary(new BigDecimal("60000"));

    Mockito.when(employeeService.patchEmployee(Mockito.eq(1L), Mockito.any()))
        .thenReturn(buildEmployee());

    mockMvc.perform(patch("/api/v1/employees/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk());
  }

  // ---------------- SOFT DELETE ----------------
  @Test
  void deleteEmployee_shouldReturn204() throws Exception {

    mockMvc.perform(delete("/api/v1/employees/1"))
        .andExpect(status().isNoContent());
  }

  // ---------------- HARD DELETE ----------------
  @Test
  void hardDelete_shouldReturn204() throws Exception {

    mockMvc.perform(delete("/api/v1/employees/1/hard"))
        .andExpect(status().isNoContent());
  }

  // ---------------- SALARY RANGE ----------------
  @Test
  void getBySalaryRange_shouldReturnList() throws Exception {

    Mockito.when(employeeService.getEmployeesBySalaryRange(Mockito.any(), Mockito.any()))
        .thenReturn(List.of(buildEmployee()));

    mockMvc.perform(get("/api/v1/employees/salary-range")
            .param("min", "10000")
            .param("max", "60000"))
        .andExpect(status().isOk());
  }

  // ---------------- IMPORT ----------------
  @Test
  void importEmployees_shouldReturnResult() throws Exception {

    ImportResultDto result = ImportResultDto.builder()
        .successCount(1)
        .failCount(0)
        .errors(List.of())
        .build();

    Mockito.when(importService.importEmployees(Mockito.any()))
        .thenReturn(result);

    MockMultipartFile file = new MockMultipartFile(
        "file",
        "employees.xlsx",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "dummy".getBytes()
    );

    mockMvc.perform(multipart("/api/v1/employees/import")
            .file(file))
        .andExpect(status().isOk());
  }

  // ---------------- EXPORT EXCEL ----------------
  @Test
  void exportExcel_shouldReturnFile() throws Exception {

    Mockito.doAnswer(invocation -> {
      HttpServletResponse response = invocation.getArgument(2);
      response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      response.setHeader("Content-Disposition", "attachment; filename=test.xlsx");
      return null;
    }).when(exportService).exportToExcel(Mockito.any(), Mockito.any(), Mockito.any());

    mockMvc.perform(get("/api/v1/employees/export/excel"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Type",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
  }

  // ---------------- EXPORT PDF ----------------
  @Test
  void exportPdf_shouldReturnFile() throws Exception {

    Mockito.doAnswer(invocation -> {
      HttpServletResponse response = invocation.getArgument(0);

      response.setContentType("application/pdf");
      response.setHeader("Content-Disposition", "attachment; filename=test.pdf");

      return null;
    }).when(pdfService).exportToPdf(Mockito.any());

    mockMvc.perform(get("/api/v1/employees/export/pdf"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Type", "application/pdf"))
        .andExpect(header().string("Content-Disposition",
            org.hamcrest.Matchers.containsString("attachment")));
  }

  // ---------------- HELPER ----------------
  private Employee buildEmployee() {
    Employee e = new Employee();
    e.setId(1L);
    e.setFirstName("John");
    e.setLastName("Doe");
    e.setEmail("john@test.com");
    e.setDepartment("IT");
    e.setSalary(new BigDecimal("50000"));
    e.setDateOfJoining(LocalDate.now());
    e.setActive(true);
    return e;
  }
}
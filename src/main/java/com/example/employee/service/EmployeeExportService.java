package com.example.employee.service;

import jakarta.servlet.http.HttpServletResponse;

public interface EmployeeExportService {
  void exportToExcel(String department, Boolean active, HttpServletResponse response);
}

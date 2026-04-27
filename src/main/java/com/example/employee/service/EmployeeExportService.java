package com.example.employee.service;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Service responsible for exporting employee data into an Excel (.xlsx) file.
 * <p>
 * The exported file includes employee details with optional filtering by department
 * and active status. The output is written directly to the HTTP response stream
 * without creating temporary files.
 */
public interface EmployeeExportService {

  /**
   * Exports employees to an Excel file and writes it to the HTTP response output stream.
   *
   * @param department optional filter for employee department (case-insensitive)
   * @param active optional filter for employee active status (true/false)
   * @param response the HTTP response used to stream the generated Excel file
   */
  void exportToExcel(String department, Boolean active, HttpServletResponse response);
}
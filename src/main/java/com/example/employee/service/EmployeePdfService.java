package com.example.employee.service;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Service responsible for generating a PDF report of employees.
 * <p>
 * The report includes formatted employee data, styled tables,
 * and pagination. The generated PDF is streamed directly to the client.
 */
public interface EmployeePdfService {

  /**
   * Generates a PDF report of all employees and writes it to the HTTP response.
   *
   * @param response the HTTP response used to stream the generated PDF file
   */
  void exportToPdf(HttpServletResponse response);
}
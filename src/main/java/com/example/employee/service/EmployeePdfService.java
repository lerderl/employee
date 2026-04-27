package com.example.employee.service;

import jakarta.servlet.http.HttpServletResponse;

public interface EmployeePdfService {
  void exportToPdf(HttpServletResponse response);
}

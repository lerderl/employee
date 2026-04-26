package com.example.employee.service;

import com.example.employee.dto.ImportResultDto;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeImportService {
  ImportResultDto importEmployees(MultipartFile file);
}

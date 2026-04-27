package com.example.employee.service;

import com.example.employee.dto.ImportResultDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service responsible for importing employee data from an Excel (.xlsx) file.
 * <p>
 * The import process performs row-by-row validation, aggregates errors, and allows
 * partial success. Valid records are persisted, while invalid rows are skipped and reported.
 */
public interface EmployeeImportService {

  /**
   * Imports employee records from the provided Excel file.
   *
   * @param file the uploaded Excel file (must be in .xlsx format)
   * @return an {@link ImportResultDto} containing success count, failure count,
   *         and detailed error messages per row
   *
   * @throws com.example.employee.exception.InvalidFileFormatException
   *         if the file is not a valid .xlsx format
   *
   * @throws com.example.employee.exception.ExcelProcessingException
   *         if a system-level error occurs during file processing
   */
  ImportResultDto importEmployees(MultipartFile file);
}
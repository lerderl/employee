package com.example.employee.service.implementation;

import com.example.employee.dto.EmployeeRequestDto;
import com.example.employee.dto.ImportResultDto;
import com.example.employee.entity.Employee;
import com.example.employee.exception.ExcelProcessingException;
import com.example.employee.mapper.EmployeeMapper;
import com.example.employee.service.EmployeeImportService;
import com.example.employee.service.EmployeeService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmployeeImportServiceImpl implements EmployeeImportService {
  private final EmployeeService employeeService;
  private final Validator validator;

  @Override
  @Transactional
  public ImportResultDto importEmployees(MultipartFile employeeFile) {
//    Validate file type
    if (employeeFile.isEmpty() || !Objects.requireNonNull(employeeFile.getOriginalFilename()).endsWith(".xlsx")) {
      throw new IllegalArgumentException("Only .xlsx files are supported");
    }

    int successCount = 0;
    int failureCount = 0;
    List<String> errorMessages = new ArrayList<>();

    try (
        InputStream is = employeeFile.getInputStream();
        Workbook workbook = new XSSFWorkbook(is)
        ) {
      Sheet sheet = workbook.getSheetAt(0);

//      Iterate rows and skip header
      for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
        Row row = sheet.getRow(rowNum);
        if (row == null) continue;

        try {
          EmployeeRequestDto dto = mapRowToDto(row);

//          validation
          Set<ConstraintViolation<EmployeeRequestDto>> violations = validator.validate(dto);

          if (!violations.isEmpty()) {
            failureCount++;
            for (ConstraintViolation<?> v : violations) {
              errorMessages.add("Row " + (rowNum + 1) + ": " +
                  v.getPropertyPath() + " - " + v.getMessage());
            }
            continue;
          }

//          Save via service (business rules apply here)
          Employee employee = EmployeeMapper.toEntity(dto);
          employeeService.createEmployee(employee);

          successCount++;
        } catch (Exception e) {
          failureCount++;
          errorMessages.add("Row " + (rowNum + 1) + ": " + e.getMessage());
        }
      }
    } catch (Exception e) {
//      System-level failure → rollback
      throw new ExcelProcessingException("Error processing Excel file: " + e.getMessage());
    }
    return ImportResultDto.builder()
        .successCount(successCount)
        .failCount(failureCount)
        .errors(errorMessages)
        .build();
  }

//  Row mapping
  private EmployeeRequestDto mapRowToDto(Row row) {
    return new EmployeeRequestDto(
        getString(row.getCell(0)),
        getString(row.getCell(1)),
        getString(row.getCell(2)),
        getString(row.getCell(3)),
        getBigDecimal(row.getCell(4)),
        getLocalDate(row.getCell(5)),
        getBoolean(row.getCell(6))
    );
  }

  private String getString(Cell cell) {
    return cell == null ? null : cell.getStringCellValue().trim();
  }

  private BigDecimal getBigDecimal(Cell cell) {
    if (cell == null) return null;
    return BigDecimal.valueOf(cell.getNumericCellValue());
  }

  private LocalDate getLocalDate(Cell cell) {
    if (cell == null) return null;
    return cell.getLocalDateTimeCellValue().toLocalDate();
  }

  private Boolean getBoolean(Cell cell) {
    if (cell == null) return null;
    return cell.getBooleanCellValue();
  }
}

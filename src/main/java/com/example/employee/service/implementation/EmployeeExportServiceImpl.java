package com.example.employee.service.implementation;

import org.apache.poi.ss.usermodel.*;
import lombok.RequiredArgsConstructor;
import com.example.employee.entity.Employee;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import com.example.employee.repository.EmployeeRepository;
import com.example.employee.service.EmployeeExportService;

import java.util.List;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmployeeExportServiceImpl implements EmployeeExportService {
  private final EmployeeRepository employeeRepository;

  @Override
  public void exportToExcel(String department, Boolean active, HttpServletResponse response) {
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Employees");

//      ---------------- HEADER STYLE ----------------
      XSSFCellStyle headerStyle = workbook.createCellStyle();
      Font headerFont = workbook.createFont();
      headerFont.setBold(true);
      headerFont.setColor(IndexedColors.WHITE.getIndex());

      headerStyle.setFont(headerFont);
      headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
      headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

//      ---------------- ROW STYLES ----------------
      XSSFCellStyle evenRowStyle = workbook.createCellStyle();
      evenRowStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
      evenRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

      XSSFCellStyle oddRowStyle = workbook.createCellStyle();
      oddRowStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
      oddRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

//      ---------------- SALARY STYLE ----------------
      CellStyle salaryStyle = workbook.createCellStyle();
      salaryStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));

//      ---------------- HEADER ----------------
      String[] columns = {
          "id", "firstName", "lastName", "email",
          "department", "salary", "dateOfJoining",
          "active", "createdAt", "updatedAt"
      };

      Row header = sheet.createRow(0);

      for (int i = 0; i < columns.length; i++) {
        Cell cell = header.createCell(i);
        cell.setCellValue(columns[i]);
        cell.setCellStyle(headerStyle);
      }

//      ---------------- FETCH DATA ----------------
      List<Employee> employees = fetchFiltered(department, active);

//      ---------------- DATE FORMAT ----------------
      DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
      DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;

      int rowIdx = 1;

      for (Employee e : employees) {

        Row row = sheet.createRow(rowIdx);

        XSSFCellStyle rowStyle = (rowIdx % 2 == 0) ? evenRowStyle : oddRowStyle;

        createCell(row, 0, e.getId(), rowStyle);
        createCell(row, 1, e.getFirstName(), rowStyle);
        createCell(row, 2, e.getLastName(), rowStyle);
        createCell(row, 3, e.getEmail(), rowStyle);
        createCell(row, 4, e.getDepartment(), rowStyle);

//        Salary (numeric with 2 decimal places)
        Cell salaryCell = row.createCell(5);
        if (e.getSalary() != null) {
          salaryCell.setCellValue(e.getSalary().setScale(2, RoundingMode.HALF_UP).doubleValue());
        }
        salaryCell.setCellStyle(salaryStyle);

//        Dates as ISO strings
        createCell(row, 6,
            e.getDateOfJoining() != null ? e.getDateOfJoining().format(formatter) : null,
            rowStyle);

        createCell(row, 7, e.getActive(), rowStyle);

        createCell(row, 8,
            e.getCreatedAt() != null ? e.getCreatedAt().format(dateTimeFormatter) : null,
            rowStyle);

        createCell(row, 9,
            e.getUpdatedAt() != null ? e.getUpdatedAt().format(dateTimeFormatter) : null,
            rowStyle);

        rowIdx++;
      }

//  ---------------- AUTO SIZE ----------------
      for (int i = 0; i < columns.length; i++) {
        sheet.autoSizeColumn(i);
      }

//  ---------------- RESPONSE HEADERS ----------------
      String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
      String filename = "employees_" + timestamp + ".xlsx";

      response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

//  ---------------- WRITE OUTPUT ----------------
      OutputStream os = response.getOutputStream();
      workbook.write(os);
      os.flush();
    } catch (Exception e) {
      throw new RuntimeException("Excel export failed: " + e.getMessage());
    }
  }

//  ---------------- FILTER LOGIC ----------------
  private List<Employee> fetchFiltered(String department, Boolean active) {

    if (department != null) {
      return employeeRepository.findByDepartment(department);
    }

    if (active != null) {
      return employeeRepository.findByActiveTrue();
    }

    return employeeRepository.findAll();
  }

//  ---------------- HELPER ----------------
  private void createCell(Row row, int col, Object value, CellStyle style) {
    Cell cell = row.createCell(col);

    if (value instanceof Number) {
      cell.setCellValue(((Number) value).doubleValue());
    } else if (value instanceof Boolean) {
      cell.setCellValue((Boolean) value);
    } else if (value != null) {
      cell.setCellValue(value.toString());
    }

    cell.setCellStyle(style);
  }
}

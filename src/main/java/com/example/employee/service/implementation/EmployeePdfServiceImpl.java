package com.example.employee.service.implementation;

import com.example.employee.entity.Employee;
import com.example.employee.footer.FooterPageEvent;
import com.example.employee.service.EmployeePdfService;
import com.example.employee.repository.EmployeeRepository;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.openpdf.text.*;
import org.openpdf.text.Font;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.util.Locale;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmployeePdfServiceImpl implements EmployeePdfService {
  private final EmployeeRepository employeeRepository;

  @Override
  public void exportToPdf(HttpServletResponse response) {
    try {
      String timestamp = java.time.LocalDateTime.now()
          .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

      response.setContentType("application/pdf");
      response.setHeader("Content-Disposition", "attachment; filename=employee_report_" + timestamp + ".pdf");

      byte[] pdfBytes = generateEmployeePdf();

      try (OutputStream os = response.getOutputStream()) {
        os.write(pdfBytes);
        os.flush();
      }
    } catch (Exception e) {
      throw new RuntimeException("PDF generation failed: " + e.getMessage());
    }
  }

  @Override
  public byte[] sendPdfToMail() {
    return generateEmployeePdf();
  }

//  ---------------- HELPERS ----------------

  private void addHeaderCell(PdfPTable table, String text, Font font) {
    PdfPCell cell = new PdfPCell(new Phrase(text, font));
    cell.setBackgroundColor(new Color(0, 102, 204));
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setPadding(5);
    table.addCell(cell);
  }

  private void addCell(PdfPTable table, Object value, Font font, Color bg, int align) {
    PdfPCell cell = new PdfPCell(new Phrase(value != null ? value.toString() : "", font));
    cell.setBackgroundColor(bg);
    cell.setHorizontalAlignment(align);
    cell.setPadding(5);
    table.addCell(cell);
  }

  private byte[] generateEmployeePdf() {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()){
      List<Employee> employees = employeeRepository.findAll();

      Document document = new Document(PageSize.A4, 36, 36, 54, 54);
      PdfWriter writer = PdfWriter.getInstance(document, out);

//      Footer handler
      writer.setPageEvent(new FooterPageEvent());
      document.open();

//      ---------------- TITLE ----------------
      Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
      Font normalFont = new Font(Font.HELVETICA, 10);

      Paragraph company = new Paragraph("Lerderl Ltd.", titleFont);
      company.setAlignment(Element.ALIGN_CENTER);

      Paragraph title = new Paragraph("Employees Report", titleFont);
      title.setAlignment(Element.ALIGN_CENTER);

      Paragraph meta = new Paragraph(
          "Generated: " + java.time.LocalDateTime.now()
              .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) +
              "\nTotal Records: " + employees.size(),
          normalFont
      );
      meta.setAlignment(Element.ALIGN_CENTER);

      document.add(company);
      document.add(title);
      document.add(meta);
      document.add(Chunk.NEWLINE);

//      ---------------- TABLE ----------------
      PdfPTable table = new PdfPTable(7);
      table.setWidthPercentage(100);
      table.setWidths(new float[]{1, 2, 3, 2, 2, 2, 1});

//      Header style
      Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);

      addHeaderCell(table, "ID", headerFont);
      addHeaderCell(table, "Full Name", headerFont);
      addHeaderCell(table, "Email", headerFont);
      addHeaderCell(table, "Department", headerFont);
      addHeaderCell(table, "Salary", headerFont);
      addHeaderCell(table, "Date of Joining", headerFont);
      addHeaderCell(table, "Status", headerFont);

//      ---------------- DATA ----------------
      boolean alternate = false;

      NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "NG"));
      DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;

      for (Employee e : employees) {
        Color bgColor = alternate ? new Color(230, 240, 255) : Color.WHITE;
        alternate = !alternate;

        Font rowFont = new Font(Font.HELVETICA, 9);

        if (Boolean.FALSE.equals(e.getActive())) {
          rowFont.setStyle(Font.STRIKETHRU);
          rowFont.setColor(Color.GRAY);
        }

        addCell(table, e.getId(), rowFont, bgColor, Element.ALIGN_LEFT);

        addCell(table,
            e.getFirstName() + " " + e.getLastName(),
            rowFont, bgColor, Element.ALIGN_LEFT);

        addCell(table, e.getEmail(), rowFont, bgColor, Element.ALIGN_LEFT);
        addCell(table, e.getDepartment(), rowFont, bgColor, Element.ALIGN_LEFT);

//        Salary (right-aligned)
        addCell(table,
            currency.format(e.getSalary()),
            rowFont, bgColor, Element.ALIGN_RIGHT);

        addCell(table,
            e.getDateOfJoining().format(dateFormatter),
            rowFont, bgColor, Element.ALIGN_LEFT);

        addCell(table,
            e.getActive() ? "Active" : "Inactive",
            rowFont, bgColor, Element.ALIGN_LEFT);
      }

      document.add(table);

      document.close();
      return out.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("PDF generation failed: " + e.getMessage());
    }
  }
}

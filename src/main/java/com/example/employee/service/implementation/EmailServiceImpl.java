package com.example.employee.service.implementation;

import com.example.employee.service.EmailService;

import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
  private final JavaMailSender mailSender;

  @Override
  public void sendEmployeeReport(String to, byte[] excel, byte[] pdf) {

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);

      helper.setTo(to);
      helper.setSubject("Employee Reports");
      helper.setText("Please find attached employee reports (Excel & PDF).");

      helper.addAttachment("employees.xlsx",
          new org.springframework.core.io.ByteArrayResource(excel));

      helper.addAttachment("employees.pdf",
          new org.springframework.core.io.ByteArrayResource(pdf));

      mailSender.send(message);

    } catch (Exception e) {
      throw new RuntimeException("Email sending failed", e);
    }
  }
}

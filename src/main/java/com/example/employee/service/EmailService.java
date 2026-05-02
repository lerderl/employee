package com.example.employee.service;

public interface EmailService {
  void sendEmployeeReport(String to, byte[] excel, byte[] pdf);
}

package com.example.employee.dto;

import lombok.Data;
import lombok.Builder;

import java.util.List;

@Data
@Builder
public class ImportResultDto {
  private int successCount;
  private int failCount;
  private List<String> errors;
}

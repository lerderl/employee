package com.example.employee.footer;

import org.openpdf.text.pdf.*;
import org.openpdf.text.Document;

import java.io.IOException;

public class FooterPageEvent extends PdfPageEventHelper {
  private PdfTemplate total;

  @Override
  public void onOpenDocument(PdfWriter writer, Document document) {
    total = writer.getDirectContent().createTemplate(30, 16);
  }

  @Override
  public void onEndPage(PdfWriter writer, Document document) {
    PdfContentByte cb = writer.getDirectContent();
    String text = "Page " + writer.getPageNumber() + " of ";

    float x = document.right() - 100;
    float y = document.bottom() - 10;

    cb.beginText();
    try {
      cb.setFontAndSize(BaseFont.createFont(), 9);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    cb.setTextMatrix(x, y);
    cb.showText(text);
    cb.endText();

    cb.addTemplate(total, x + 50, y);
  }

  @Override
  public void onCloseDocument(PdfWriter writer, Document document) {
    total.beginText();
    try {
      total.setFontAndSize(BaseFont.createFont(), 9);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    total.showText(String.valueOf(writer.getPageNumber() - 1));
    total.endText();
  }
}

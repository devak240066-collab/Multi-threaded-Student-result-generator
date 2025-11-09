package com.example.results.service;

import com.example.results.model.Student;
import java.io.ByteArrayOutputStream;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelService {
  public static byte[] toWorkbookBytes(List<Student> students) {
    try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      Sheet sh = wb.createSheet("Results");
      // Determine all subjects
      Set<String> subjects = new LinkedHashSet<>();
      for (Student s : students) subjects.addAll(s.getMarks().keySet());
      List<String> subList = new ArrayList<>(subjects);

      int rowIdx = 0;
      Row header = sh.createRow(rowIdx++);
      int c = 0;
      header.createCell(c++).setCellValue("id");
      header.createCell(c++).setCellValue("name");
      for (String sub : subList) header.createCell(c++).setCellValue(sub);
      header.createCell(c++).setCellValue("total");
      header.createCell(c++).setCellValue("average");
      header.createCell(c++).setCellValue("grade");

      for (Student s : students) {
        Row r = sh.createRow(rowIdx++);
        int j = 0;
        r.createCell(j++).setCellValue(s.getId());
        r.createCell(j++).setCellValue(s.getName());
        for (String sub : subList) {
          Integer mark = s.getMarks().get(sub);
          r.createCell(j++).setCellValue(mark == null ? 0 : mark);
        }
        r.createCell(j++).setCellValue(s.getTotal());
        r.createCell(j++).setCellValue(s.getAverage());
        r.createCell(j++).setCellValue(s.getGrade());
      }

      for (int i = 0; i < 3 + subList.size(); i++) sh.autoSizeColumn(i);
      wb.write(out);
      return out.toByteArray();
    } catch (Exception e) {
      return new byte[0];
    }
  }
}

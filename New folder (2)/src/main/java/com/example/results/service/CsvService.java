package com.example.results.service;

import com.example.results.model.Student;
import java.io.*;
import java.util.*;
import org.apache.commons.csv.*;

public class CsvService {
  public static List<Student> parse(String csvText) {
    if (csvText == null) return List.of();
    try (Reader in = new StringReader(csvText)) {
      CSVFormat fmt = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build();
      Iterable<CSVRecord> records = fmt.parse(in);
      List<Student> list = new ArrayList<>();
      for (CSVRecord r : records) {
        String id = safe(r, "id");
        String name = safe(r, "name");
        Map<String,Integer> marks = new LinkedHashMap<>();
        for (String h : r.getParser().getHeaderNames()) {
          if (h.equalsIgnoreCase("id") || h.equalsIgnoreCase("name")) continue;
          String v = r.get(h);
          if (v != null && !v.isBlank()) {
            try { marks.put(h, Integer.parseInt(v.trim())); } catch (NumberFormatException ignored) {}
          }
        }
        Student s = new Student(id, name, marks);
        list.add(s);
      }
      return list;
    } catch (Exception e) {
      return List.of();
    }
  }

  private static String safe(CSVRecord r, String key) {
    try { return r.get(key); } catch (Exception e) { return ""; }
  }
}

package com.example.results.model;

import java.util.*;

public class Student {
  private String id;
  private String name;
  private Map<String, Integer> marks;
  private int total;
  private double average;
  private String grade;

  public Student() {}

  public Student(String id, String name, Map<String, Integer> marks) {
    this.id = id;
    this.name = name;
    this.marks = new LinkedHashMap<>(marks);
    recalc();
  }

  public void recalc() {
    this.total = marks.values().stream().mapToInt(Integer::intValue).sum();
    this.average = marks.isEmpty() ? 0 : total * 1.0 / marks.size();
    this.grade = average >= 90 ? "A+" : average >= 80 ? "A" : average >= 70 ? "B" : average >= 60 ? "C" : average >= 50 ? "D" : "F";
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public Map<String, Integer> getMarks() { return marks; }
  public void setMarks(Map<String, Integer> marks) { this.marks = marks; recalc(); }
  public int getTotal() { return total; }
  public double getAverage() { return average; }
  public String getGrade() { return grade; }
}

package com.example.results.service;

import com.example.results.model.Student;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.*;

public class GenerationService {
  private final ThreadPoolExecutor executor;
  private final SecureRandom rnd = new SecureRandom();

  public GenerationService(ThreadPoolExecutor executor) { this.executor = executor; }

  public List<Student> generate(int count, List<String> subjects) {
    List<Callable<Student>> tasks = new ArrayList<>();
    for (int i = 1; i <= count; i++) {
      final int idx = i;
      tasks.add(() -> createStudent(idx, subjects));
    }
    try {
      List<Future<Student>> futures = executor.invokeAll(tasks);
      List<Student> res = new ArrayList<>(futures.size());
      for (Future<Student> f : futures) res.add(f.get());
      return res;
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      return Collections.emptyList();
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  private Student createStudent(int idx, List<String> subjects) {
    String id = String.format("S%04d", idx);
    String name = randomName();
    Map<String,Integer> marks = new LinkedHashMap<>();
    for (String s : subjects) {
      marks.put(s, 40 + rnd.nextInt(61)); // 40..100
    }
    return new Student(id, name, marks);
  }

  private String randomName() {
    String[] first = {"Aarav","Vivaan","Aditya","Sai","Diya","Isha","Aanya","Kiran","Neha","Ravi"};
    String[] last = {"Sharma","Patel","Reddy","Iyer","Khan","Singh","Das","Nair","Gupta","Joshi"};
    return first[rnd.nextInt(first.length)] + " " + last[rnd.nextInt(last.length)];
  }
}

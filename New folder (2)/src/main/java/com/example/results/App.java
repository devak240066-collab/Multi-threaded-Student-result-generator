package com.example.results;

import static spark.Spark.*;

import com.example.results.db.StudentDao;
import com.example.results.model.Student;
import com.example.results.service.CsvService;
import com.example.results.service.ExcelService;
import com.example.results.service.GenerationService;
import com.example.results.service.MetricsService;
import com.example.results.service.ThreadPoolManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import spark.Filter;

public class App {
  private static final Gson GSON = new Gson();
  private static final List<Student> MEMORY_STORE = new CopyOnWriteArrayList<>();

  public static void main(String[] args) {
    port(getAssignedPort());
    ipAddress("0.0.0.0");

    staticFiles.location("public");

    ThreadPoolManager.init();
    MetricsService.init(ThreadPoolManager.getExecutor());

    StudentDao dao = new StudentDao();
    dao.createTableIfNotExists();

    before(corsHeaders());
    options("/*", (req, res) -> "OK");

    get("/api/metrics", (req, res) -> {
      res.type("application/json");
      return GSON.toJson(MetricsService.snapshot());
    });

    get("/api/students", (req, res) -> {
      res.type("application/json");
      return GSON.toJson(MEMORY_STORE);
    });

    post("/api/generate", (req, res) -> {
      res.type("application/json");
      JsonObject body = GSON.fromJson(req.body(), JsonObject.class);
      int count = body != null && body.has("count") ? body.get("count").getAsInt() : 50;
      final List<String> subjects = new ArrayList<>();
      if (body != null && body.has("subjects") && body.get("subjects").isJsonArray()) {
        body.getAsJsonArray("subjects").forEach(e -> subjects.add(e.getAsString()));
      } else {
        subjects.addAll(Arrays.asList("Maths", "Physics", "Chemistry", "English", "CS"));
      }

      GenerationService gen = new GenerationService(ThreadPoolManager.getExecutor());
      List<Student> generated = gen.generate(count, subjects);

      MEMORY_STORE.clear();
      MEMORY_STORE.addAll(generated);

      dao.batchUpsert(generated);

      return GSON.toJson(Map.of("generated", generated.size()));
    });

    post("/api/import", (req, res) -> {
      res.type("application/json");
      String csv = req.body();
      List<Student> parsed = CsvService.parse(csv);
      MEMORY_STORE.clear();
      MEMORY_STORE.addAll(parsed);
      dao.batchUpsert(parsed);
      return GSON.toJson(Map.of("imported", parsed.size()));
    });

    get("/api/export", (req, res) -> {
      byte[] bytes = ExcelService.toWorkbookBytes(MEMORY_STORE);
      res.type("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      res.header("Content-Disposition", "attachment; filename=results.xlsx");
      res.raw().getOutputStream().write(bytes);
      res.raw().getOutputStream().flush();
      return res.raw();
    });

    get("/health", (req, res) -> "OK");
  }

  private static int getAssignedPort() {
    String port = System.getenv("PORT");
    if (port != null) {
      try { return Integer.parseInt(port); } catch (NumberFormatException ignored) {}
    }
    return 4567;
  }

  private static Filter corsHeaders() {
    return (req, res) -> {
      res.header("Access-Control-Allow-Origin", "*");
      res.header("Access-Control-Allow-Headers", "Content-Type");
      res.header("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
    };
  }
}

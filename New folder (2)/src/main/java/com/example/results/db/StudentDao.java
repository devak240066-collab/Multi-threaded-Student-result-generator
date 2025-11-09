package com.example.results.db;

import com.example.results.model.Student;
import com.google.gson.Gson;
import java.sql.*;
import java.util.*;

public class StudentDao {
  private static final Gson GSON = new Gson();

  public void createTableIfNotExists() {
    try (Connection c = getConnection(); Statement st = c.createStatement()) {
      st.executeUpdate("CREATE TABLE IF NOT EXISTS students (id VARCHAR(64) PRIMARY KEY, name VARCHAR(255), marks CLOB, total INT, average DOUBLE, grade VARCHAR(8))");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void batchUpsert(List<Student> students) {
    String sql = "MERGE INTO students KEY(id) VALUES(?,?,?,?,?,?)";
    try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
      for (Student s : students) {
        ps.setString(1, s.getId());
        ps.setString(2, s.getName());
        ps.setString(3, GSON.toJson(s.getMarks()));
        ps.setInt(4, s.getTotal());
        ps.setDouble(5, s.getAverage());
        ps.setString(6, s.getGrade());
        ps.addBatch();
      }
      ps.executeBatch();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private Connection getConnection() throws SQLException {
    String url = System.getenv().getOrDefault("JDBC_URL", "jdbc:h2:file:./data/studentdb;DB_CLOSE_DELAY=-1;MODE=MySQL");
    return DriverManager.getConnection(url, "sa", "");
  }
}

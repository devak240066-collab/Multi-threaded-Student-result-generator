package com.example.results.service;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

public class MetricsService {
  private static ThreadPoolExecutor executor;

  public static void init(ThreadPoolExecutor ex) { executor = ex; }

  public static Map<String, Object> snapshot() {
    Map<String, Object> m = new LinkedHashMap<>();
    if (executor != null) {
      m.put("poolSize", executor.getPoolSize());
      m.put("activeCount", executor.getActiveCount());
      m.put("queued", executor.getQueue().size());
      m.put("completed", executor.getCompletedTaskCount());
      m.put("corePoolSize", executor.getCorePoolSize());
    }
    OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
    m.put("availableProcessors", os.getAvailableProcessors());
    m.put("systemLoadAverage", os.getSystemLoadAverage());
    Runtime rt = Runtime.getRuntime();
    m.put("freeMemory", rt.freeMemory());
    m.put("totalMemory", rt.totalMemory());
    m.put("maxMemory", rt.maxMemory());
    return m;
  }
}

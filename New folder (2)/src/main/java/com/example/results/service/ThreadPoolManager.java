package com.example.results.service;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolManager {
  private static ThreadPoolExecutor EXECUTOR;
  private static final AtomicInteger THREAD_NUM = new AtomicInteger(1);

  public static void init() {
    if (EXECUTOR != null) return;
    int cores = Runtime.getRuntime().availableProcessors();
    int threads = Math.max(2, Math.min(cores * 2, 32));
    BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(1000);
    EXECUTOR = new ThreadPoolExecutor(
        threads,
        threads,
        60L,
        TimeUnit.SECONDS,
        queue,
        new ThreadFactory() {
          private final ThreadFactory def = Executors.defaultThreadFactory();
          @Override public Thread newThread(Runnable r) {
            Thread t = def.newThread(r);
            t.setName("work-" + THREAD_NUM.getAndIncrement());
            t.setDaemon(false);
            return t;
          }
        },
        new ThreadPoolExecutor.CallerRunsPolicy());
  }

  public static ThreadPoolExecutor getExecutor() { return EXECUTOR; }
}

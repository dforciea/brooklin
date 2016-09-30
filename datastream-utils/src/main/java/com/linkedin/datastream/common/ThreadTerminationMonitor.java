package com.linkedin.datastream.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;


/**
 * This class traps threads with unhandled exceptions and
 * reports abnormal terminations and reports them as metric,
 * however it doesn't attempt to recover the condition
 */
public class ThreadTerminationMonitor {

  private static final String ABNORMAL_TERMINATIONS = "abnormalTerminations";

  private static final Logger LOG = LoggerFactory.getLogger(ThreadTerminationMonitor.class);

  private static final Thread.UncaughtExceptionHandler OLD_HANDLER;
  private static final Meter TERMINATION_RATE;

  static {
    // Create metric
    TERMINATION_RATE = new Meter();

    // Replace the default uncaught exception handler
    OLD_HANDLER = Thread.getDefaultUncaughtExceptionHandler();
    Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> {
      LOG.error(String.format("Thread %s terminated abnormally", t.getName()), e);
      TERMINATION_RATE.mark();
      // Resume the old behavior
      if (OLD_HANDLER != null) {
        OLD_HANDLER.uncaughtException(t, e);
      }
    });
  }

  public static Map<String, Metric> getMetrics() {
    Map<String, Metric> metrics = new HashMap<>();
    String metricName = MetricRegistry.name(
        ThreadTerminationMonitor.class.getSimpleName(), ABNORMAL_TERMINATIONS);
    metrics.put(metricName, TERMINATION_RATE);
    return Collections.unmodifiableMap(metrics);
  }
}
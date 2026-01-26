package com.burakpozut.order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
// 1) Decide the log format (JSON)
// Goal: logs are structured, not free‑text
// Add JSON logging in Spring Boot (logback JSON encoder)
// Include fields: timestamp, level, service, traceId, event, message
// 2) Add Logstash to your stack
// Run Logstash as a container
// Expose a Beats input (port 5044)
// Output to Elasticsearch
// 3) Add Filebeat (or Fluent Bit)
// Filebeat ships container logs to Logstash
// It adds metadata (container name, service)
// 4) Logstash pipeline (key part)
// Typical pipeline stages:
// input: beats on 5044
// filter: parse JSON, add fields (service, env)
// output: Elasticsearch index logs-%{+YYYY.MM.dd}
// 5) Create Kibana data view
// Index pattern: logs-* (or filebeat-*)
// Verify fields like traceId, service, level
// 6) Query by traceId
// Example: traceId:"<id>"
// Build saved searches or dashboards
// 7) Add correlation to traces later
// OpenTelemetry (optional), link logs ↔ traces
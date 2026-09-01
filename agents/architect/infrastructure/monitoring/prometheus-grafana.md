# Monitoring and Observability: Prometheus + Grafana

## Overview

Floktask uses a comprehensive monitoring stack based on **Prometheus** for metrics collection and **Grafana** for visualization. This setup provides real-time insights into application performance, infrastructure health, and business metrics.

## Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     Monitoring Architecture                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────────────────────┐   │
│  │  Application│    │  Kubernetes │    │         AWS Services          │   │
│  │  Metrics    │    │  Metrics    │    │                                 │   │
│  └──────┬──────┘    └──────┬──────┘    └──────────────┬──────────────┘   │
│         │                 │                        │                     │
│         ▼                 ▼                        ▼                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        Prometheus Server                              │   │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────────────────┐ │   │
│  │  │  Scrape     │    │  Storage    │    │  Alert Manager           │ │   │
│  │  │  Targets    │    │  (TSDB)     │    │  (Alert Rules)           │ │   │
│  │  └─────────────┘    └─────────────┘    └─────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                         │
│                                    ▼                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        Grafana                                       │   │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────────────────┐ │   │
│  │  │  Dashboards │    │  Alerts      │    │  Data Sources           │ │   │
│  │  └─────────────┘    └─────────────┘    └─────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                         │
│         ┌──────────────────────────┬──────────────────────────┐            │
│         ▼                          ▼                          ▼            │
│  ┌─────────────┐           ┌─────────────┐           ┌─────────────┐    │
│  │   Slack     │           │   Email     │           │  PagerDuty  │    │
│  └─────────────┘           └─────────────┘           └─────────────┘    │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Components

### 1. Prometheus

**Purpose**: Time-series metrics collection and storage

#### Configuration

```yaml
# prometheus-values.yaml (Helm chart configuration)
global:
  scrape_interval: 15s
  evaluation_interval: 15s
  scrape_timeout: 10s

server:
  persistentVolume:
    enabled: true
    size: 500Gi
    storageClass: gp3
  retention: 90d
  retentionSize: "400GB"
  resources:
    requests:
      memory: 4Gi
      cpu: 2
    limits:
      memory: 8Gi
      cpu: 4

alertmanager:
  enabled: true
  persistentVolume:
    enabled: true
    size: 10Gi
  config:
    global:
      resolve_timeout: 5m
      slack_api_url: ${SLACK_WEBHOOK_URL}
    route:
      group_by: ['alertname', 'severity']
      group_wait: 30s
      group_interval: 5m
      repeat_interval: 3h
      receiver: 'slack-notifications'
      routes:
        - match:
            severity: critical
          receiver: 'pagerduty-critical'
        - match:
            severity: warning
          receiver: 'slack-notifications'
    receivers:
      - name: 'slack-notifications'
        slack_configs:
          - channel: '#floktask-alerts'
            send_resolved: true
            title: '{{ template "slack.floktask.title" . }}'
            text: '{{ template "slack.floktask.text" . }}'
      - name: 'pagerduty-critical'
        pagerduty_configs:
          - service_key: ${PAGERDUTY_SERVICE_KEY}
            send_resolved: true

pushgateway:
  enabled: true
  persistentVolume:
    enabled: true
    size: 10Gi
```

#### Scrape Targets

| Target | Endpoint | Interval | Purpose |
|--------|----------|----------|---------|
| Kubernetes Pods | `/metrics` | 15s | Application metrics |
| Kubernetes Nodes | `/metrics` | 15s | Node metrics |
| Kubernetes System | `/metrics` | 15s | K8s components |
| Prometheus | `/metrics` | 15s | Self-monitoring |
| Node Exporter | `/metrics` | 15s | System metrics |
| cAdvisor | `/metrics` | 15s | Container metrics |
| AWS CloudWatch | `cloudwatch_exporter` | 60s | AWS metrics |
| Application | `/actuator/prometheus` | 15s | Custom app metrics |

### 2. Grafana

**Purpose**: Visualization and alerting

#### Configuration

```yaml
# grafana-values.yaml (Helm chart configuration)
replicas: 2
persistence:
  enabled: true
  size: 50Gi
  storageClass: gp3
  accessModes: [ReadWriteOnce]

resources:
  requests:
    memory: 1Gi
    cpu: 500m
  limits:
    memory: 2Gi
    cpu: 1

service:
  type: ClusterIP
  port: 3000

ingress:
  enabled: true
  annotations:
    kubernetes.io/ingress.class: nginx
    cert-manager.io/cluster-issuer: letsencrypt-prod
  hosts:
    - grafana.floktask.com
  tls:
    - secretName: grafana-tls
      hosts:
        - grafana.floktask.com

grafana.ini:
  auth:
    disable_login_form: false
    disable_signout_menu: false
  auth.anonymous:
    enabled: false
  auth.github:
    enabled: true
    client_id: ${GITHUB_CLIENT_ID}
    client_secret: ${GITHUB_CLIENT_SECRET}
    scopes: user:email,read:org
    auth_url: https://github.com/login/oauth/authorize
    token_url: https://github.com/login/oauth/access_token
    api_url: https://api.github.com/user
    allowed_organizations: dondgoklo-cyber

plugins:
  - grafana-clock-panel
  - grafana-simple-json-datasource
  - grafana-worldmap-panel
  - grafana-piechart-panel
  - vonage-status-panel

dashboards:
  default:
    timepicker:
      refresh_intervals: ["5s", "10s", "30s", "1m", "5m", "15m", "30m", "1h", "2h", "1d"]
      time_options: ["5m", "15m", "1h", "6h", "12h", "24h", "2d", "7d", "30d"]

sidebar:
  sections:
    - name: Floktask
      type: custom
      items:
        - name: Overview
          url: /d/overview
          icon: dashboard
        - name: Infrastructure
          url: /d/infrastructure
          icon: server
        - name: Applications
          url: /d/applications
          icon: applications
        - name: Business Metrics
          url: /d/business
          icon: analytics
```

#### Data Sources

| Data Source | Type | URL | Access |
|-------------|------|-----|--------|
| Prometheus | Prometheus | http://prometheus-server:9090 | Server |
| Loki | Loki | http://loki:3100 | Server |
| Tempo | Tempo | http://tempo:3200 | Server |
| AWS CloudWatch | CloudWatch | https://monitoring.eu-central-1.amazonaws.com | AWS IAM |
| Elasticsearch | Elasticsearch | http://elasticsearch:9200 | Server |

### 3. Alert Manager

**Purpose**: Alert routing and management

#### Alert Rules

```yaml
# alert-rules.yaml
groups:
  - name: infrastructure.rules
    rules:
      # Kubernetes Cluster
      - alert: KubeClusterUnhealthy
        expr: kube_node_status_condition{condition="Ready", status="true"} == 0
        for: 5m
        labels:
          severity: critical
          category: infrastructure
        annotations:
          summary: "Kubernetes cluster is unhealthy"
          description: "Node {{ $labels.node }} has been unready for more than 5 minutes"

      - alert: KubePodCrashLooping
        expr: increase(kube_pod_container_status_restarts_total[1h]) > 5
        for: 10m
        labels:
          severity: warning
          category: infrastructure
        annotations:
          summary: "Pod is crash looping"
          description: "Pod {{ $labels.namespace }}/{{ $labels.pod }} is crash looping ({{ $value }} restarts in last hour)"

      - alert: KubePodNotReady
        expr: kube_pod_status_ready{condition="true"} == 0
        for: 5m
        labels:
          severity: warning
          category: infrastructure
        annotations:
          summary: "Pod is not ready"
          description: "Pod {{ $labels.namespace }}/{{ $labels.pod }} has been in non-ready state for more than 5 minutes"

      # Resource Utilization
      - alert: HighCPUUsage
        expr: (100 - (avg by (instance) (irate(node_cpu_seconds_total{mode="idle"}[5m])) * 100)) > 90
        for: 10m
        labels:
          severity: warning
          category: infrastructure
        annotations:
          summary: "High CPU usage"
          description: "Node {{ $labels.instance }} has CPU usage of {{ $value }}%"

      - alert: HighMemoryUsage
        expr: (node_memory_MemTotal_bytes - node_memory_MemAvailable_bytes) / node_memory_MemTotal_bytes * 100 > 90
        for: 10m
        labels:
          severity: warning
          category: infrastructure
        annotations:
          summary: "High memory usage"
          description: "Node {{ $labels.instance }} has memory usage of {{ $value }}%"

      - alert: HighDiskUsage
        expr: (node_filesystem_avail_bytes{mountpoint="/"} / node_filesystem_size_bytes{mountpoint="/"}) * 100 < 10
        for: 10m
        labels:
          severity: critical
          category: infrastructure
        annotations:
          summary: "High disk usage"
          description: "Node {{ $labels.instance }} has less than 10% disk space available"

  - name: application.rules
    rules:
      # Application Health
      - alert: ApplicationDown
        expr: up{job="floktask-services"} == 0
        for: 2m
        labels:
          severity: critical
          category: application
        annotations:
          summary: "Application is down"
          description: "Service {{ $labels.service }} has been down for more than 2 minutes"

      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status=~"5.."}[5m]) / rate(http_server_requests_seconds_count[5m]) > 0.05
        for: 5m
        labels:
          severity: warning
          category: application
        annotations:
          summary: "High error rate"
          description: "Service {{ $labels.service }} has error rate of {{ $value }}%"

      - alert: HighLatency
        expr: histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket[5m])) by (le, service)) > 1
        for: 10m
        labels:
          severity: warning
          category: application
        annotations:
          summary: "High latency"
          description: "Service {{ $labels.service }} has 95th percentile latency of {{ $value }}s"

      # Business Metrics
      - alert: HighSyncQueueSize
        expr: sync_queue_size > 1000
        for: 15m
        labels:
          severity: warning
          category: business
        annotations:
          summary: "High sync queue size"
          description: "Sync queue has {{ $value }} items waiting"

      - alert: SyncFailures
        expr: increase(sync_failures_total[5m]) > 10
        for: 5m
        labels:
          severity: warning
          category: business
        annotations:
          summary: "Sync failures"
          description: "{{ $value }} sync failures in last 5 minutes"

  - name: database.rules
    rules:
      # PostgreSQL
      - alert: PostgreSQLDown
        expr: pg_up == 0
        for: 2m
        labels:
          severity: critical
          category: database
        annotations:
          summary: "PostgreSQL is down"
          description: "PostgreSQL instance {{ $labels.instance }} has been down for more than 2 minutes"

      - alert: HighDatabaseConnections
        expr: pg_stat_activity_count{datname!~"template.*|postgres"} > pg_settings_max_connections * 0.8
        for: 10m
        labels:
          severity: warning
          category: database
        annotations:
          summary: "High database connections"
          description: "Database {{ $labels.datname }} has {{ $value }} connections (80% of max)"

      - alert: LongRunningQueries
        expr: pg_stat_activity_max_tx_duration_seconds > 30
        for: 5m
        labels:
          severity: warning
          category: database
        annotations:
          summary: "Long running query"
          description: "Database {{ $labels.datname }} has query running for {{ $value }} seconds"

      # Redis
      - alert: RedisDown
        expr: redis_up == 0
        for: 2m
        labels:
          severity: critical
          category: database
        annotations:
          summary: "Redis is down"
          description: "Redis instance {{ $labels.instance }} has been down for more than 2 minutes"

      - alert: HighRedisMemory
        expr: redis_memory_used_bytes / redis_memory_max_bytes * 100 > 90
        for: 10m
        labels:
          severity: warning
          category: database
        annotations:
          summary: "High Redis memory usage"
          description: "Redis instance {{ $labels.instance }} has memory usage of {{ $value }}%"

  - name: aws.rules
    rules:
      # AWS Metrics
      - alert: HighAPILatency
        expr: aws_api_gateway_latency_p99 > 1000
        for: 10m
        labels:
          severity: warning
          category: aws
        annotations:
          summary: "High API Gateway latency"
          description: "API Gateway has 99th percentile latency of {{ $value }}ms"

      - alert: HighAPIErrors
        expr: rate(aws_api_gateway_5xx_error[5m]) > 0.01
        for: 5m
        labels:
          severity: warning
          category: aws
        annotations:
          summary: "High API Gateway errors"
          description: "API Gateway has {{ $value }}% 5xx errors"

      - alert: HighLambdaErrors
        expr: rate(aws_lambda_errors_total[5m]) > 0.05
        for: 5m
        labels:
          severity: warning
          category: aws
        annotations:
          summary: "High Lambda errors"
          description: "Lambda function {{ $labels.function_name }} has {{ $value }}% errors"
```

## Dashboards

### 1. Overview Dashboard

**Purpose**: High-level view of the entire system

**Panels**:
- System Health Status
- Total Requests (Rate)
- Error Rate
- Average Latency
- Active Users
- Resource Utilization (CPU, Memory, Disk)
- Service Status
- Recent Alerts

### 2. Infrastructure Dashboard

**Purpose**: Kubernetes cluster and infrastructure monitoring

**Panels**:
- Cluster Status
- Node Status
- Pod Status
- Resource Requests vs Limits
- Network Traffic
- Storage Usage
- Ingress/Ingress Traffic
- HPA (Horizontal Pod Autoscaler) Status

### 3. Application Dashboard

**Purpose**: Application-level metrics

**Panels**:
- Request Rate by Service
- Error Rate by Service
- Latency Distribution
- JVM Metrics (Memory, GC, Threads)
- Database Connection Pool
- Cache Hit Ratio
- Sync Queue Size
- Sync Processing Time

### 4. Database Dashboard

**Purpose**: Database performance monitoring

**Panels**:
- PostgreSQL Status
- Query Performance
- Connection Count
- Locks
- Replication Status
- Cache Hit Ratio
- Slow Queries
- Redis Memory Usage
- Redis Commands Rate

### 5. Business Metrics Dashboard

**Purpose**: Business-level metrics and KPIs

**Panels**:
- Active Users
- Daily Active Users (DAU)
- Monthly Active Users (MAU)
- Tasks Created
- Tasks Completed
- Projects Created
- Sync Operations
- AI Requests
- Notification Delivery Rate
- User Registration Rate

### 6. Cost Monitoring Dashboard

**Purpose**: Cloud cost tracking

**Panels**:
- AWS Cost by Service
- AWS Cost by Account
- Cost Forecast
- Cost Anomalies
- Resource Utilization vs Cost
- Reserved Instance Utilization
- Spot Instance Savings

## Metrics Collection

### Application Metrics (Spring Boot Actuator)

```yaml
# application.yaml
management:
  endpoints:
    web:
      exposure:
        include: health, metrics, prometheus
    prometheus:
      enabled: true
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
    distribution:
      percentiles:
        http.server.requests: 0.5, 0.9, 0.95, 0.99
    tags:
      application: floktask
      service: ${spring.application.name}

# Custom metrics
@Bean
MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
    return registry -> registry.config().commonTags(
        "application", "floktask",
        "environment", System.getProperty("spring.profiles.active", "default"),
        "region", System.getenv("AWS_REGION")
    );
}
```

### Custom Business Metrics

```java
// TaskService.java
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;

@Service
public class TaskService {
    
    private final Counter tasksCreatedCounter;
    private final Counter tasksCompletedCounter;
    private final Timer taskProcessingTimer;
    
    public TaskService(MeterRegistry meterRegistry) {
        this.tasksCreatedCounter = Counter.builder("floktask.tasks.created")
            .description("Number of tasks created")
            .tag("service", "task-service")
            .register(meterRegistry);
            
        this.tasksCompletedCounter = Counter.builder("floktask.tasks.completed")
            .description("Number of tasks completed")
            .tag("service", "task-service")
            .register(meterRegistry);
            
        this.taskProcessingTimer = Timer.builder("floktask.tasks.processing.time")
            .description("Time taken to process tasks")
            .tag("service", "task-service")
            .publishPercentileHistogram()
            .register(meterRegistry);
    }
    
    public Task createTask(Task task) {
        tasksCreatedCounter.increment();
        return taskProcessingTimer.record(() -> taskRepository.save(task));
    }
    
    public Task completeTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.setStatus(TaskStatus.DONE);
        tasksCompletedCounter.increment();
        return taskRepository.save(task);
    }
}
```

## Alerting

### Notification Channels

| Channel | Purpose | Severity |
|---------|---------|----------|
| Slack #floktask-alerts | General alerts | Warning, Critical |
| Email | All alerts | All |
| PagerDuty | Critical alerts | Critical |
| SMS | Critical alerts | Critical |

### Alert Grouping

- **Group by**: alertname, severity
- **Group wait**: 30 seconds
- **Group interval**: 5 minutes
- **Repeat interval**: 3 hours

### Alert Resolutions

- **Auto-resolve**: Most alerts resolve automatically when the condition clears
- **Manual resolve**: Some alerts require manual acknowledgment
- **Escalation**: Critical alerts escalate to PagerDuty after 15 minutes

## Logging

### Centralized Logging Stack

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         Logging Architecture                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────────────────────┐   │
│  │  Application│    │  Kubernetes │    │         AWS Services          │   │
│  │  Logs       │    │  Logs       │    │                                 │   │
│  └──────┬──────┘    └──────┬──────┘    └──────────────┬──────────────┘   │
│         │                 │                        │                     │
│         ▼                 ▼                        ▼                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        Fluent Bit                                   │   │
│  │  (DaemonSet on each node)                                        │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                         │
│                                    ▼                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        OpenSearch / Elasticsearch                     │   │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────────────────┐ │   │
│  │  │  Indexing   │    │  Storage    │    │  Search & Analytics       │ │   │
│  │  └─────────────┘    └─────────────┘    └─────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                         │
│                                    ▼                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        Kibana / Grafana Loki                         │   │
│  │  (Visualization and Querying)                                       │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Log Retention

| Log Type | Retention | Storage Class |
|----------|-----------|---------------|
| Application Logs | 30 days | Hot |
| System Logs | 90 days | Warm |
| Audit Logs | 1 year | Cold |
| Security Logs | 2 years | Glacier |

## Distributed Tracing

### AWS X-Ray Integration

```yaml
# Spring Boot configuration for X-Ray
spring:
  cloud:
    aws:
      xray:
        enabled: true
        sampling:
          strategy: local
          fixed-rate: 0.1
          rules:
            - description: "Always sample errors"
              fixed-rate: 1.0
              predicate:
                type: error
            - description: "Sample 10% of successful requests"
              fixed-rate: 0.1
              predicate:
                type: success
```

### Trace Collection

- **Sampling Rate**: 100% for errors, 10% for others
- **Retention**: 30 days
- **Integration**:
  - Spring Boot applications
  - AWS Lambda functions
  - API Gateway
  - RDS (PostgreSQL)
  - ElastiCache (Redis)

### Trace Visualization

- **X-Ray Console**: AWS native visualization
- **Grafana Tempo**: Open-source alternative
- **Service Maps**: Visualize service dependencies

## Performance Monitoring

### Key Performance Indicators (KPIs)

| KPI | Target | Measurement |
|-----|--------|-------------|
| API Latency (P95) | < 500ms | Prometheus histogram |
| API Latency (P99) | < 1000ms | Prometheus histogram |
| Error Rate | < 0.1% | Prometheus counter |
| Uptime | > 99.9% | Prometheus up metric |
| Sync Queue Processing Time | < 1s | Custom metric |
| Database Query Time (P95) | < 100ms | Prometheus histogram |

### Synthetic Monitoring

- **Uptime Checks**: Every 1 minute from multiple regions
- **API Endpoint Tests**: Every 5 minutes
- **User Journey Tests**: Every 15 minutes
- **Performance Tests**: Daily at off-peak hours

## Capacity Planning

### Resource Forecasting

- **CPU/Memory**: 30-day trend analysis
- **Storage**: Growth rate prediction
- **Database**: Connection pool and query performance trends
- **Network**: Bandwidth usage trends

### Capacity Alerts

- **CPU/Memory**: Predicted to exceed 80% in 7 days
- **Storage**: Predicted to exceed 80% in 14 days
- **Database Connections**: Predicted to exceed 80% in 7 days

## Maintenance

### Regular Tasks

1. **Daily**:
   - Check for failed scrapes
   - Verify alert delivery
   - Review dashboards

2. **Weekly**:
   - Review and update alert rules
   - Test alert notifications
   - Optimize Prometheus queries
   - Clean up old dashboards

3. **Monthly**:
   - Review monitoring coverage
   - Update documentation
   - Test disaster recovery procedures
   - Review costs

### Troubleshooting

#### Common Issues

1. **Prometheus High Memory Usage**:
   - Solution: Increase retention period cleanup frequency
   - Solution: Add more memory
   - Solution: Use remote write to long-term storage

2. **Grafana Slow Dashboards**:
   - Solution: Optimize Prometheus queries
   - Solution: Use recording rules for expensive queries
   - Solution: Add caching

3. **Missing Metrics**:
   - Solution: Check scrape targets
   - Solution: Verify service discovery
   - Solution: Check network connectivity

4. **Alert Storms**:
   - Solution: Adjust alert grouping
   - Solution: Increase thresholds
   - Solution: Add inhibition rules

## Security

### Access Control

- **Prometheus**: Accessible only from within the cluster
- **Grafana**: HTTPS with authentication
- **Alert Manager**: Accessible only from within the cluster
- **Data Sources**: Encrypted connections where available

### Data Protection

- **Encryption at Rest**: All storage encrypted
- **Encryption in Transit**: TLS for all connections
- **Authentication**: Grafana requires authentication
- **Authorization**: Role-based access control (RBAC)

### Audit Logging

- **Prometheus**: Logs all configuration changes
- **Grafana**: Audit logging enabled
- **Alert Manager**: Logs all alert transitions

## Cost Optimization

### Prometheus

- **Retention**: 90 days for high-resolution data
- **Remote Write**: For long-term storage (cheaper)
- **Compaction**: Aggressive compaction settings
- **Scrape Interval**: Balanced between accuracy and cost

### Grafana

- **Caching**: Enable query caching
- **Dashboards**: Limit number of active dashboards
- **Plugins**: Only install necessary plugins

### Overall

- **Estimated Monthly Cost**: $200 - $500
- **Main Cost Drivers**: Storage, compute for Prometheus
- **Optimization**: Use remote write to S3 for long-term storage

## Contact Information

- **Monitoring Team**: monitoring@floktask.com
- **On-Call**: PagerDuty rotation
- **Slack Channel**: #floktask-monitoring
- **Documentation**: https://docs.floktask.com/monitoring

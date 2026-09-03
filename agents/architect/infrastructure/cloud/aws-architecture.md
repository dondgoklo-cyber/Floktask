# AWS Cloud Architecture for Floktask

## Overview

Floktask cloud infrastructure is designed to be **scalable, highly available, and cost-efficient**. We use AWS as the primary cloud provider with a multi-region deployment strategy.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                                AWS Global Infrastructure                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────────┐    ┌─────────────────────┐    ┌─────────────────┐ │
│  │    Primary Region    │    │    Secondary Region   │    │    Tertiary      │ │
│  │   (eu-central-1)     │    │   (eu-west-1)        │    │    Region       │ │
│  │                     │    │                     │    │   (us-east-1)    │ │
│  │  ┌─────────────────┐│    │  ┌─────────────────┐│    │                 │ │
│  │  │  Production      ││    │  │  Disaster        ││    │  ┌─────────────┐ │ │
│  │  │  Environment     ││    │  │  Recovery       ││    │  │  Backup       │ │ │
│  │  │                 ││    │  │  Environment    ││    │  │  Environment │ │ │
│  │  └─────────────────┘│    │  └─────────────────┘│    │  └─────────────┘ │ │
│  └─────────────────────┘    └─────────────────────┘    └─────────────────┘ │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Primary Region: eu-central-1 (Frankfurt)

### Network Infrastructure

#### VPC Design
- **Main VPC**: `10.0.0.0/16`
  - Production subnets: `10.0.1.0/24` - `10.0.6.0/24` (6 AZs)
  - Private subnets: `10.0.10.0/24` - `10.0.15.0/24`
  - Database subnets: `10.0.20.0/24` - `10.0.22.0/24`
  - Reserved subnets: `10.0.100.0/24` - `10.0.255.0/24`

- **VPC Flow Logs**: Enabled for all subnets
- **DNS Support**: Enabled
- **DNS Hostnames**: Enabled

#### Network Components

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            Network Architecture                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────────┐   │
│  │  Internet        │    │  VPC             │    │  On-Premises        │   │
│  │  Gateway         │    │                 │    │  (Future)           │   │
│  └────────┬────────┘    └────────┬────────┘    └──────────┬───────────┘   │
│           │                     │                         │                │
│           │                     │                         │                │
│  ┌────────▼────────┐    ┌────────▼────────┐    ┌────────▼─────────┐   │
│  │  Route 53        │    │  ALB            │    │  VPN Connection   │   │
│  │  (DNS)          │    │  (Load Balancer) │    │  (Site-to-Site)   │   │
│  └────────┬────────┘    └────────┬────────┘    └─────────────────┘   │
│           │                     │                                    │
│           │                     ▼                                    │
│           │  ┌─────────────────────────────────────────────────────────┐   │
│           │  │                    Public Subnets                        │   │
│           │  │  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐    │   │
│           │  │  │  AZ-1   │  │  AZ-2   │  │  AZ-3   │  │  AZ-4   │    │   │
│           │  │  │ 10.0.1 │  │ 10.0.2 │  │ 10.0.3 │  │ 10.0.4 │    │   │
│           │  │  └─────────┘  └─────────┘  └─────────┘  └─────────┘    │   │
│           │  └─────────────────────────────────────────────────────────┘   │
│           │                                                                   │
│           │  ┌─────────────────────────────────────────────────────────┐   │
│           │  │                    Private Subnets                        │   │
│           │  │  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐    │   │
│           │  │  │  AZ-1   │  │  AZ-2   │  │  AZ-3   │  │  AZ-4   │    │   │
│           │  │  │ 10.0.10│  │ 10.0.11│  │ 10.0.12│  │ 10.0.13│    │   │
│           │  │  └─────────┘  └─────────┘  └─────────┘  └─────────┘    │   │
│           │  └─────────────────────────────────────────────────────────┘   │
│           │                                                                   │
│           │  ┌─────────────────────────────────────────────────────────┐   │
│           │  │                    Database Subnets                         │   │
│           │  │  ┌─────────┐  ┌─────────┐  ┌─────────┐                │   │
│           │  │  │  AZ-1   │  │  AZ-2   │  │  AZ-3   │                │   │
│           │  │  │ 10.0.20│  │ 10.0.21│  │ 10.0.22│                │   │
│           │  │  └─────────┘  └─────────┘  └─────────┘                │   │
│           │  └─────────────────────────────────────────────────────────┘   │
│           │                                                                   │
│           └──────────────────────────────────────────────────────────────┘   │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Security Groups

| Security Group | Description | Inbound Rules | Outbound Rules |
|---------------|-------------|---------------|----------------|
| `sg-alb` | ALB Security Group | HTTP:80, HTTPS:443 from 0.0.0.0/0 | All traffic |
| `sg-eks` | EKS Cluster | HTTPS:443 from sg-alb, SSH:22 from bastion | All traffic |
| `sg-rds` | RDS Security Group | PostgreSQL:5432 from sg-eks | All traffic |
| `sg-redis` | Redis Security Group | Redis:6379 from sg-eks | All traffic |
| `sg-bastion` | Bastion Host | SSH:22 from VPN IP | All traffic |

### Compute Resources

#### EKS Cluster (Kubernetes)
- **Cluster Name**: `floktask-production`
- **Kubernetes Version**: 1.28
- **Node Groups**:
  - **On-Demand**: m6i.large (min: 3, max: 10, desired: 5)
  - **Spot**: m6i.large (min: 0, max: 20)
  - **GPU**: g4dn.xlarge (for AI workloads, min: 0, max: 5)

- **Cluster Autoscaler**: Enabled
- **CoreDNS**: Enabled
- **Metrics Server**: Enabled
- **AWS Load Balancer Controller**: Enabled
- **ExternalDNS**: Enabled

#### Node Groups Configuration

```yaml
# On-Demand Node Group
apiVersion: eksctl.io/v1alpha5
kind: ClusterConfig
metadata:
  name: floktask-production
  region: eu-central-1
nodeGroups:
  - name: on-demand
    instanceType: m6i.large
    desiredCapacity: 5
    minSize: 3
    maxSize: 10
    volumeSize: 100
    volumeType: gp3
    labels:
      node-type: on-demand
    tags:
      Environment: production
      NodeType: on-demand

  - name: spot
    instanceType: m6i.large
    desiredCapacity: 0
    minSize: 0
    maxSize: 20
    spot: true
    volumeSize: 100
    volumeType: gp3
    labels:
      node-type: spot
    tags:
      Environment: production
      NodeType: spot

  - name: gpu
    instanceType: g4dn.xlarge
    desiredCapacity: 0
    minSize: 0
    maxSize: 5
    volumeSize: 200
    volumeType: gp3
    labels:
      node-type: gpu
    tags:
      Environment: production
      NodeType: gpu
```

### Container Registry

- **ECR Repositories**:
  - `floktask/api-gateway`
  - `floktask/user-service`
  - `floktask/task-service`
  - `floktask/project-service`
  - `floktask/sync-service`
  - `floktask/ai-service`
  - `floktask/notification-service`
  - `floktask/finance-service`
  - `floktask/habit-service`
  - `floktask/note-service`

- **Image Scanning**: Enabled (ECR Enhanced Scanning)
- **Lifecycle Policies**: Keep last 30 images per repository
- **Cross-Region Replication**: To eu-west-1 and us-east-1

### Database Services

#### Amazon RDS (PostgreSQL)

| Database | Instance Type | Storage | Multi-AZ | Engine Version | Backup |
|----------|---------------|---------|---------|----------------|--------|
| User DB | db.r6g.large | 200 GB | Yes | PostgreSQL 15 | Daily |
| Task DB | db.r6g.large | 500 GB | Yes | PostgreSQL 15 | Daily |
| Project DB | db.r6g.large | 200 GB | Yes | PostgreSQL 15 | Daily |
| Finance DB | db.r6g.large | 300 GB | Yes | PostgreSQL 15 | Daily |
| Habit DB | db.r6g.medium | 100 GB | Yes | PostgreSQL 15 | Daily |
| Note DB | db.r6g.medium | 100 GB | Yes | PostgreSQL 15 | Daily |

**Configuration**:
- **Parameter Group**: Custom with optimized settings
- **Backup Retention**: 30 days
- **Backup Window**: 02:00-04:00 UTC
- **Maintenance Window**: Sun 04:00-06:00 UTC
- **Monitoring**: Enhanced monitoring (60s interval)
- **Performance Insights**: Enabled
- **Encryption**: AES-256

#### Amazon ElastiCache (Redis)

| Cache | Node Type | Nodes | Engine Version | Use Case |
|-------|-----------|-------|----------------|---------|
| Session Cache | cache.r6g.medium | 2 | Redis 7 | User sessions |
| Sync Queue | cache.r6g.medium | 3 | Redis 7 | Sync operations |
| Task Cache | cache.r6g.large | 2 | Redis 7 | Task data |

**Configuration**:
- **Cluster Mode**: Enabled for Sync Queue
- **Encryption**: In-transit and at-rest
- **Backup**: Daily snapshots
- **Maintenance Window**: Same as RDS
- **Monitoring**: Enhanced

### Storage Services

#### Amazon S3

| Bucket | Purpose | Versioning | Encryption | Lifecycle |
|--------|---------|-----------|------------|-----------|
| `floktask-attachments` | File attachments | Enabled | SSE-S3 | 30 days to IA |
| `floktask-avatars` | User avatars | Enabled | SSE-S3 | None |
| `floktask-backups` | Database backups | Enabled | SSE-S3 | 90 days to Glacier |
| `floktask-logs` | Application logs | Enabled | SSE-S3 | 30 days to IA, 90 to Glacier |
| `floktask-assets` | Static assets | Disabled | SSE-S3 | None |

**Configuration**:
- **Block Public Access**: Enabled for all buckets
- **Access Logging**: Enabled
- **Object Lock**: Enabled for backups (30 days retention)
- **Cross-Region Replication**: For critical buckets

#### Amazon EFS
- **File System**: `floktask-shared`
- **Performance Mode**: General Purpose
- **Throughput Mode**: Bursting
- **Lifecycle Management**: Move to IA after 30 days
- **Encryption**: Enabled
- **Backup**: Daily via AWS Backup

### Messaging Services

#### Amazon SQS
- **Sync Queue**: Standard queue for sync operations
- **Notification Queue**: FIFO queue for notifications
- **AI Queue**: Standard queue for AI processing

#### Amazon SNS
- **Topics**:
  - `floktask-notifications`
  - `floktask-alerts`
  - `floktask-sync-events`

### Monitoring and Logging

#### Amazon CloudWatch
- **Metrics**: All AWS services
- **Alarms**: CPU, Memory, Disk, Network, Errors
- **Dashboards**: Custom dashboards for each service
- **Logs**: Centralized logging

#### AWS CloudTrail
- **Trails**: All regions
- **Retention**: 90 days
- **Insights**: Enabled

#### AWS X-Ray
- **Tracing**: Enabled for all microservices
- **Sampling**: 100% for errors, 10% for others
- **Retention**: 30 days

### Security Services

#### AWS IAM
- **Roles**: Least privilege for all services
- **Policies**: Custom policies for each role
- **Users**: MFA required for all human users

#### AWS KMS
- **Customer Master Keys**:
  - `floktask-data-key` for data encryption
  - `floktask-secrets-key` for secrets encryption
  - `floktask-backup-key` for backup encryption

#### AWS Secrets Manager
- **Secrets**:
  - Database credentials
  - API keys
  - JWT secrets
  - OAuth client secrets

#### AWS Certificate Manager
- **Certificates**:
  - `*.floktask.com` (Wildcard)
  - `api.floktask.com`
  - `staging-api.floktask.com`

#### AWS WAF
- **Web ACLs**:
  - Rate limiting
  - SQL injection protection
  - Cross-site scripting protection
  - IP reputation filtering

### CI/CD Pipeline

#### AWS CodePipeline
- **Pipelines**:
  - `floktask-api-gateway-deploy`
  - `floktask-user-service-deploy`
  - `floktask-task-service-deploy`
  - ... (for all services)

#### AWS CodeBuild
- **Projects**:
  - Build and test for each service
  - Docker image building
  - Security scanning

#### AWS CodeDeploy
- **Applications**:
  - EKS deployments via kubectl

### Backup and Disaster Recovery

#### AWS Backup
- **Plans**:
  - **Daily**: All databases, EFS, critical S3 buckets
  - **Weekly**: All EBS volumes, RDS snapshots
  - **Monthly**: Full system backups

#### Disaster Recovery Strategy

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        Disaster Recovery Strategy                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        Primary Region (eu-central-1)                  │   │
│  │                                                                       │   │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────────────────┐  │   │
│  │  │  Production  │    │  Warm       │    │  Backup to S3 +         │  │   │
│  │  │  Environment │    │  Standby    │    │  Cross-Region Replication│  │   │
│  │  │             │    │  Environment │    │                             │  │   │
│  │  └─────────────┘    └─────────────┘    └─────────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                         │
│                                    ▼                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                     Secondary Region (eu-west-1)                       │   │
│  │                                                                       │   │
│  │  ┌───────────────────────────────────────────────────────────────┐ │   │
│  │  │                    Disaster Recovery Environment                   │ │   │
│  │  │                                                                   │ │   │
│  │  │  - RDS Read Replicas (promoted to primary)                       │ │   │
│  │  │  - EKS Cluster (scaled down)                                       │ │   │
│  │  │  - S3 Cross-Region Replication                                    │ │   │
│  │  │  - Route 53 Failover                                               │ │   │
│  │  └───────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                         │
│                                    ▼                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      Tertiary Region (us-east-1)                        │   │
│  │                                                                       │   │
│  │  ┌───────────────────────────────────────────────────────────────┐ │   │
│  │  │                      Backup Environment                             │ │   │
│  │  │                                                                   │ │   │
│  │  │  - S3 Backups (read-only)                                         │ │   │
│  │  │  - RDS Snapshots                                                   │ │   │
│  │  │  - Minimal infrastructure for emergency recovery                 │ │   │
│  │  └───────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Recovery Time Objectives (RTO)**:
- **Critical Services**: < 15 minutes
- **All Services**: < 1 hour
- **Data Loss**: < 5 minutes (for RDS with Multi-AZ)

**Recovery Point Objectives (RPO)**:
- **Databases**: < 5 minutes
- **File Storage**: < 1 hour
- **Configuration**: < 1 hour

## Secondary Region: eu-west-1 (Dublin)

### Warm Standby Environment
- **Purpose**: Disaster recovery and load testing
- **Infrastructure**:
  - EKS Cluster (minimal nodes)
  - RDS Read Replicas
  - ElastiCache (read-only)
  - S3 Cross-Region Replication

### Configuration
- **EKS**: 2 on-demand nodes (m6i.medium)
- **RDS**: Read replicas of all production databases
- **S3**: Cross-region replication from primary
- **Route 53**: Latency-based routing

## Tertiary Region: us-east-1 (Virginia)

### Backup Environment
- **Purpose**: Long-term backups and compliance
- **Infrastructure**:
  - S3 Backups (read-only)
  - RDS Snapshots
  - Minimal compute for emergency recovery

## Cost Optimization

### Cost Saving Strategies

1. **Reserved Instances**: 1-year and 3-year reservations for predictable workloads
2. **Spot Instances**: For stateless workloads (up to 80% savings)
3. **Savings Plans**: Flexible commitment for variable workloads
4. **Auto Scaling**: Scale based on demand
5. **Storage Lifecycle**: Move infrequently accessed data to cheaper storage classes
6. **RDS Proxy**: Reduce database connections
7. **Caching**: Reduce database load with ElastiCache

### Estimated Monthly Costs (Production)

| Service | Estimated Cost | Notes |
|---------|----------------|-------|
| EKS (On-Demand) | $1,500 - $3,000 | 5-10 nodes |
| EKS (Spot) | $300 - $1,000 | 0-20 nodes |
| RDS (PostgreSQL) | $2,000 - $4,000 | 6 instances |
| ElastiCache | $500 - $1,000 | 3 clusters |
| S3 | $200 - $500 | 10 TB storage |
| ECR | $50 - $100 | Image storage |
| EFS | $200 - $500 | 5 TB storage |
| ALB | $50 - $100 | Application Load Balancer |
| CloudFront | $100 - $300 | CDN |
| Route 53 | $50 - $100 | DNS |
| Backup | $200 - $500 | AWS Backup |
| Monitoring | $100 - $300 | CloudWatch, X-Ray |
| **Total** | **$5,250 - $11,700** | Per month |

## Security Architecture

### Network Security

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Security Architecture                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐   │
│  │   Internet       │    │   WAF           │    │   CloudFront    │   │
│  │                 │    │                 │    │                 │   │
│  └────────┬────────┘    └────────┬────────┘    └────────┬────────┘   │
│           │                     │                         │                │
│           │                     ▼                         ▼                │
│           │              ┌─────────────────────────────────┐               │
│           │              │           ALB                     │               │
│           │              │  (HTTPS Termination)              │               │
│           │              └──────────────┬────────────────┘               │
│           │                         │                                   │
│           │                         ▼                                   │
│           │              ┌─────────────────────────────────┐               │
│           │              │         EKS Ingress               │               │
│           │              │    (Network Load Balancer)        │               │
│           │              └──────────────┬────────────────┘               │
│           │                         │                                   │
│           │                         ▼                                   │
│           │  ┌───────────────────────────────────────────────────────┐  │
│           │  │                    EKS Cluster                            │  │
│           │  │  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  │  │
│           │  │  │  Pod    │  │  Pod    │  │  Pod    │  │  Pod    │  │  │
│           │  │  │         │  │         │  │         │  │         │  │  │
│           │  │  └─────────┘  └─────────┘  └─────────┘  └─────────┘  │  │
│           │  └───────────────────────────────────────────────────────┘  │
│           │                                                                   │
│           └───────────────────────────────────────────────────────────┘   │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Security Controls

1. **Network Level**:
   - WAF with OWASP rules
   - Network ACLs for subnet-level filtering
   - Security Groups for instance-level filtering
   - VPN for on-premises connectivity

2. **Application Level**:
   - JWT authentication
   - Role-based access control (RBAC)
   - Rate limiting
   - Input validation

3. **Data Level**:
   - Encryption at rest (AES-256)
   - Encryption in transit (TLS 1.3)
   - Database encryption
   - Secrets management

4. **Monitoring Level**:
   - CloudTrail for API logging
   - CloudWatch for monitoring
   - GuardDuty for threat detection
   - Security Hub for compliance

## Compliance

### Standards
- **GDPR**: General Data Protection Regulation
- **SOC 2**: Service Organization Control 2
- **ISO 27001**: Information Security Management
- **PCI DSS**: Payment Card Industry Data Security Standard (for finance features)

### Data Protection
- **Personal Data**: Encrypted, access controlled
- **Backup**: Encrypted, geographically distributed
- **Retention**: Configurable retention policies
- **Deletion**: Secure deletion procedures

## Monitoring and Alerting

### Key Metrics

| Metric | Threshold | Alert Level |
|--------|-----------|-------------|
| CPU Utilization | > 80% for 5 min | Warning |
| CPU Utilization | > 90% for 5 min | Critical |
| Memory Utilization | > 85% for 5 min | Warning |
| Memory Utilization | > 95% for 5 min | Critical |
| Disk Utilization | > 80% | Warning |
| Disk Utilization | > 90% | Critical |
| Network In | > 1 Gbps | Warning |
| Network Out | > 1 Gbps | Warning |
| HTTP 5xx Errors | > 1% | Warning |
| HTTP 5xx Errors | > 5% | Critical |
| Latency | > 500ms | Warning |
| Latency | > 1000ms | Critical |

### Alert Notifications
- **Email**: For all alerts
- **SMS**: For critical alerts
- **Slack**: For all alerts
- **PagerDuty**: For critical alerts (24/7)

## Deployment Process

### CI/CD Pipeline

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           CI/CD Pipeline                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  Developer → GitHub → CodePipeline → CodeBuild → ECR → EKS → Production         │
│                                    │                                      │
│                                    ▼                                      │
│                              Testing & Scanning                                  │
│                                    │                                      │
│                                    ▼                                      │
│                              Approval Gate                                      │
│                                    │                                      │
│                                    ▼                                      │
│                              Staging Deployment                                  │
│                                    │                                      │
│                                    ▼                                      │
│                              Production Deployment                              │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Deployment Stages

1. **Development**:
   - Branch: `feature/*`
   - Environment: Development EKS cluster
   - Deployment: Automatic on push

2. **Staging**:
   - Branch: `main`
   - Environment: Staging EKS cluster
   - Deployment: Automatic after tests pass

3. **Production**:
   - Branch: `release/*`
   - Environment: Production EKS cluster
   - Deployment: Manual approval required

### Rollback Strategy
- **Automatic**: On deployment failure
- **Manual**: Via CI/CD pipeline or kubectl
- **Database**: Point-in-time recovery from RDS snapshots

## Maintenance Windows

| Activity | Schedule | Duration | Impact |
|----------|----------|----------|--------|
| EKS Upgrades | 1st Sunday of month, 04:00 UTC | 2 hours | Downtime for affected nodes |
| RDS Maintenance | 2nd Sunday of month, 04:00 UTC | 1 hour | Brief connection interruptions |
| Security Patching | 3rd Sunday of month, 04:00 UTC | 2 hours | Rolling updates |
| Emergency Maintenance | As needed | Variable | Minimal impact |

## Contact Information

- **Infrastructure Team**: infrastructure@floktask.com
- **Security Team**: security@floktask.com
- **On-Call**: PagerDuty rotation
- **Status Page**: https://status.floktask.com

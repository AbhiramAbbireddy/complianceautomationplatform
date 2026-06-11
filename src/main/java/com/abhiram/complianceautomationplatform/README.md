# Compliance Automation Platform

A multi-tenant, role-based compliance management backend built with Spring Boot 4. Organizations register on the platform, define compliance requirements, assign them to employees, track completion through a structured approval workflow, and store supporting documents on AWS S3 — with automated email reminders firing at critical deadlines.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Module Breakdown](#module-breakdown)
- [Role Model and Access Control](#role-model-and-access-control)
- [API Reference](#api-reference)
- [Database Design](#database-design)
- [Security Implementation](#security-implementation)
- [Automated Scheduler](#automated-scheduler)
- [Document Storage](#document-storage)
- [Audit Trail](#audit-trail)
- [Exception Handling](#exception-handling)
- [Configuration and Environment](#configuration-and-environment)
- [Running Locally](#running-locally)

---

## Overview

The platform models the real structure of an organization: a Company owns multiple Departments, each Department has a manager and employees under them. Compliance tasks are created by Compliance Managers or Owners, scoped to a company, and pushed down to employees via assignments. The assignment lifecycle — PENDING → IN_PROGRESS → SUBMITTED → VERIFIED — is enforced at the service layer using role checks on every state transition. Department Managers verify the work their team submits. An Auditor role has read-only visibility across all compliances in their company.

Every meaningful action is captured in an immutable audit log via a custom AOP annotation. Email notifications are sent on assignment, on status changes, at the 3-day and 1-day deadlines, and when a task goes overdue — driven by a cron scheduler that runs daily at 9 AM.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Security | Spring Security 6, JWT (jjwt 0.12.6) |
| Persistence | Spring Data JPA, Hibernate |
| Database | PostgreSQL |
| Migrations | Flyway |
| Object Storage | AWS S3 (AWS SDK v2 — software.amazon.awssdk 2.31.72) |
| Email | Spring Mail (JavaMailSender, SMTP/Gmail) |
| API Documentation | SpringDoc OpenAPI 3.0.3 (Swagger UI) |
| Build | Maven |
| Utilities | Lombok, Spring AOP |
| Async | Spring `@Async` with custom thread pool executor |
| Scheduling | Spring `@Scheduled` (cron) |

---

## Architecture

The project follows a domain-driven package structure. Each bounded context (compliance, assignment, document, audit, etc.) is a self-contained module with its own controller, service, repository, entity, and DTO layers. There is no cross-module direct entity dependency — modules that need data from another domain resolve it through the other module's repository.

```
complianceautomationplatform/
├── auth/               # Registration, login, logout
├── security/           # JWT filter, JwtService, SecurityConfig, RevokedToken
├── user/               # User CRUD, team management
├── company/            # Company entity and repository
├── department/         # Department CRUD
├── role/               # Role constants and entity
├── compliance/         # Compliance lifecycle (CRUD, status, scoping)
├── assignment/         # Assign compliances to employees, verify submissions
├── document/           # S3 upload/download for compliance evidence
├── audit/              # @Audit annotation + AOP aspect + log persistence
├── notification/       # EmailService, HTML email templates
├── scheduler/          # ComplianceReminderScheduler (daily cron)
├── dashboard/          # Role-scoped dashboard aggregations
├── exception/          # GlobalExceptionHandler, custom exceptions
└── config/             # SecurityConfig, AsyncConfig, SchedulerConfig, OpenApiConfig

<p align="center"> <img src="docs/architecture.svg" alt="Architecture Diagram" width="1200"> </p>

```

**Request flow:**

```
Client → JwtAuthenticationFilter → SecurityFilterChain
       → Controller (@PreAuthorize role check)
       → Service (business logic + company-scoping)
       → Repository (Spring Data JPA → PostgreSQL)
       → AuditAspect (@AfterReturning on @Audit-annotated methods)
       → EmailService (@Async — non-blocking)
```

---

## Module Breakdown

### Auth

Handles registration, login, and logout. On registration, the caller must specify a role; the `DataInitializer` seeds the five roles at startup. Login returns a JWT. Logout inserts the token's JTI into the `revoked_tokens` table, making the token permanently invalid even before expiry.

### Security

`JwtAuthenticationFilter` intercepts every request, extracts and validates the JWT, checks the JTI against the revoked-token table, and populates the `SecurityContext`. `SecurityConfig` is stateless (no session), CSRF disabled, with all `/api/auth/**` and Swagger endpoints public. Method-level security is enabled via `@EnableMethodSecurity`, so `@PreAuthorize` annotations on controller methods are the primary enforcement point.

### Compliance

Compliance records are always scoped to a company — a user from Company A cannot see or modify compliances belonging to Company B. Statuses are managed as a `ComplianceStatus` enum: `PENDING`, `IN_PROGRESS`, `SUBMITTED`, `VERIFIED`. Frequency is a separate enum: `DAILY`, `WEEKLY`, `MONTHLY`, `QUARTERLY`, `ANNUALLY`.

### Assignment

A Compliance Manager or Owner assigns a compliance to a specific employee. The employee can update their own assignment status to `IN_PROGRESS` or `SUBMITTED`. The Department Manager verifies the assignment, which sets the compliance status to `VERIFIED`. Each assignment also tracks three boolean flags for reminder deduplication (`reminder3DaySent`, `reminder1DaySent`, `overdueReminderSent`).

### Document

Employees upload evidence files (up to 10 MB) against a specific compliance. Files are stored in AWS S3. The `ComplianceDocument` entity stores the S3 key, original file name, MIME type, file size, and a pre-signed URL. Download returns a pre-signed URL generated on demand.

### Dashboard

Three role-specific views are exposed: Owner sees company-wide aggregated compliance stats. Department Manager sees their department's stats plus team member performance. Employee sees only their own assigned tasks and completion rate.

### Notification

`EmailService` sends HTML emails using `JavaMailSender`. All email calls are annotated with `@Async` and executed on a custom thread pool (configured in `AsyncConfig`) so they never block the request thread. `EmailTemplates` holds the HTML bodies for: assignment notification, status update, 3-day reminder, 1-day reminder, and overdue alert.

---

## Role Model and Access Control

Five roles are seeded into the database at startup via `DataInitializer`.

| Role | Description |
|---|---|
| `OWNER` | Registered via `/api/auth/register`. Has full company-wide access. Can create compliance managers, department managers, departments, and employees. |
| `COMPLIANCE_MANAGER` | Created by Owner. Manages compliance records (create, update, delete). Can view all compliances in the company. |
| `DEPARTMENT_MANAGER` | Created by Owner. Manages employees in their department. Assigns compliances to employees. Verifies employee submissions. |
| `EMPLOYEE` | Created by Owner or Department Manager. Receives compliance assignments. Updates task status. Uploads evidence documents. |
| `AUDITOR` | Read-only access to all compliance records in the company. Cannot create, modify, or verify anything. |

Role enforcement uses `@PreAuthorize` at the controller method level. Service methods additionally check that the acting user and the target resource belong to the same company.

---

## API Reference

### Auth — `/api/auth`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/register` | Public | Register a new OWNER and company |
| POST | `/login` | Public | Authenticate and receive JWT |
| POST | `/logout` | Authenticated | Revoke the current JWT |

### Users — `/api/users`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/department-managers` | OWNER | Create a department manager |
| POST | `/employees` | OWNER, DEPARTMENT_MANAGER | Create an employee |
| GET | `/` | Authenticated | List users in the company |
| GET | `/{id}` | Authenticated | Get user by ID |
| GET | `/my-team` | DEPARTMENT_MANAGER | List employees under this manager |

### Compliances — `/api/compliances`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/` | OWNER, COMPLIANCE_MANAGER | Create a compliance task |
| GET | `/` | OWNER, COMPLIANCE_MANAGER, AUDITOR | List all company compliances |
| GET | `/{id}` | OWNER, COMPLIANCE_MANAGER, AUDITOR | Get compliance by ID |
| PUT | `/{id}` | OWNER, COMPLIANCE_MANAGER | Update compliance |
| DELETE | `/{id}` | OWNER, COMPLIANCE_MANAGER | Delete compliance |
| GET | `/my-department` | DEPARTMENT_MANAGER | Compliances scoped to own department |
| GET | `/my-compliances` | EMPLOYEE | Compliances assigned to self |

### Assignments — `/api/assignments`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/` | OWNER, DEPARTMENT_MANAGER | Assign a compliance to an employee |
| GET | `/my-tasks` | EMPLOYEE | Get own assignment list |
| PATCH | `/{id}/status` | EMPLOYEE | Update assignment status |
| PUT | `/{assignmentId}/verify` | DEPARTMENT_MANAGER | Verify a submitted assignment |

### Documents — `/api/documents`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/upload/{complianceId}` | Authenticated | Upload evidence file (multipart) |
| GET | `/compliance/{complianceId}` | Authenticated | List documents for a compliance |
| GET | `/download/{documentId}` | Authenticated | Get pre-signed S3 download URL |

### Dashboard — `/api/dashboard`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/owner` | OWNER | Company-wide compliance summary |
| GET | `/manager` | DEPARTMENT_MANAGER | Department-level summary |
| GET | `/manager/team` | DEPARTMENT_MANAGER | Per-member performance breakdown |
| GET | `/employee` | EMPLOYEE | Personal task summary |

### Departments — `/api/departments`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/` | OWNER | Create a department |
| GET | `/` | Authenticated | List departments in the company |

### Audit Logs — `/api/audit`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/` | OWNER, AUDITOR | Retrieve full audit log for the company |

---

## Database Design

Ten tables across three migration scripts plus the base Hibernate-generated schema.

<p align="center"> <img src="docs/erd.svg" alt="Database ERD" width="1200"> </p>

### Tables

**companies**
```
id            BIGINT PK
name          VARCHAR NOT NULL
email         VARCHAR NOT NULL UNIQUE
created_at    TIMESTAMP
```

**roles**
```
id    BIGINT PK
name  VARCHAR NOT NULL UNIQUE
```

**departments**
```
id          BIGINT PK
name        VARCHAR NOT NULL
company_id  BIGINT FK → companies.id
created_at  TIMESTAMP
```

**users**
```
id            BIGINT PK
name          VARCHAR NOT NULL
email         VARCHAR NOT NULL UNIQUE
password      VARCHAR NOT NULL
enabled       BOOLEAN
company_id    BIGINT FK → companies.id
role_id       BIGINT FK → roles.id
department_id BIGINT FK → departments.id
manager_id    BIGINT FK → users.id (self-referential)
created_at    TIMESTAMP
```

**compliances**
```
id            BIGINT PK
title         VARCHAR
description   VARCHAR(2000)
due_date      DATE
frequency     ENUM (DAILY, WEEKLY, MONTHLY, QUARTERLY, ANNUALLY)
status        ENUM (PENDING, IN_PROGRESS, SUBMITTED, VERIFIED)
company_id    BIGINT FK → companies.id
created_by    BIGINT FK → users.id
department_id BIGINT FK → departments.id
created_at    TIMESTAMP
```

**compliance_assignments**
```
id                    BIGINT PK
compliance_id         BIGINT FK → compliances.id
assigned_to           BIGINT FK → users.id
assigned_by           BIGINT FK → users.id
assigned_at           TIMESTAMP
completed_at          TIMESTAMP
remarks               VARCHAR
reminder_3day_sent    BOOLEAN NOT NULL DEFAULT false
reminder_1day_sent    BOOLEAN NOT NULL DEFAULT false
overdue_reminder_sent BOOLEAN NOT NULL DEFAULT false
```

**compliance_documents** (added in V3 migration)
```
id           BIGINT PK
file_name    VARCHAR
file_type    VARCHAR
file_size    BIGINT
s3_key       VARCHAR
document_url VARCHAR
uploaded_at  TIMESTAMP
compliance_id BIGINT FK → compliances.id
uploaded_by   BIGINT FK → users.id
```

**notifications**
```
id         BIGINT PK
user_id    BIGINT FK → users.id
title      VARCHAR
message    VARCHAR(2000)
is_read    BOOLEAN
created_at TIMESTAMP
```

**audit_logs**
```
id           BIGINT PK
action       VARCHAR
entity_type  VARCHAR
entity_id    BIGINT
performed_by VARCHAR (email string)
performed_at TIMESTAMP
details      VARCHAR(1000)
```

**revoked_tokens** (added in V6 migration)
```
id          BIGINT PK
token_jti   VARCHAR NOT NULL UNIQUE
revoked_at  TIMESTAMP NOT NULL
user_email  VARCHAR NOT NULL
```

### Relationships Summary

- `companies` ← one-to-many → `departments`, `users`, `compliances`
- `departments` ← one-to-many → `users`, `compliances`
- `users` ← self-referential many-to-one → `users` (manager hierarchy)
- `users` ← one-to-many → `compliance_assignments` (as assignee and as assigner)
- `compliances` ← one-to-many → `compliance_assignments`, `compliance_documents`
- `roles` ← many-to-one → `users`

---

## Security Implementation

**JWT generation and validation** — `JwtService` issues tokens signed with an HMAC-SHA key derived from the `JWT_SECRET` environment variable. The token payload embeds the user's email and a `jti` (JWT ID) claim used for revocation tracking.

**Token revocation** — On logout, the token's `jti` is written to `revoked_tokens`. The `JwtAuthenticationFilter` queries this table on every authenticated request. Tokens that appear in the table are rejected regardless of expiry.

**Stateless session** — `SessionCreationPolicy.STATELESS` is configured; no `HttpSession` is created or used.

**Method security** — `@EnableMethodSecurity` activates Spring's `@PreAuthorize` processing. Every controller method that is not purely public declares its role requirements declaratively.

**Company isolation** — Service methods extract the acting user's company from the `SecurityContext` and use it as a filter on all queries, preventing cross-tenant data access.

---

## Automated Scheduler

`ComplianceReminderScheduler` runs on a `0 0 9 * * *` cron (every day at 9:00 AM). It fetches all assignments whose compliance status is not `VERIFIED`, then for each one calculates the days remaining until the due date.

- **3 days remaining** → sends a reminder email and sets `reminder3DaySent = true`
- **1 day remaining** → sends a reminder email and sets `reminder1DaySent = true`
- **Overdue (days < 0)** → sends an overdue alert and sets `overdueReminderSent = true`

The three boolean flags on `ComplianceAssignment` ensure each reminder fires at most once per assignment, preventing duplicate emails if the scheduler re-evaluates the same assignment across multiple days in the same window.

---

## Document Storage

File uploads go through `DocumentService` which:

1. Validates the multipart file
2. Generates a unique S3 key using a UUID prefix
3. Calls the AWS S3 SDK `PutObjectRequest` to stream the file to the configured bucket
4. Persists a `ComplianceDocument` record with the S3 key, file metadata, and compliance reference

Downloads return a pre-signed URL generated by `GetObjectPresignRequest` with a configurable TTL, rather than proxying the file bytes through the application server.

S3 credentials and region are injected via environment variables (`AWS_REGION`, `AWS_BUCKET_NAME`, `AWS_ACCESS_KEY`, `AWS_SECRET_KEY`).

---

## Audit Trail

A custom `@Audit` annotation is applied to service methods that perform state-changing operations:

```java
@Audit(action = "CREATE_COMPLIANCE", entityType = "Compliance")
public ComplianceResponse createCompliance(...) { ... }
```

`AuditAspect` intercepts method returns with `@AfterReturning`, extracts the current user's email from the `SecurityContext` via `SecurityUtils`, and delegates to `AuditLogService` which persists the record. The `audit_logs` table is append-only — there is no delete or update path.

---

## Exception Handling

`GlobalExceptionHandler` annotated with `@RestControllerAdvice` maps all exceptions to structured `ErrorResponse` JSON:

| Exception | HTTP Status |
|---|---|
| `ResourceNotFoundException` | 404 |
| `DuplicateResourceException` | 409 |
| `BusinessException` | 400 |
| `UnauthorizedException` | 403 |
| `MethodArgumentNotValidException` | 400 (field-level validation errors) |
| Unhandled `Exception` | 500 |

---

## Configuration and Environment

All secrets and infrastructure coordinates are externalized as environment variables. No credentials are hardcoded.

| Variable | Purpose |
|---|---|
| `DB_URL` | PostgreSQL JDBC URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | HMAC signing key |
| `JWT_EXPIRATION` | Token lifetime in milliseconds |
| `AWS_REGION` | AWS region for S3 |
| `AWS_BUCKET_NAME` | S3 bucket name |
| `AWS_ACCESS_KEY` | AWS access key ID |
| `AWS_SECRET_KEY` | AWS secret access key |
| `MAIL_USERNAME` | Gmail SMTP username |
| `MAIL_PASSWORD` | Gmail app password |

Flyway is enabled and manages all schema migrations. `spring.jpa.hibernate.ddl-auto=validate` ensures Hibernate only validates the schema against entities and never modifies it in production.

An `application-local.properties` profile is gitignored and used for local overrides.

---

## Running Locally

**Prerequisites:** Java 21, Maven, PostgreSQL, AWS S3 bucket (or LocalStack for local simulation)

```bash
# Clone and enter the project
git clone https://github.com/AbhiramAbbireddy/compliance-automation-platform
cd complianceautomationplatform

# Create the database
psql -U postgres -c "CREATE DATABASE compliance_db;"

# Set environment variables (or populate application-local.properties)
export DB_URL=jdbc:postgresql://localhost:5432/compliance_db
export DB_USERNAME=postgres
export DB_PASSWORD=yourpassword
export JWT_SECRET=your-256-bit-secret
export JWT_EXPIRATION=86400000
export AWS_REGION=ap-south-1
export AWS_BUCKET_NAME=your-bucket
export AWS_ACCESS_KEY=your-key
export AWS_SECRET_KEY=your-secret
export MAIL_USERNAME=you@gmail.com
export MAIL_PASSWORD=your-app-password

# Run
./mvnw spring-boot:run
```

Swagger UI is available at `http://localhost:8080/swagger-ui/index.html` once the application starts.

The `DataInitializer` seeds the five roles (`OWNER`, `COMPLIANCE_MANAGER`, `DEPARTMENT_MANAGER`, `EMPLOYEE`, `AUDITOR`) on first startup. Register an OWNER account first — all other user creation flows from that account.
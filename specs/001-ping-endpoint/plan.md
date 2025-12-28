# Implementation Plan: Ping Endpoint

**Branch**: `001-ping-endpoint` | **Date**: 2025-11-09 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/001-ping-endpoint/spec.md`

## Summary

Create a RESTful API endpoint that accepts a hostname or IP address as a path parameter and executes a network ping operation, returning the result with response time and appropriate error handling. The feature enables developers and monitoring systems to check host reachability through HTTP API calls while ensuring input validation and security against command injection attacks.

## Technical Context

**Language/Version**: Java 17 (Amazon Corretto JDK)  
**Primary Dependencies**: Spring Boot 3.4.4 (Spring Web, Spring Boot Actuator, Spring Boot Validation), SpringDoc OpenAPI  
**Storage**: N/A (no persistence required)  
**Testing**: JUnit 5, Spring Boot Test, MockMvc, Mockito  
**Target Platform**: Linux/macOS/Windows server (containerized via Docker)  
**Project Type**: Single project (REST API)  
**Performance Goals**: <100ms API overhead (excluding actual ping time), handle 10+ concurrent requests  
**Constraints**: <6 seconds total response time (5s ping timeout + 1s processing), input validation must prevent command injection  
**Scale/Scope**: Single endpoint addition to existing DevOps Automation API, minimal complexity

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### I. Single Responsibility Principle (SRP)
- ✅ **PingController**: HTTP request/response mapping only
- ✅ **PingService**: Business logic for executing ping and processing results
- ✅ **PingRequestDTO**: Immutable data transfer for request (host parameter)
- ✅ **PingResponseDTO**: Immutable data transfer for response (status, responseTime, errorMessage)
- ✅ **Custom Exceptions**: Dedicated exception classes for validation errors, timeout errors, unreachable hosts

**Status**: PASS - Each class has single responsibility, no mixed concerns

### II. Dependency Injection & Inversion of Control
- ✅ Service injected into controller via `@Autowired` or constructor injection
- ✅ Configuration values (timeout, ping count) injected via `@Value` from `application.properties`
- ✅ No manual object instantiation with `new` for Spring-managed beans

**Status**: PASS - Proper DI usage throughout

### III. Clean Code Standards
- ✅ Descriptive naming: `executarPing`, `PingResponseDTO`, `validarHost`
- ✅ Methods kept short (<20 lines ideal)
- ✅ Extract regex patterns to constants: `HOST_PATTERN`, `IP_PATTERN`
- ✅ Custom exceptions with meaningful messages
- ✅ Fail-fast validation in controller
- ✅ Immutable DTOs

**Status**: PASS - Follows all clean code standards

### IV. Test-Driven Development (TDD)
- ✅ Unit tests for `PingService` (mocked process execution)
- ✅ Integration tests for `PingController` (MockMvc)
- ✅ Test coverage for:
  - Successful ping scenarios (valid hostname, IP, localhost)
  - Error scenarios (unreachable host, invalid format, timeout)
  - Input validation (injection attempts, empty input, malformed input)
  - Edge cases (long hostnames, IPv6, concurrent requests)

**Status**: PASS - Comprehensive test strategy defined

### V. RESTful API Design
- ✅ GET method for read-only operation: `GET /api/ping/{host}`
- ✅ Resource naming with noun: `/ping` (not `/executePing` or `/doPing`)
- ✅ Status codes: 200 (success), 400 (validation), 408 (timeout), 500 (error)
- ✅ `/api` prefix maintained for consistency
- ✅ SpringDoc OpenAPI annotations: `@Operation`, `@Parameter`
- ✅ Bean Validation: `@Pattern` for host parameter
- ✅ Response via DTO (never expose internal entities)

**Status**: PASS - Fully compliant with REST principles

### Technology Stack Constraints
- ✅ Java 17 with Spring Boot 3.4.4
- ✅ Spring Web, Spring Boot Actuator, Spring Boot Validation
- ✅ Maven build tool
- ✅ JUnit 5, MockMvc, Mockito for testing
- ✅ SpringDoc OpenAPI for documentation
- ✅ No prohibited technologies used

**Status**: PASS - Adheres to mandatory technology stack

### Code Quality & Review Process
- ✅ Pre-commit: `./mvnw clean compile` (no warnings)
- ✅ Pre-commit: `./mvnw test` (all tests pass)
- ✅ Code review for SRP, DI, test coverage, clean code, API design
- ✅ Performance target: <100ms API overhead achieved
- ✅ Build time target: <2 minutes maintained (minimal code addition)

**Status**: PASS - All quality gates satisfied

### Overall Constitution Compliance
**✅ ALL GATES PASSED** - No violations, no complexity tracking needed

## Project Structure

### Documentation (this feature)

```text
specs/001-ping-endpoint/
├── plan.md              # This file (/speckit.plan command output)
├── spec.md              # Feature specification (already created)
├── research.md          # Phase 0 output (process execution approaches)
├── data-model.md        # Phase 1 output (DTOs and exceptions)
├── quickstart.md        # Phase 1 output (how to test the endpoint)
├── contracts/           # Phase 1 output (OpenAPI spec)
│   └── ping-api.yaml
└── checklists/
    └── requirements.md  # Quality checklist (already created)
```

### Source Code (repository root)

```text
src/main/java/br/com/java_api/
├── controller/
│   └── PingController.java          # NEW: REST endpoint for ping
├── service/
│   └── PingService.java              # NEW: Business logic for ping execution
├── dto/
│   ├── PingRequestDTO.java           # NEW: Request DTO (optional, host from path)
│   └── PingResponseDTO.java          # NEW: Response DTO (status, responseTime, errorMessage)
├── exception/
│   ├── HostUnreachableException.java # NEW: Custom exception for unreachable hosts
│   ├── PingTimeoutException.java     # NEW: Custom exception for timeouts
│   └── InvalidHostException.java     # NEW: Custom exception for validation errors
└── config/
    └── (existing configuration)

src/main/resources/
├── application.properties            # UPDATED: Add ping.timeout, ping.count properties
└── (existing resources)

src/test/java/br/com/java_api/
├── controller/
│   └── PingControllerTest.java       # NEW: Integration tests with MockMvc
└── service/
    └── PingServiceTest.java          # NEW: Unit tests with mocked ProcessBuilder
```

**Structure Decision**: Single project structure (Option 1) as this is a REST API backend. The existing Java Spring Boot project structure is maintained with new classes added to appropriate packages following SRP. No new top-level directories needed - ping functionality integrates seamlessly into existing controller/service/dto/exception pattern established by CepController/CepService.



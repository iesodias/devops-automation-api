# Data Model: Ping Endpoint

**Feature**: 001-ping-endpoint  
**Phase**: 1 - Design & Contracts  
**Date**: 2025-11-09

## Overview

The ping endpoint requires minimal data structures as it's a stateless operation with no persistence. The model consists of DTOs for request/response and custom exceptions for error handling.

---

## DTOs (Data Transfer Objects)

### PingRequestDTO

**Purpose**: Encapsulate ping request parameters (optional - host comes from path variable)

**Note**: This DTO may not be necessary since host is passed as path parameter. Included for completeness and future extensibility (e.g., adding configurable timeout per request).

**Fields**:

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `host` | String | Required, Pattern validation | Target hostname or IP address |

**Validation Rules**:
- MUST match hostname or IP address pattern
- MUST NOT contain shell metacharacters (`;`, `|`, `&`, `` ` ``, `$`, `(`, `)`)
- MUST NOT exceed 253 characters (DNS hostname limit)
- MUST NOT be null or empty

**Java Representation**:
```java
package br.com.java_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PingRequestDTO {
    
    @NotBlank(message = "Host cannot be blank")
    @Size(max = 253, message = "Host exceeds maximum length of 253 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9._-]+$",
        message = "Host contains invalid characters"
    )
    private String host;
    
    // Constructors
    public PingRequestDTO() {}
    
    public PingRequestDTO(String host) {
        this.host = host;
    }
    
    // Getters and Setters
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
}
```

---

### PingResponseDTO

**Purpose**: Encapsulate ping operation result with status, timing, and error information

**Fields**:

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `host` | String | Yes | Target host that was pinged |
| `reachable` | boolean | Yes | Whether ping was successful |
| `responseTime` | Double | No | Response time in milliseconds (null if unreachable) |
| `status` | String | Yes | Human-readable status: "SUCCESS", "TIMEOUT", "UNREACHABLE", "INVALID" |
| `message` | String | No | Additional information or error message |
| `timestamp` | String | Yes | ISO 8601 timestamp of when ping was executed |

**State Rules**:
- If `reachable = true`: `responseTime` MUST be present, `status = "SUCCESS"`
- If `reachable = false`: `responseTime` SHOULD be null, `status` indicates failure reason
- `timestamp` MUST always be present in ISO 8601 format (e.g., "2025-11-09T14:30:00Z")

**Java Representation**:
```java
package br.com.java_api.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PingResponseDTO {
    
    private String host;
    private boolean reachable;
    private Double responseTime; // In milliseconds
    private String status;
    private String message;
    private String timestamp;
    
    // Constructors
    public PingResponseDTO() {
        this.timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    public PingResponseDTO(String host, boolean reachable, 
                           Double responseTime, String status, String message) {
        this();
        this.host = host;
        this.reachable = reachable;
        this.responseTime = responseTime;
        this.status = status;
        this.message = message;
    }
    
    // Static factory methods for common scenarios
    public static PingResponseDTO success(String host, double responseTime) {
        return new PingResponseDTO(
            host, 
            true, 
            responseTime, 
            "SUCCESS", 
            String.format("Host %s is reachable (%.2f ms)", host, responseTime)
        );
    }
    
    public static PingResponseDTO timeout(String host) {
        return new PingResponseDTO(
            host, 
            false, 
            null, 
            "TIMEOUT", 
            String.format("Ping to %s timed out after 5 seconds", host)
        );
    }
    
    public static PingResponseDTO unreachable(String host, String reason) {
        return new PingResponseDTO(
            host, 
            false, 
            null, 
            "UNREACHABLE", 
            String.format("Host %s is unreachable: %s", host, reason)
        );
    }
    
    public static PingResponseDTO invalid(String host, String reason) {
        return new PingResponseDTO(
            host, 
            false, 
            null, 
            "INVALID", 
            String.format("Invalid host %s: %s", host, reason)
        );
    }
    
    // Getters and Setters
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    
    public boolean isReachable() { return reachable; }
    public void setReachable(boolean reachable) { this.reachable = reachable; }
    
    public Double getResponseTime() { return responseTime; }
    public void setResponseTime(Double responseTime) { this.responseTime = responseTime; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
```

---

## Custom Exceptions

### Exception Hierarchy

```
RuntimeException
└── PingException (base for all ping-related exceptions)
    ├── InvalidHostException (400 Bad Request)
    ├── HostUnreachableException (404 Not Found or 503 Service Unavailable)
    └── PingTimeoutException (408 Request Timeout)
```

---

### PingException (Base)

**Purpose**: Base exception for all ping-related errors

**Fields**:
- `message`: Error message
- `cause`: Original exception (if any)

**Java Representation**:
```java
package br.com.java_api.exception;

public class PingException extends RuntimeException {
    
    public PingException(String message) {
        super(message);
    }
    
    public PingException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

### InvalidHostException

**Purpose**: Thrown when host parameter fails validation

**HTTP Mapping**: 400 Bad Request

**Common Scenarios**:
- Empty or null host
- Host contains shell metacharacters
- Host exceeds maximum length
- Host doesn't match hostname/IP pattern

**Java Representation**:
```java
package br.com.java_api.exception;

public class InvalidHostException extends PingException {
    
    public InvalidHostException(String message) {
        super(message);
    }
    
    public InvalidHostException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

### HostUnreachableException

**Purpose**: Thrown when ping fails because host is unreachable

**HTTP Mapping**: 503 Service Unavailable (or 404 Not Found)

**Common Scenarios**:
- Host doesn't exist
- Network connectivity issues
- Firewall blocks ICMP
- DNS resolution fails

**Java Representation**:
```java
package br.com.java_api.exception;

public class HostUnreachableException extends PingException {
    
    public HostUnreachableException(String message) {
        super(message);
    }
    
    public HostUnreachableException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

### PingTimeoutException

**Purpose**: Thrown when ping operation times out

**HTTP Mapping**: 408 Request Timeout

**Common Scenarios**:
- Host takes too long to respond
- Network latency exceeds timeout threshold
- Host accepts but doesn't respond to ICMP

**Java Representation**:
```java
package br.com.java_api.exception;

public class PingTimeoutException extends PingException {
    
    public PingTimeoutException(String message) {
        super(message);
    }
    
    public PingTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

## Exception Handling Strategy

### Global Exception Handler

**Purpose**: Centralize exception-to-HTTP mapping following SRP

**Location**: `br.com.java_api.exception.GlobalExceptionHandler` (if not exists) or extend existing

**Mapping Rules**:

| Exception | HTTP Status | Response Body |
|-----------|-------------|---------------|
| `InvalidHostException` | 400 Bad Request | `{"error": "message", "timestamp": "...", "path": "/api/ping/..."}` |
| `PingTimeoutException` | 408 Request Timeout | `{"error": "message", "timestamp": "...", "path": "/api/ping/..."}` |
| `HostUnreachableException` | 503 Service Unavailable | `{"error": "message", "timestamp": "...", "path": "/api/ping/..."}` |
| `PingException` (generic) | 500 Internal Server Error | `{"error": "message", "timestamp": "...", "path": "/api/ping/..."}` |
| `MethodArgumentNotValidException` | 400 Bad Request | `{"error": "Validation failed", "details": [...]}` |

**Java Representation**:
```java
package br.com.java_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class PingExceptionHandler {
    
    @ExceptionHandler(InvalidHostException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidHost(
            InvalidHostException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }
    
    @ExceptionHandler(PingTimeoutException.class)
    public ResponseEntity<Map<String, Object>> handleTimeout(
            PingTimeoutException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.REQUEST_TIMEOUT, request);
    }
    
    @ExceptionHandler(HostUnreachableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreachable(
            HostUnreachableException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE, request);
    }
    
    @ExceptionHandler(PingException.class)
    public ResponseEntity<Map<String, Object>> handleGenericPing(
            PingException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
    
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            String message, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, status);
    }
}
```

---

## Data Flow

```
1. Request:  GET /api/ping/www.google.com
             ↓
2. Controller validates @PathVariable with @Pattern
             ↓
3. Service validates host (additional security layer)
             ↓
4. Service executes ping via ProcessBuilder
             ↓
5. Service parses output → extract response time
             ↓
6. Service creates PingResponseDTO
             ↓ (success path)
7. Controller returns 200 OK with PingResponseDTO
             ↓ (error path)
8. Service throws InvalidHostException / PingTimeoutException / HostUnreachableException
             ↓
9. GlobalExceptionHandler catches and maps to appropriate HTTP status
             ↓
10. Client receives error response with clear message
```

---

## Summary

**Entities Created**:
- `PingRequestDTO` (optional, for future extensibility)
- `PingResponseDTO` (required, immutable with factory methods)
- `PingException` (base exception)
- `InvalidHostException` (validation errors → 400)
- `HostUnreachableException` (unreachable hosts → 503)
- `PingTimeoutException` (timeout errors → 408)
- `PingExceptionHandler` (centralized exception mapping)

**Validation Strategy**: Multi-layer (Bean Validation at controller + business validation at service)

**Immutability**: DTOs are designed with final fields where appropriate, factory methods for common scenarios

**SRP Compliance**: Each class has single responsibility (data transfer, exception representation, exception handling)

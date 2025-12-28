# Research: Ping Endpoint Implementation

**Feature**: 001-ping-endpoint  
**Phase**: 0 - Outline & Research  
**Date**: 2025-11-09

## Research Questions

### 1. How to execute system ping command from Java?

**Decision**: Use `ProcessBuilder` to execute OS-native ping command

**Rationale**:
- Java doesn't have native ICMP support without elevated privileges
- `ProcessBuilder` is the standard Java approach for executing external commands
- Allows platform-specific command construction (different ping syntax on Windows vs Unix)
- Provides timeout control via `Process.waitFor(timeout, TimeUnit)`
- Enables input/output stream handling for parsing ping results

**Alternatives Considered**:
- **InetAddress.isReachable()**: Rejected - unreliable, often blocked by firewalls, doesn't use true ICMP ping, lacks response time accuracy
- **Third-party ICMP libraries (e.g., ICMPPing)**: Rejected - adds external dependency, requires native libraries, increases complexity for educational project
- **Raw socket ICMP**: Rejected - requires root/admin privileges, platform-specific, overly complex for use case

**Implementation Approach**:
```java
// Platform detection
String os = System.getProperty("os.name").toLowerCase();
ProcessBuilder processBuilder;

if (os.contains("win")) {
    // Windows: ping -n 1 -w 5000 hostname
    processBuilder = new ProcessBuilder("ping", "-n", "1", "-w", "5000", host);
} else {
    // Linux/macOS: ping -c 1 -W 5 hostname
    processBuilder = new ProcessBuilder("ping", "-c", "1", "-W", "5", host);
}

Process process = processBuilder.start();
boolean completed = process.waitFor(6, TimeUnit.SECONDS);
```

---

### 2. How to parse ping output to extract response time?

**Decision**: Parse stdout using regex patterns specific to each OS

**Rationale**:
- Ping output format is consistent per platform but varies between Windows/Unix
- Regex extraction is simple, reliable for structured output
- No need for complex parsing libraries
- Response time always appears in predictable pattern

**Alternatives Considered**:
- **JSON output**: Rejected - ping doesn't support JSON output natively
- **Parse entire output as structured data**: Rejected - overkill for extracting one metric
- **Use exit code only**: Rejected - doesn't provide response time information

**Implementation Approach**:
```java
// Unix/Linux/macOS pattern: "time=14.2 ms"
Pattern unixPattern = Pattern.compile("time=([0-9.]+)\\s*ms");

// Windows pattern: "time=14ms" or "time<1ms"
Pattern windowsPattern = Pattern.compile("time[=<]([0-9.]+)ms");

// Read process output
BufferedReader reader = new BufferedReader(
    new InputStreamReader(process.getInputStream())
);
String line;
while ((line = reader.readLine()) != null) {
    Matcher matcher = pattern.matcher(line);
    if (matcher.find()) {
        responseTime = Double.parseDouble(matcher.group(1));
        break;
    }
}
```

---

### 3. How to validate and sanitize hostname/IP input to prevent command injection?

**Decision**: Multi-layer validation with regex whitelist and character sanitization

**Rationale**:
- Command injection is critical security concern when executing OS commands
- Whitelist approach (only allow valid characters) is more secure than blacklist
- Bean Validation at controller + service layer validation provides defense in depth
- Hostname RFC standards define allowed characters clearly

**Alternatives Considered**:
- **Blacklist dangerous characters**: Rejected - impossible to enumerate all attack vectors
- **URL encoding**: Rejected - doesn't prevent all injection attacks
- **Only allow pre-approved hosts**: Rejected - defeats purpose of dynamic ping endpoint

**Implementation Approach**:
```java
// Controller validation with Bean Validation
@Pattern(
    regexp = "^[a-zA-Z0-9._-]+$|^([0-9]{1,3}\\.){3}[0-9]{1,3}$",
    message = "Host must be valid hostname or IP address"
)
String host

// Service layer additional validation
private static final Pattern HOSTNAME_PATTERN = 
    Pattern.compile("^[a-zA-Z0-9.-]+$");
private static final Pattern IPV4_PATTERN = 
    Pattern.compile("^([0-9]{1,3}\\.){3}[0-9]{1,3}$");
private static final Pattern IPV6_PATTERN = 
    Pattern.compile("^([0-9a-fA-F]{0,4}:){2,7}[0-9a-fA-F]{0,4}$");

private void validateHost(String host) {
    if (host == null || host.trim().isEmpty()) {
        throw new InvalidHostException("Host cannot be empty");
    }
    
    // Check length
    if (host.length() > 253) {
        throw new InvalidHostException("Hostname exceeds maximum length");
    }
    
    // Check for dangerous characters
    if (host.contains(";") || host.contains("|") || 
        host.contains("&") || host.contains("`") ||
        host.contains("$") || host.contains("(") || host.contains(")")) {
        throw new InvalidHostException("Host contains invalid characters");
    }
    
    // Verify matches valid pattern
    if (!HOSTNAME_PATTERN.matcher(host).matches() &&
        !IPV4_PATTERN.matcher(host).matches() &&
        !IPV6_PATTERN.matcher(host).matches()) {
        throw new InvalidHostException("Invalid host format");
    }
}
```

---

### 4. How to handle timeouts and unreachable hosts gracefully?

**Decision**: Use Process timeout + custom exception hierarchy

**Rationale**:
- `Process.waitFor(timeout, TimeUnit)` provides built-in timeout mechanism
- Exit codes indicate different failure types (timeout vs unreachable vs network error)
- Custom exceptions allow specific HTTP status code mapping
- Timeout should be slightly longer than ping timeout to account for process overhead

**Alternatives Considered**:
- **Thread interruption**: Rejected - more complex, ProcessBuilder timeout is sufficient
- **Single generic exception**: Rejected - loses distinction between timeout/unreachable/validation errors
- **Retry logic**: Rejected - adds latency, should be caller's responsibility

**Implementation Approach**:
```java
Process process = processBuilder.start();
boolean completed = process.waitFor(6, TimeUnit.SECONDS); // 5s ping + 1s buffer

if (!completed) {
    process.destroyForcibly();
    throw new PingTimeoutException("Ping timed out after 5 seconds");
}

int exitCode = process.exitValue();
if (exitCode != 0) {
    // Exit code 1 typically means host unreachable
    // Exit code 2 typically means network error
    throw new HostUnreachableException("Host is unreachable: " + host);
}
```

---

### 5. Should we support configurable ping count and timeout?

**Decision**: Yes, via `application.properties` with sensible defaults

**Rationale**:
- Different environments may need different timeout values
- Single ping (-c 1) is sufficient for reachability check but configurable count allows flexibility
- Follows Spring Boot configuration best practices
- No need to expose in API (would complicate interface) - configuration is deployment concern

**Alternatives Considered**:
- **Hardcoded values**: Rejected - inflexible, requires code change for different environments
- **Query parameters in API**: Rejected - adds complexity, enables DoS via large timeouts, breaks API simplicity
- **Per-environment profiles**: Considered - but properties are simpler and sufficient

**Implementation Approach**:
```properties
# application.properties
ping.timeout.seconds=5
ping.count=1
ping.deadline.seconds=6
```

```java
@Service
public class PingService {
    @Value("${ping.timeout.seconds:5}")
    private int pingTimeout;
    
    @Value("${ping.count:1}")
    private int pingCount;
    
    @Value("${ping.deadline.seconds:6}")
    private int processDeadline;
}
```

---

### 6. How to handle platform differences (Windows vs Linux/macOS)?

**Decision**: Detect OS at runtime and construct appropriate command

**Rationale**:
- Ping command syntax differs significantly between Windows and Unix-like systems
- System property `os.name` reliably identifies platform
- Single codebase can support all platforms
- Docker deployment will use Linux, but local dev may be Windows/macOS

**Platform-Specific Commands**:

| Platform | Command | Explanation |
|----------|---------|-------------|
| Windows | `ping -n 1 -w 5000 hostname` | `-n 1` = 1 packet, `-w 5000` = 5000ms timeout |
| Linux/macOS | `ping -c 1 -W 5 hostname` | `-c 1` = 1 packet, `-W 5` = 5 second timeout |

**Implementation Approach**:
```java
private ProcessBuilder createPingProcess(String host) {
    String os = System.getProperty("os.name").toLowerCase();
    
    if (os.contains("win")) {
        return new ProcessBuilder(
            "ping", "-n", String.valueOf(pingCount), 
            "-w", String.valueOf(pingTimeout * 1000), 
            host
        );
    } else {
        return new ProcessBuilder(
            "ping", "-c", String.valueOf(pingCount), 
            "-W", String.valueOf(pingTimeout), 
            host
        );
    }
}
```

---

### 7. Logging strategy for ping operations

**Decision**: Use SLF4J (Spring Boot default) with structured logging at service layer

**Rationale**:
- Constitutional requirement (FR-009: log all ping requests)
- SLF4J is already included in Spring Boot
- Service layer is appropriate place (not controller - SRP)
- Include timestamp, target host, result, response time for observability

**Log Levels**:
- `INFO`: Successful pings with response time
- `WARN`: Timeouts and unreachable hosts (expected failures)
- `ERROR`: Unexpected exceptions (process errors, parsing failures)

**Implementation Approach**:
```java
@Service
@Slf4j
public class PingService {
    public PingResponseDTO executarPing(String host) {
        log.info("Executing ping to host: {}", host);
        
        try {
            // ... execute ping ...
            log.info("Ping successful - host: {}, responseTime: {}ms", 
                     host, responseTime);
            return response;
        } catch (PingTimeoutException e) {
            log.warn("Ping timeout - host: {}", host);
            throw e;
        } catch (Exception e) {
            log.error("Ping failed - host: {}, error: {}", 
                      host, e.getMessage(), e);
            throw e;
        }
    }
}
```

---

## Research Summary

All technical questions resolved. Key decisions:

1. **Process Execution**: `ProcessBuilder` with platform-specific commands
2. **Parsing**: Regex extraction of response time from stdout
3. **Security**: Multi-layer validation with whitelist regex patterns
4. **Timeouts**: `Process.waitFor()` with 6-second deadline (5s ping + 1s buffer)
5. **Configuration**: `application.properties` for timeout/count settings
6. **Platform Support**: Runtime OS detection for Windows/Linux/macOS compatibility
7. **Logging**: SLF4J at service layer with INFO/WARN/ERROR levels

**Ready for Phase 1**: Data model definition and contract generation.

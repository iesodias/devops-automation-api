# Quickstart: Ping Endpoint

**Feature**: 001-ping-endpoint  
**Last Updated**: 2025-11-09

## Overview

This guide explains how to test and use the Ping Endpoint feature locally. The endpoint allows you to ping any hostname or IP address via HTTP API.

---

## Prerequisites

- Java 17 installed
- Maven 3.8+ (or use `./mvnw` wrapper)
- Application running on port 8081
- Network connectivity for external hosts

---

## Starting the Application

### Option 1: Maven Wrapper (Recommended)

```bash
# From project root
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

### Option 2: Run JAR

```bash
# Build first
./mvnw clean package -DskipTests

# Run
java -jar target/java-api-0.0.1-SNAPSHOT.jar
```

### Option 3: Docker

```bash
# Build image
docker build -t devops-api .

# Run container
docker run -d -p 8081:8081 --name java-api-container devops-api
```

**Verify application is running:**
```bash
curl http://localhost:8081/actuator/health
# Expected: {"status":"UP"}
```

---

## Testing the Ping Endpoint

### 1. Basic Ping - Valid Hostname

**Request:**
```bash
curl http://localhost:8081/api/ping/www.google.com
```

**Expected Response (200 OK):**
```json
{
  "host": "www.google.com",
  "reachable": true,
  "responseTime": 14.2,
  "status": "SUCCESS",
  "message": "Host www.google.com is reachable (14.20 ms)",
  "timestamp": "2025-11-09T14:30:00"
}
```

---

### 2. Ping IPv4 Address

**Request:**
```bash
curl http://localhost:8081/api/ping/8.8.8.8
```

**Expected Response (200 OK):**
```json
{
  "host": "8.8.8.8",
  "reachable": true,
  "responseTime": 15.5,
  "status": "SUCCESS",
  "message": "Host 8.8.8.8 is reachable (15.50 ms)",
  "timestamp": "2025-11-09T14:30:05"
}
```

---

### 3. Ping Localhost

**Request:**
```bash
curl http://localhost:8081/api/ping/localhost
```

**Expected Response (200 OK):**
```json
{
  "host": "localhost",
  "reachable": true,
  "responseTime": 0.5,
  "status": "SUCCESS",
  "message": "Host localhost is reachable (0.50 ms)",
  "timestamp": "2025-11-09T14:30:10"
}
```

---

### 4. Unreachable Host

**Request:**
```bash
curl http://localhost:8081/api/ping/nonexistent-host-12345.com
```

**Expected Response (503 Service Unavailable):**
```json
{
  "timestamp": "2025-11-09T14:30:15",
  "status": 503,
  "error": "Service Unavailable",
  "message": "Host nonexistent-host-12345.com is unreachable",
  "path": "/api/ping/nonexistent-host-12345.com"
}
```

---

### 5. Invalid Host - Command Injection Attempt

**Request:**
```bash
curl http://localhost:8081/api/ping/host\;rm%20-rf%20/
```

**Expected Response (400 Bad Request):**
```json
{
  "timestamp": "2025-11-09T14:30:20",
  "status": 400,
  "error": "Bad Request",
  "message": "Host contains invalid characters",
  "path": "/api/ping/host;rm -rf /"
}
```

---

### 6. Empty Host

**Request:**
```bash
curl http://localhost:8081/api/ping/
```

**Expected Response (400 Bad Request):**
```json
{
  "timestamp": "2025-11-09T14:30:25",
  "status": 400,
  "error": "Bad Request",
  "message": "Host cannot be blank",
  "path": "/api/ping/"
}
```

---

### 7. Timeout Scenario

**Note**: This requires a host that accepts connections but doesn't respond to ping within 5 seconds. This is rare but can be simulated.

**Request:**
```bash
# Example with a slow/unresponsive host
curl http://localhost:8081/api/ping/slow-host.example.com
```

**Expected Response (408 Request Timeout):**
```json
{
  "timestamp": "2025-11-09T14:30:30",
  "status": 408,
  "error": "Request Timeout",
  "message": "Ping to slow-host.example.com timed out after 5 seconds",
  "path": "/api/ping/slow-host.example.com"
}
```

---

## Using via Web Browser

Simply navigate to:
```
http://localhost:8081/api/ping/www.google.com
```

The browser will display the JSON response.

---

## Using Swagger UI

1. Navigate to: http://localhost:8081/swagger-ui.html
2. Find the **Ping** section
3. Click on `GET /api/ping/{host}`
4. Click **Try it out**
5. Enter a host (e.g., `www.google.com`)
6. Click **Execute**
7. View the response

---

## Response Time Expectations

| Target | Typical Response Time |
|--------|----------------------|
| localhost | < 1 ms |
| Local network hosts | 1-10 ms |
| Public internet hosts | 10-100 ms |
| Slow/distant hosts | 100-1000 ms |
| Timeout threshold | 5000 ms (5 seconds) |

---

## Common Issues & Troubleshooting

### Issue: "Connection refused" on localhost:8081

**Solution:**
```bash
# Check if application is running
ps aux | grep java

# Check if port 8081 is in use
lsof -i :8081  # macOS/Linux
netstat -ano | findstr :8081  # Windows

# Restart application
./mvnw spring-boot:run
```

---

### Issue: All pings return "unreachable"

**Possible Causes:**
1. **No internet connection**: Test with `curl http://localhost:8081/api/ping/localhost`
2. **Firewall blocking outbound ICMP**: Check firewall settings
3. **Application doesn't have permission to execute ping**: On some systems, ping requires elevated privileges

**Solution:**
```bash
# Test manually
ping -c 1 www.google.com  # Should work outside the app

# Check application logs
tail -f logs/spring.log  # Or wherever logs are configured
```

---

### Issue: Timeout on all requests (even localhost)

**Possible Causes:**
1. **Ping command not found**: Missing on system PATH
2. **ProcessBuilder configuration issue**: Check logs for errors

**Solution:**
```bash
# Verify ping is available
which ping  # macOS/Linux
where ping  # Windows

# Check application logs for detailed error messages
```

---

### Issue: 400 Bad Request on valid hostnames

**Check:**
- Hostname doesn't contain special characters
- Hostname is <= 253 characters
- No typos in hostname

**Valid Examples:**
- ✅ `www.google.com`
- ✅ `example-site.io`
- ✅ `8.8.8.8`
- ✅ `localhost`

**Invalid Examples:**
- ❌ `host;command` (contains semicolon)
- ❌ `host|command` (contains pipe)
- ❌ `host&command` (contains ampersand)
- ❌ (empty string)

---

## Performance Testing

### Single Request Latency

```bash
# Measure total request time including ping
time curl http://localhost:8081/api/ping/8.8.8.8
```

**Expected**: < 100ms API overhead + actual ping time

---

### Concurrent Requests

```bash
# Using GNU Parallel (install with: brew install parallel)
seq 10 | parallel -j 10 curl -s http://localhost:8081/api/ping/www.google.com

# Or using Apache Bench
ab -n 100 -c 10 http://localhost:8081/api/ping/8.8.8.8
```

**Expected**: System should handle 10+ concurrent pings without degradation

---

## Configuration

The ping endpoint behavior can be configured in `application.properties`:

```properties
# Ping timeout in seconds (default: 5)
ping.timeout.seconds=5

# Number of ping packets to send (default: 1)
ping.count=1

# Process deadline in seconds (ping timeout + buffer, default: 6)
ping.deadline.seconds=6
```

**To modify:**
1. Edit `src/main/resources/application.properties`
2. Restart the application

---

## Next Steps

After verifying the endpoint works:

1. **Run tests**: `./mvnw test`
2. **Check Swagger documentation**: http://localhost:8081/swagger-ui.html
3. **View API documentation**: http://localhost:8081/v3/api-docs
4. **Check health status**: http://localhost:8081/actuator/health
5. **Integrate into monitoring system**: Use endpoint for health checks

---

## Support

If you encounter issues:

1. Check application logs
2. Verify prerequisites are met
3. Test with `localhost` first (simplest case)
4. Open GitHub issue with logs and error details

---

## Example Test Script

Save this as `test-ping-endpoint.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:8081/api/ping"

echo "Testing Ping Endpoint..."
echo ""

echo "1. Testing localhost..."
curl -s "$BASE_URL/localhost" | jq '.'
echo ""

echo "2. Testing Google DNS..."
curl -s "$BASE_URL/8.8.8.8" | jq '.'
echo ""

echo "3. Testing invalid host..."
curl -s "$BASE_URL/nonexistent-host-xyz.com" | jq '.'
echo ""

echo "4. Testing injection attempt (should fail)..."
curl -s "$BASE_URL/host;rm%20-rf%20/" | jq '.'
echo ""

echo "All tests complete!"
```

Run with: `chmod +x test-ping-endpoint.sh && ./test-ping-endpoint.sh`

**Note**: Requires `jq` for JSON formatting (`brew install jq` on macOS)

# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Development
- **Start application locally**: `./mvnw spring-boot:run` (runs on port 8081)
- **Build application**: `./mvnw clean package`
- **Run tests**: `./mvnw test`
- **Run single test**: `./mvnw test -Dtest=<TestClassName>`
- **Skip tests during build**: `./mvnw clean package -DskipTests`
- **Debug mode**: `./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"`
- **Run with profile**: `./mvnw spring-boot:run -Dspring.profiles.active=dev`

### Docker
- **Build Docker image**: `docker build -t devops-api .`
- **Run container**: `docker run -d -p 8081:8081 --name java-api-container devops-api`
- **View logs**: `docker logs java-api-container`
- **Stop and remove**: `docker stop java-api-container && docker rm java-api-container`

### API Testing
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **Health check**: http://localhost:8081/actuator/health
- **Visual status page**: http://localhost:8081/status
- **Test hello endpoint**: `curl http://localhost:8081/api/hello`
- **Test status endpoint**: `curl http://localhost:8081/api/status`
- **Test CEP lookup**: `curl http://localhost:8081/api/cep/30350210`

## Architecture

This is a Spring Boot application for DevOps automation learning, organized with the following structure:

### Controllers (`controller/`)
- **REST Controllers**:
  - StatusRestController - `/api/status` with detailed system metrics
  - CepController - `/api/cep/{cep}` for address lookup
  - HelloController - `/api/hello`, `/api/info`, `/api/getContainerName`, `/api/json`
- **Web Controllers**: HomeController, StatusController, CepPageController for HTML pages
- **Error Handling**: CustomErrorController for custom error pages

### Services (`service/`)
- **CepService**: Integrates with ViaCEP API for Brazilian address lookup by CEP (postal code)
  - Validates CEP format (must be exactly 8 digits)
  - Automatically strips non-numeric characters
  - Throws CepNotFoundException for invalid CEPs
- **SystemInfoService**: Provides system and container information (hostname, memory, CPU, etc.)

### DTOs (`dto/`)
- **CepResponseDTO**: Data transfer object for CEP/address responses (cep, logradouro, bairro, localidade, uf, etc.)
- **ContainerInfoDTO**: Container and system information response

### Exception Handling (`exception/`)
- **GlobalExceptionHandler**: Centralized exception handling with proper HTTP status codes
- **Custom Exceptions**:
  - CepNotFoundException (404) - CEP not found in ViaCEP
  - ExternalApiException (502) - ViaCEP API communication errors

### Configuration (`config/`)
- **RestTemplateConfig**: HTTP client configuration for external API calls
  - Connection timeout: 5 seconds
  - Read timeout: 10 seconds

## Key Technologies
- **Spring Boot 3.4.4** with Java 17
- **Spring Web** for REST endpoints
- **Thymeleaf** for HTML templates
- **SpringDoc OpenAPI** for API documentation
- **Spring Boot Actuator** for health checks
- **Spring Boot DevTools** for development

## Application Details
- **Port**: 8081 (configured in application.properties)
- **External API**: Integrates with ViaCEP API (https://viacep.com.br/ws/) for Brazilian postal code lookup
- **Templates**: Uses Thymeleaf templates in `src/main/resources/templates/`
- **Static Resources**: Located in `src/main/resources/static/`
- **Multi-stage Docker Build**: Uses Maven 3.9.6 for build and Amazon Corretto 17 Alpine for runtime

## Configuration
Key settings in application.properties:
- **server.port**: 8081
- **api.viacep.url**: https://viacep.com.br/ws/
- **api.connection.timeout**: 5000ms
- **api.read.timeout**: 10000ms
- **management.endpoints.web.exposure.include**: health, info
- **app.version**: 1.0.0

## API Endpoints

### REST API (`/api/*`)
- **GET /api/hello** - Returns "Ola, Mundo da API!"
- **GET /api/status** - Detailed system status with metrics (hostname, memory, CPU, etc.)
- **GET /api/info** - Container and environment information
- **GET /api/getContainerName** - Returns hostname/container name
- **GET /api/json** - Static JSON example response
- **GET /api/cep/{cep}** - Address lookup by Brazilian postal code (CEP must be 8 digits)

### Web Pages
- **GET /** - Home page with navigation buttons
- **GET /status** - Visual dashboard showing system status
- **GET /cep** - Form-based interface for CEP lookup

### Monitoring
- **GET /actuator/health** - Application health check
- **GET /actuator/info** - Application information
- **GET /swagger-ui.html** - Interactive API documentation

## Troubleshooting

### Port 8081 Already in Use
```bash
# Find process using port 8081
lsof -i :8081  # macOS/Linux
netstat -ano | findstr :8081  # Windows

# Kill the process
kill -9 <PID>  # macOS/Linux
taskkill /PID <PID> /F  # Windows
```

### CEP API Errors
- CEP must be exactly 8 digits (non-numeric characters are automatically stripped)
- Returns 404 if CEP not found in ViaCEP database
- Returns 502 if ViaCEP API is unreachable
- Test ViaCEP directly: `curl https://viacep.com.br/ws/30350210/json/`
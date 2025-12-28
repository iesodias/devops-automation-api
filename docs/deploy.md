# 🚢 Deploy e Build

Guia completo para build, deploy e configuração da **DevOps Automation API** em diferentes ambientes.

---

## 📋 Índice

1. [Build Local](#-build-local)
2. [Docker Build](#-docker-build)
3. [Deploy Local](#-deploy-local)
4. [Deploy com Docker Compose](#-deploy-com-docker-compose)
5. [Deploy no Kubernetes](#-deploy-no-kubernetes)
6. [Variáveis de Ambiente](#-variáveis-de-ambiente)
7. [Configurações de Produção](#-configurações-de-produção)
8. [CI/CD](#-cicd)
9. [Monitoramento](#-monitoramento)
10. [Troubleshooting](#-troubleshooting)

---

## 🏗️ Build Local

### Pré-requisitos

- ☕ Java 17 (Amazon Corretto JDK recomendado)
- 🐘 Maven 3.9+ (ou usar Maven Wrapper)

### Build com Maven Wrapper

```bash
# Navegue até o diretório do projeto
cd java-api

# Build completo (com testes)
./mvnw clean package

# Build sem executar testes (mais rápido)
./mvnw clean package -DskipTests

# Build com testes específicos
./mvnw clean package -Dtest=HelloControllerTest

# Build em modo verbose
./mvnw clean package -X
```

### Artefatos Gerados

Após o build, os seguintes artefatos são gerados em `target/`:

```
target/
├── java-api-0.0.1-SNAPSHOT.jar          # JAR executável principal
├── java-api-0.0.1-SNAPSHOT.jar.original # JAR original (sem deps)
├── classes/                              # Classes compiladas
├── test-classes/                         # Testes compilados
└── surefire-reports/                     # Relatórios de teste
```

### Executar o JAR Localmente

```bash
# Executar com configurações padrão
java -jar target/java-api-0.0.1-SNAPSHOT.jar

# Executar com profile específico
java -jar -Dspring.profiles.active=prod target/java-api-0.0.1-SNAPSHOT.jar

# Executar com porta customizada
java -jar -Dserver.port=9090 target/java-api-0.0.1-SNAPSHOT.jar

# Executar com opções de memória
java -Xmx512m -Xms256m -jar target/java-api-0.0.1-SNAPSHOT.jar
```

---

## 🐳 Docker Build

### Dockerfile Multi-stage

O projeto utiliza um Dockerfile multi-stage para otimizar o tamanho da imagem:

```dockerfile
# Stage 1: Build
FROM maven:3.9.6-amazoncorretto-17 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=builder /build/target/java-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Vantagens:**
- ✅ Imagem final menor (~200MB vs ~800MB)
- ✅ Build isolado e reproduzível
- ✅ Não expõe código-fonte
- ✅ Segurança aprimorada

### Build da Imagem Docker

```bash
# Build básico
docker build -t devops-api:latest .

# Build com tag de versão
docker build -t devops-api:1.0.0 .

# Build com nome de registry
docker build -t myregistry.com/devops-api:latest .

# Build sem cache (forçar rebuild completo)
docker build --no-cache -t devops-api:latest .

# Build com build args
docker build \
  --build-arg MAVEN_OPTS="-Xmx1024m" \
  -t devops-api:latest .
```

### Verificar a Imagem

```bash
# Listar imagens
docker images | grep devops-api

# Inspecionar a imagem
docker inspect devops-api:latest

# Ver histórico de layers
docker history devops-api:latest

# Ver tamanho da imagem
docker images devops-api:latest --format "{{.Size}}"
```

### Executar Container

```bash
# Executar em modo interativo (foreground)
docker run -it -p 8081:8081 devops-api:latest

# Executar em modo daemon (background)
docker run -d \
  --name java-api-container \
  -p 8081:8081 \
  devops-api:latest

# Executar com variáveis de ambiente
docker run -d \
  --name java-api-container \
  -p 8081:8081 \
  -e SERVER_PORT=8081 \
  -e API_CONNECTION_TIMEOUT=10000 \
  devops-api:latest

# Executar com volume para logs
docker run -d \
  --name java-api-container \
  -p 8081:8081 \
  -v $(pwd)/logs:/app/logs \
  devops-api:latest

# Executar com limitação de recursos
docker run -d \
  --name java-api-container \
  -p 8081:8081 \
  --memory="512m" \
  --cpus="1.0" \
  devops-api:latest
```

### Gerenciar Container

```bash
# Ver logs
docker logs java-api-container

# Ver logs em tempo real
docker logs -f java-api-container

# Ver últimas 100 linhas
docker logs --tail 100 java-api-container

# Entrar no container
docker exec -it java-api-container sh

# Verificar status
docker ps | grep java-api-container

# Ver uso de recursos
docker stats java-api-container

# Parar container
docker stop java-api-container

# Remover container
docker rm java-api-container

# Parar e remover em um comando
docker stop java-api-container && docker rm java-api-container
```

---

## 🐋 Deploy com Docker Compose

### docker-compose.yml

Crie um arquivo `docker-compose.yml` na raiz do projeto:

```yaml
version: '3.8'

services:
  java-api:
    build:
      context: .
      dockerfile: Dockerfile
    image: devops-api:latest
    container_name: java-api-container
    ports:
      - "8081:8081"
    environment:
      - SERVER_PORT=8081
      - API_VIACEP_URL=https://viacep.com.br/ws/
      - API_CONNECTION_TIMEOUT=5000
      - API_READ_TIMEOUT=10000
      - SPRING_PROFILES_ACTIVE=prod
    volumes:
      - ./logs:/app/logs
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    networks:
      - devops-network

networks:
  devops-network:
    driver: bridge
```

### Comandos Docker Compose

```bash
# Subir serviços
docker-compose up -d

# Build e subir serviços
docker-compose up -d --build

# Ver logs
docker-compose logs -f

# Ver logs de um serviço específico
docker-compose logs -f java-api

# Ver status dos serviços
docker-compose ps

# Parar serviços
docker-compose stop

# Parar e remover containers
docker-compose down

# Parar e remover containers + volumes
docker-compose down -v

# Recriar containers
docker-compose up -d --force-recreate

# Escalar serviços (múltiplas instâncias)
docker-compose up -d --scale java-api=3
```

---

## ☸️ Deploy no Kubernetes

### Deployment YAML

Crie `k8s/deployment.yaml`:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: java-api-deployment
  labels:
    app: java-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: java-api
  template:
    metadata:
      labels:
        app: java-api
    spec:
      containers:
      - name: java-api
        image: devops-api:latest
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 8081
        env:
        - name: SERVER_PORT
          value: "8081"
        - name: API_VIACEP_URL
          value: "https://viacep.com.br/ws/"
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 20
          periodSeconds: 5
          timeoutSeconds: 3
          failureThreshold: 3
```

### Service YAML

Crie `k8s/service.yaml`:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: java-api-service
spec:
  type: LoadBalancer
  selector:
    app: java-api
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8081
    name: http
```

### ConfigMap YAML

Crie `k8s/configmap.yaml`:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: java-api-config
data:
  SERVER_PORT: "8081"
  API_VIACEP_URL: "https://viacep.com.br/ws/"
  API_CONNECTION_TIMEOUT: "5000"
  API_READ_TIMEOUT: "10000"
```

### Aplicar no Kubernetes

```bash
# Criar namespace
kubectl create namespace devops-api

# Aplicar ConfigMap
kubectl apply -f k8s/configmap.yaml -n devops-api

# Aplicar Deployment
kubectl apply -f k8s/deployment.yaml -n devops-api

# Aplicar Service
kubectl apply -f k8s/service.yaml -n devops-api

# Verificar status
kubectl get pods -n devops-api
kubectl get svc -n devops-api
kubectl get deployments -n devops-api

# Ver logs de um pod
kubectl logs -f <pod-name> -n devops-api

# Descrever pod
kubectl describe pod <pod-name> -n devops-api

# Escalar deployment
kubectl scale deployment java-api-deployment --replicas=5 -n devops-api

# Atualizar imagem (rolling update)
kubectl set image deployment/java-api-deployment \
  java-api=devops-api:2.0.0 -n devops-api

# Ver status do rollout
kubectl rollout status deployment/java-api-deployment -n devops-api

# Rollback se necessário
kubectl rollout undo deployment/java-api-deployment -n devops-api
```

---

## 🔐 Variáveis de Ambiente

### Variáveis Disponíveis

| Variável | Padrão | Descrição | Exemplo |
|----------|--------|-----------|---------|
| `SERVER_PORT` | `8081` | Porta do servidor | `8081` |
| `SPRING_PROFILES_ACTIVE` | - | Profile ativo | `prod`, `dev` |
| `API_VIACEP_URL` | `https://viacep.com.br/ws/` | URL base da API ViaCEP | URL completa |
| `API_CONNECTION_TIMEOUT` | `5000` | Timeout de conexão (ms) | `5000` |
| `API_READ_TIMEOUT` | `10000` | Timeout de leitura (ms) | `10000` |
| `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE` | `health,info` | Endpoints Actuator expostos | `health,metrics` |

### Configurar Variáveis

#### Linux/Mac (Shell)

```bash
export SERVER_PORT=9090
export SPRING_PROFILES_ACTIVE=prod
export API_CONNECTION_TIMEOUT=10000
```

#### Windows (CMD)

```cmd
set SERVER_PORT=9090
set SPRING_PROFILES_ACTIVE=prod
set API_CONNECTION_TIMEOUT=10000
```

#### Windows (PowerShell)

```powershell
$env:SERVER_PORT="9090"
$env:SPRING_PROFILES_ACTIVE="prod"
$env:API_CONNECTION_TIMEOUT="10000"
```

#### Docker

```bash
docker run -d \
  -e SERVER_PORT=9090 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e API_CONNECTION_TIMEOUT=10000 \
  devops-api:latest
```

#### Kubernetes

```yaml
env:
- name: SERVER_PORT
  value: "9090"
- name: SPRING_PROFILES_ACTIVE
  value: "prod"
```

---

## ⚙️ Configurações de Produção

### application-prod.properties

Crie `src/main/resources/application-prod.properties`:

```properties
# Server
server.port=8081
server.compression.enabled=true
server.compression.mime-types=application/json,application/xml,text/html,text/xml,text/plain

# Actuator (restringir em produção)
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
management.metrics.export.prometheus.enabled=true

# Logging
logging.level.root=INFO
logging.level.br.com.java_api=INFO
logging.file.name=/app/logs/application.log
logging.file.max-size=10MB
logging.file.max-history=30

# API externa
api.viacep.url=https://viacep.com.br/ws/
api.connection.timeout=10000
api.read.timeout=20000

# Security headers
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=true
```

### JVM Options para Produção

```bash
java -jar \
  -Xms256m \
  -Xmx512m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/app/logs/heapdump.hprof \
  -Dspring.profiles.active=prod \
  -Djava.security.egd=file:/dev/./urandom \
  target/java-api-0.0.1-SNAPSHOT.jar
```

### Dockerfile Otimizado para Produção

```dockerfile
FROM maven:3.9.6-amazoncorretto-17 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:17-alpine
RUN apk add --no-cache curl
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=builder /build/target/java-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1
ENTRYPOINT ["java", \
  "-Xms256m", \
  "-Xmx512m", \
  "-XX:+UseG1GC", \
  "-Dspring.profiles.active=prod", \
  "-jar", \
  "app.jar"]
```

---

## 🔄 CI/CD

### GitHub Actions Workflow

Crie `.github/workflows/ci-cd.yml`:

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v3
      
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'corretto'
        
    - name: Cache Maven packages
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        
    - name: Build with Maven
      run: ./mvnw clean package -DskipTests
      
    - name: Run tests
      run: ./mvnw test
      
    - name: Build Docker image
      run: docker build -t devops-api:${{ github.sha }} .
      
    - name: Push to Registry
      if: github.ref == 'refs/heads/main'
      run: |
        echo ${{ secrets.DOCKER_PASSWORD }} | docker login -u ${{ secrets.DOCKER_USERNAME }} --password-stdin
        docker tag devops-api:${{ github.sha }} ${{ secrets.DOCKER_USERNAME }}/devops-api:latest
        docker push ${{ secrets.DOCKER_USERNAME }}/devops-api:latest
```

### GitLab CI/CD

Crie `.gitlab-ci.yml`:

```yaml
stages:
  - build
  - test
  - docker
  - deploy

variables:
  MAVEN_OPTS: "-Dmaven.repo.local=.m2/repository"

build:
  stage: build
  image: maven:3.9.6-amazoncorretto-17
  script:
    - ./mvnw clean package -DskipTests
  artifacts:
    paths:
      - target/*.jar
    expire_in: 1 week

test:
  stage: test
  image: maven:3.9.6-amazoncorretto-17
  script:
    - ./mvnw test
  coverage: '/Total.*?([0-9]{1,3})%/'

docker:
  stage: docker
  image: docker:latest
  services:
    - docker:dind
  script:
    - docker build -t $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA .
    - docker tag $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA $CI_REGISTRY_IMAGE:latest
    - docker push $CI_REGISTRY_IMAGE:latest
  only:
    - main
```

---

## 📊 Monitoramento

### Prometheus Metrics

Adicione ao `pom.xml`:

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

Configure em `application.properties`:

```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.metrics.export.prometheus.enabled=true
management.endpoint.prometheus.enabled=true
```

Acesse métricas: `http://localhost:8081/actuator/prometheus`

### Grafana Dashboard

Importe o dashboard ID **4701** (JVM Micrometer) no Grafana.

### Logging

```bash
# Ver logs em tempo real
tail -f logs/application.log

# Buscar erros
grep ERROR logs/application.log

# Análise de performance
grep "duration" logs/application.log | awk '{print $NF}'
```

---

## 🐛 Troubleshooting

### Build falha por falta de memória

```bash
# Aumentar memória do Maven
export MAVEN_OPTS="-Xmx1024m"
./mvnw clean package
```

### Docker build muito lento

```bash
# Usar cache do Docker
docker build --cache-from devops-api:latest -t devops-api:latest .

# Ou .dockerignore para excluir arquivos desnecessários
echo "target/" >> .dockerignore
echo ".git/" >> .dockerignore
```

### Container não inicia

```bash
# Verificar logs
docker logs java-api-container

# Verificar se a porta está disponível
lsof -i :8081

# Entrar no container para debug
docker exec -it java-api-container sh
```

### Health check falha no Kubernetes

```bash
# Aumentar initialDelaySeconds
kubectl edit deployment java-api-deployment

# Verificar logs do pod
kubectl logs <pod-name>

# Descrever pod para ver eventos
kubectl describe pod <pod-name>
```

---

## 📚 Referências

- [Spring Boot Deployment](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Kubernetes Deployment Guide](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)

---

## 🔗 Links Relacionados

- 🏠 [Home](index.md) - Página inicial
- 📘 [API Reference](api.md) - Documentação da API
- 🏗️ [Arquitetura](arquitetura.md) - Arquitetura do sistema

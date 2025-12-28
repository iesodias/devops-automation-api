# DevOps Automation API

Bem-vindo à documentação oficial da **DevOps Automation API**!

## Visão Geral

A DevOps Automation API é uma aplicação Java REST construída com Spring Boot, desenvolvida especialmente para ensinar e demonstrar práticas modernas de automação DevOps, incluindo:

- ✅ Desenvolvimento de APIs REST com qualidade
- ✅ Integração contínua e entrega contínua (CI/CD)
- ✅ Containerização com Docker
- ✅ Orquestração com Kubernetes
- ✅ Monitoramento e health checks
- ✅ Documentação automática de APIs

Esta API serve como projeto prático para o curso **DevOps Automation**, oferecendo endpoints funcionais que demonstram integração com APIs externas, gerenciamento de recursos estáticos e boas práticas de desenvolvimento.

---

## Objetivo

Fornecer uma aplicação real e funcional que possa ser usada para:

1. **Aprender** práticas de DevOps hands-on
2. **Experimentar** pipelines CI/CD
3. **Praticar** deployment em diferentes ambientes
4. **Entender** monitoramento e observabilidade
5. **Implementar** testes automatizados

---

## Funcionalidades Principais

### Interface Web
- **Página inicial** com navegação intuitiva
- **Dashboard de status** visual com métricas do sistema
- **Formulário de consulta CEP** interativo

### API REST
- **Endpoints básicos** (hello, status, info)
- **Integração com ViaCEP** para consulta de endereços
- **Informações de container** e ambiente
- **Recursos estáticos JSON**

### Monitoramento
- **Spring Boot Actuator** para health checks
- **Métricas do sistema** (CPU, memória, disco)
- **OpenAPI/Swagger** para documentação interativa

---

## Tecnologias Utilizadas

| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| **Java** | 17 (Amazon Corretto) | Linguagem de programação |
| **Spring Boot** | 3.4.4 | Framework principal |
| **Maven** | 3.9.6+ | Gerenciador de dependências |
| **Thymeleaf** | - | Engine de templates HTML |
| **Docker** | - | Containerização |
| **SpringDoc OpenAPI** | 2.5.0 | Documentação automática |
| **Actuator** | - | Monitoramento e health checks |
| **RestTemplate** | - | Cliente HTTP |
| **JUnit 5** | - | Testes unitários |

---

## Instalação e Execução

### Pré-requisitos

- **Java 17** (Amazon Corretto JDK recomendado)
- **Maven 3.9+** (ou usar o Maven Wrapper incluído)
- **Docker** (opcional, para execução containerizada)
- **Git** para clonar o repositório

### Opção 1: Execução Local (Desenvolvimento)

Esta é a forma recomendada para desenvolvimento local com hot-reload.

```bash
# 1. Clone o repositório
git clone https://github.com/iesodias/devops-automation-api.git
cd devops-automation-api

# 2. Execute a aplicação usando Maven Wrapper
./mvnw spring-boot:run

# Ou no Windows:
mvnw.cmd spring-boot:run
```

**✅ Aplicação disponível em:** `http://localhost:8081`

!!! success "Hot Reload Habilitado"
    O Spring Boot DevTools está configurado, então mudanças no código serão recarregadas automaticamente durante o desenvolvimento.

### Opção 2: Execução com Docker

Ideal para simular ambiente de produção ou testar a imagem Docker.

```bash
# 1. Build da imagem Docker
docker build -t devops-api:latest .

# 2. Execute o container
docker run -d \
  --name java-api-container \
  -p 8081:8081 \
  devops-api:latest

# 3. Visualize os logs
docker logs -f java-api-container

# 4. Acesse a aplicação
# http://localhost:8081
```

!!! tip "Multi-stage Build"
    O Dockerfile usa multi-stage build para otimizar o tamanho da imagem final, separando o build da execução.

### Opção 3: Build do JAR

Para criar um JAR executável para deploy manual:

```bash
# Build do projeto (sem executar testes)
./mvnw clean package -DskipTests

# Execute o JAR gerado
java -jar target/java-api-0.0.1-SNAPSHOT.jar

# Ou com profile específico
java -jar -Dspring.profiles.active=prod target/java-api-0.0.1-SNAPSHOT.jar
```

---

## Exemplos de Uso

### Acessar a Interface Web

```bash
# Página inicial
open http://localhost:8081

# Dashboard de status
open http://localhost:8081/status

# Formulário de CEP
open http://localhost:8081/cep
```

### Testar Endpoints da API

```bash
# Hello World
curl http://localhost:8081/api/hello

# Status do sistema (JSON)
curl http://localhost:8081/api/status | jq

# Consultar CEP
curl http://localhost:8081/api/cep/30350210 | jq

# Informações do container
curl http://localhost:8081/api/info | jq

# Health Check
curl http://localhost:8081/actuator/health | jq
```

### Acessar Documentação Swagger

```bash
# Interface interativa Swagger UI
open http://localhost:8081/swagger-ui.html

# Especificação OpenAPI JSON
curl http://localhost:8081/v3/api-docs | jq
```

---

## Executar Testes

```bash
# Executar todos os testes
./mvnw test

# Executar testes com cobertura
./mvnw test jacoco:report

# Executar apenas testes de uma classe específica
./mvnw test -Dtest=HelloControllerTest

# Modo verbose
./mvnw test -X
```

---

## Configuração

### Variáveis de Ambiente

A aplicação pode ser configurada através das seguintes variáveis:

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `SERVER_PORT` | `8081` | Porta do servidor |
| `API_VIACEP_URL` | `https://viacep.com.br/ws/` | URL da API ViaCEP |
| `API_CONNECTION_TIMEOUT` | `5000` | Timeout de conexão (ms) |
| `API_READ_TIMEOUT` | `10000` | Timeout de leitura (ms) |

### Arquivo application.properties

O arquivo principal de configuração está em `src/main/resources/application.properties`:

```properties
spring.application.name=java-api
server.port=8081

# Actuator
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always

# API externa
api.viacep.url=https://viacep.com.br/ws/
api.connection.timeout=5000
api.read.timeout=10000
```

---

## Troubleshooting

### Problema: Porta 8081 já está em uso

```bash
# Identifique o processo usando a porta
lsof -ti:8081

# Finalize o processo
kill -9 $(lsof -ti:8081)

# Ou mude a porta da aplicação
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8082
```

### Problema: Erro de permissão ao executar mvnw

```bash
# Dê permissão de execução
chmod +x mvnw

# Execute novamente
./mvnw spring-boot:run
```

### Problema: Java não encontrado

```bash
# Verifique a instalação do Java
java -version

# Configure JAVA_HOME (Linux/Mac)
export JAVA_HOME=/path/to/java17
export PATH=$JAVA_HOME/bin:$PATH

# Windows
set JAVA_HOME=C:\path\to\java17
set PATH=%JAVA_HOME%\bin;%PATH%
```

### Problema: Timeout ao consultar CEP

```bash
# Verifique conectividade com a API externa
curl https://viacep.com.br/ws/01001000/json/

# Aumente o timeout em application.properties
api.connection.timeout=10000
api.read.timeout=20000
```

### Problema: Testes falhando

```bash
# Limpe o cache do Maven
./mvnw clean

# Execute os testes com mais informações
./mvnw test -X

# Pule os testes temporariamente
./mvnw package -DskipTests
```

---

## Suporte e Contato

### Links Úteis

- **Repositório GitHub:** [iesodias/devops-automation-api](https://github.com/iesodias/devops-automation-api)
- **Issues:** [GitHub Issues](https://github.com/iesodias/devops-automation-api/issues)
- **Documentação Swagger:** http://localhost:8081/swagger-ui.html
- **Health Check:** http://localhost:8081/actuator/health

### Como Obter Ajuda

1. **Consulte a documentação** completa nas seções de API, Arquitetura e Deploy
2. **Verifique os logs** da aplicação para mensagens de erro detalhadas
3. **Abra uma issue** no GitHub com detalhes do problema
4. **Consulte o Troubleshooting** acima para problemas comuns

---

## Próximos Passos

Agora que você tem a aplicação rodando, explore:

1. [**API Reference**](api.md) - Documentação detalhada de todos os endpoints
2. [**Arquitetura**](arquitetura.md) - Entenda a estrutura e componentes do projeto
3. [**Deploy**](deploy.md) - Aprenda a fazer deploy em diferentes ambientes

---

!!! tip "Dica de Desenvolvimento"
    Mantenha a documentação Swagger aberta enquanto desenvolve: http://localhost:8081/swagger-ui.html

!!! warning "Ambiente de Produção"
    Esta API é educacional. Para uso em produção, adicione autenticação, rate limiting e outras medidas de segurança.

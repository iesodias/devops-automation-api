# 🚀 DevOps Automation API

API Java com Spring Boot construída para ensinar automação DevOps com qualidade, integração, CI/CD e containers.
Desenvolvida como parte do curso **DevOps Automation**.

<p align="center">
  <img src="https://raw.githubusercontent.com/iesodias/devops-automation-api/main/src/main/resources/static/devops.png" alt="DevOps Automation Logo" width="120"/>
</p>

---

## 🛠️ Tecnologias utilizadas

* ☕ **Java 17** - Amazon Corretto JDK
* 🌱 **Spring Boot 3.4.4** - Framework principal
* 🐘 **Maven Wrapper** - Gerenciamento de dependências
* 🎨 **Thymeleaf** - Templates HTML dinâmicos
* 🟣 **Docker** - Containerização multi-stage
* 📘 **SpringDoc OpenAPI** - Documentação automática da API
* 🔧 **Spring Boot Actuator** - Monitoramento e health checks
* 🌐 **RestTemplate** - Cliente HTTP para APIs externas
* ✅ **Spring Boot DevTools** - Hot reload durante desenvolvimento
* 🧪 **JUnit 5** - Framework de testes unitários

---

## 🚀 Como executar a aplicação

### 📦 Opção 1: Execução Local (Recomendado)

**Pré-requisitos:**
- Java 17 instalado
- Porta 8081 disponível

```bash
# Clone o repositório
git clone https://github.com/iesodias/devops-automation-api.git
cd devops-automation-api

# Execute a aplicação
./mvnw spring-boot:run

# Ou no Windows
mvnw.cmd spring-boot:run
```

**✅ Aplicação estará disponível em:** `http://localhost:8081`

### 🐳 Opção 2: Docker (Produção)

```bash
# Build da imagem Docker
docker build -t devops-api .

# Executa o container
docker run -d -p 8081:8081 --name java-api-container devops-api

# Visualiza logs
docker logs java-api-container

# Para o container
docker stop java-api-container && docker rm java-api-container
```

### 🧪 Opção 3: Build para Deploy

```bash
# Build do JAR (sem testes)
./mvnw clean package -DskipTests

# Execute o JAR gerado
java -jar target/java-api-0.0.1-SNAPSHOT.jar
```

---

## 📚 Funcionalidades e Endpoints

### 🏠 **Páginas Web (Interface Visual)**

| Método | Endpoint | Descrição | Exemplo |
|--------|----------|-----------|---------|
| `GET` | `/` | Página inicial com botões de navegação | [http://localhost:8081/](http://localhost:8081/) |
| `GET` | `/status` | Dashboard visual do status do sistema | [http://localhost:8081/status](http://localhost:8081/status) |
| `GET` | `/cep` | Interface para busca de CEP com formulário | [http://localhost:8081/cep](http://localhost:8081/cep) |

### 🔌 **API REST Endpoints**

#### **🌟 Endpoints Básicos**
| Método | Endpoint | Descrição | Resposta |
|--------|----------|-----------|----------|
| `GET` | `/api/hello` | Mensagem de hello world | `"Ola, Mundo da API!"` |
| `GET` | `/api/status` | Status detalhado em JSON com métricas do sistema | JSON com hostname, memória, CPU, etc. |
| `GET` | `/api/info` | Informações do container e ambiente | JSON com dados do sistema |
| `GET` | `/api/getContainerName` | Nome/hostname do container atual | Texto com hostname |

#### **📍 CEP API (Integração ViaCEP)**
| Método | Endpoint | Descrição | Exemplo | Validação |
|--------|----------|-----------|---------|-----------|
| `GET` | `/api/cep/{cep}` | Busca endereço por CEP | `/api/cep/30350210` | CEP com 8 dígitos |

**Exemplo de resposta CEP:**
```json
{
  "cep": "30350-210",
  "logradouro": "Rua Matipó",
  "bairro": "Santo Antônio",
  "localidade": "Belo Horizonte",
  "uf": "MG",
  "estado": "Minas Gerais",
  "regiao": "Sudeste",
  "ibge": "3106200",
  "ddd": "31"
}
```

#### **📄 Recursos Estáticos**
| Método | Endpoint | Descrição | Formato |
|--------|----------|-----------|---------|
| `GET` | `/api/json` | Retorna JSON estático de exemplo | JSON de dispositivo |

### 🏥 **Health Check & Monitoramento**

| Endpoint | Tipo | Descrição |
|----------|------|-----------|
| `/actuator/health` | JSON | Health check técnico com status de componentes |
| `/actuator/info` | JSON | Informações da aplicação |
| `/status` | HTML | Dashboard visual de status |
| `/api/status` | JSON | Status detalhado com métricas do sistema |

**Exemplo health check:**
```json
{
  "status": "UP",
  "components": {
    "diskSpace": {"status": "UP"},
    "ping": {"status": "UP"}
  }
}
```

### 📖 **Documentação da API**

| Endpoint | Descrição |
|----------|-----------|
| `/swagger-ui.html` | Interface Swagger interativa |
| `/v3/api-docs` | OpenAPI JSON specification |

---

## 🧰 Comandos de Desenvolvimento

### **🔨 Build & Testes**
```bash
# Executa todos os testes
./mvnw test

# Executa teste específico
./mvnw test -Dtest=CepControllerTest

# Build completo com testes
./mvnw clean package

# Build sem testes (mais rápido)
./mvnw clean package -DskipTests
```

### **🐛 Debug & Desenvolvimento**
```bash
# Executa em modo debug (porta 5005)
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

# Executa com perfil específico
./mvnw spring-boot:run -Dspring.profiles.active=dev

# Hot reload automático (já incluído com DevTools)
./mvnw spring-boot:run
```

### **📊 Monitoramento**
```bash
# Verifica health da aplicação
curl http://localhost:8081/actuator/health

# Verifica se a aplicação está respondendo
curl http://localhost:8081/api/hello

# Testa endpoint de CEP
curl http://localhost:8081/api/cep/30350210
```

---

## 🏗️ Arquitetura da Aplicação

### **📁 Estrutura do Projeto**
```
src/main/java/br/com/java_api/
├── controller/          # Controladores REST e Web
├── service/            # Lógica de negócio
├── dto/                # Data Transfer Objects
├── exception/          # Tratamento de exceções
├── config/             # Configurações
└── JavaApiApplication.java

src/main/resources/
├── templates/          # Templates Thymeleaf
├── static/            # Recursos estáticos
└── application.properties

src/test/              # Testes unitários
```

### **🔧 Configurações Principais**
- **Porta:** 8081
- **Timeout conexão:** 5 segundos
- **Timeout leitura:** 10 segundos
- **API Externa:** ViaCEP (https://viacep.com.br/ws/)
- **Perfil padrão:** default
- **Health checks:** Sempre com detalhes

---

## 🧪 Testes e Qualidade

### **📈 Cobertura de Testes**
- ✅ Controllers testados
- ✅ Services testados  
- ✅ Testes de integração
- ✅ Validação de endpoints

### **🚀 Execução dos Testes**
```bash
# Todos os testes
./mvnw test

# Testes com relatório
./mvnw clean verify

# Teste específico
./mvnw test -Dtest=CepServiceTest
```

---

## 🔧 Configuração de Ambiente

### **🌍 Variáveis de Ambiente (Opcionais)**
```bash
# Porta da aplicação (padrão: 8081)
SERVER_PORT=8081

# URL da API ViaCEP (padrão: https://viacep.com.br/ws/)
API_VIACEP_URL=https://viacep.com.br/ws/

# Timeout de conexão em ms (padrão: 5000)
API_CONNECTION_TIMEOUT=5000

# Timeout de leitura em ms (padrão: 10000)
API_READ_TIMEOUT=10000
```

### **📋 Requisitos do Sistema**
- **Java:** 17 ou superior
- **Maven:** 3.8+ (ou usar wrapper incluído)
- **Docker:** 20.10+ (opcional)
- **RAM:** Mínimo 512MB livres
- **Porta:** 8081 disponível

---

## 🚨 Solução de Problemas

### **❌ Problemas Comuns**

**Erro: Porta 8081 em uso**
```bash
# Linux/Mac
lsof -i :8081
kill -9 <PID>

# Windows
netstat -ano | findstr :8081
taskkill /PID <PID> /F
```

**Erro: Java não encontrado**
```bash
# Verificar versão
java -version

# Instalar Java 17 (Ubuntu)
sudo apt install openjdk-17-jdk

# Instalar Java 17 (macOS)
brew install openjdk@17
```

**Erro: Timeout na API ViaCEP**
- Verificar conectividade com internet
- CEP deve ter exatamente 8 dígitos
- Testar: `curl https://viacep.com.br/ws/30350210/json/`

---

## 📊 Exemplos de Uso

### **🔍 Testando a API**

```bash
# Hello World
curl http://localhost:8081/api/hello
# Resposta: "Ola, Mundo da API!"

# Status do sistema
curl http://localhost:8081/api/status
# Resposta: JSON com métricas

# Buscar CEP
curl http://localhost:8081/api/cep/30350210
# Resposta: JSON com endereço completo

# Health Check
curl http://localhost:8081/actuator/health
# Resposta: Status UP/DOWN com componentes
```

### **🌐 Acessando via Browser**
- **Home:** http://localhost:8081/
- **Status Dashboard:** http://localhost:8081/status  
- **Busca CEP:** http://localhost:8081/cep
- **API Docs:** http://localhost:8081/swagger-ui.html

---

## ✅ Licença

MIT — uso livre para fins educacionais e comerciais
Projeto mantido por [@iesodias](https://github.com/iesodias)

---

## 📞 Suporte

💬 **Dúvidas ou sugestões?**
- 🐛 Issues: [GitHub Issues](https://github.com/iesodias/devops-automation-api/issues)
- 📸 Instagram: [@iesofdias](https://instagram.com/iesofdias)
- 📧 Email: Abra uma issue no GitHub

**🎯 Ideal para:**
- Aprendizado de DevOps
- Exemplo de API REST
- Demonstração de containers
- Prática de CI/CD
- Tutorial de Spring Boot
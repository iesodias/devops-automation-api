# 🚀 Guia da Collection Postman - DevOps Automation API

Esta collection contém todos os endpoints da DevOps Automation API organizados para facilitar os testes.

## 📁 Arquivos da Collection

- `DevOps-Automation-API.postman_collection.json` - Collection principal com todos os endpoints
- `DevOps-Automation-API.postman_environment.json` - Environment com variáveis configuradas
- `POSTMAN-GUIDE.md` - Este guia de uso

## 🔧 Como Usar

### 1. **Importar no Postman**

1. Abra o Postman
2. Clique em **Import** (canto superior esquerdo)
3. Selecione os arquivos:
   - `DevOps-Automation-API.postman_collection.json`
   - `DevOps-Automation-API.postman_environment.json`
4. Clique em **Import**

### 2. **Configurar Environment**

1. Selecione o environment **"DevOps Automation API - Environment"** no dropdown
2. Certifique-se de que a aplicação está rodando em `http://localhost:8081`
3. As variáveis já estão pré-configuradas

### 3. **Executar Testes**

1. **Teste Individual**: Clique em qualquer request e execute
2. **Teste de Pasta**: Clique na pasta e escolha "Run collection"
3. **Collection Completa**: Execute toda a collection de uma vez

## 📚 Estrutura da Collection

### 🌟 **Basic Endpoints**
- ✅ `GET /api/hello` - Hello World básico
- ✅ `GET /api/status` - Status detalhado do sistema
- ✅ `GET /api/info` - Informações do container
- ✅ `GET /api/getContainerName` - Nome do container
- ✅ `GET /api/json` - JSON estático de exemplo

### 📍 **CEP API**
- ✅ `GET /api/cep/30350210` - CEP válido (Belo Horizonte)
- ✅ `GET /api/cep/01001000` - CEP de São Paulo
- ✅ `GET /api/cep/20040020` - CEP do Rio de Janeiro
- ❌ `GET /api/cep/123` - CEP inválido (teste de erro)
- ❌ `GET /api/cep/99999999` - CEP inexistente (404)

### 🏥 **Health Check & Monitoring**
- ✅ `GET /actuator/health` - Health check completo
- ✅ `GET /actuator/info` - Informações da aplicação

### 🌐 **Web Pages**
- ✅ `GET /` - Página inicial
- ✅ `GET /status` - Dashboard visual
- ✅ `GET /cep` - Interface de busca CEP
- ✅ `GET /swagger-ui.html` - Documentação Swagger

### 📖 **API Documentation**
- ✅ `GET /v3/api-docs` - OpenAPI JSON
- ✅ `GET /v3/api-docs.yaml` - OpenAPI YAML

## 🎯 Variáveis de Environment

| Variável | Valor Padrão | Descrição |
|----------|--------------|-----------|
| `base_url` | `http://localhost:8081` | URL base da API |
| `valid_cep` | `30350210` | CEP válido para testes |
| `cep_sp` | `01001000` | CEP de São Paulo |
| `cep_rj` | `20040020` | CEP do Rio de Janeiro |
| `invalid_cep` | `123` | CEP inválido para teste de erro |
| `nonexistent_cep` | `99999999` | CEP inexistente para teste 404 |

## 🧪 Testes Automatizados

A collection inclui testes automatizados que verificam:

### **Testes Globais** (aplicados a todas as requests):
- ✅ Tempo de resposta menor que 5 segundos
- ✅ Status code de sucesso (200, 201, 202)

### **Testes Específicos**:
- ✅ Validação de formato JSON nas respostas da API
- ✅ Verificação de campos obrigatórios nas respostas CEP
- ✅ Validação de estrutura do health check
- ✅ Testes de erro para CEPs inválidos

## 📊 Exemplos de Resposta

### **Hello World** (`/api/hello`)
```
Ola, Mundo da API!
```

### **Status da API** (`/api/status`)
```json
{
  "hostname": "136728c8e127",
  "memory": {
    "heap_max_MB": 1960,
    "heap_used_MB": 22,
    "non_heap_used_MB": 59
  },
  "os": "Linux",
  "app_version": "1.0.0",
  "ip": "172.17.0.2",
  "java_version": "17.0.16",
  "cpu_load_avg": 1.37841796875,
  "uptime_sec": 13,
  "status": "UP",
  "timestamp": "2025-08-11T02:01:05Z"
}
```

### **Busca CEP** (`/api/cep/30350210`)
```json
{
  "cep": "30350-210",
  "logradouro": "Rua Matipó",
  "complemento": "",
  "unidade": "",
  "bairro": "Santo Antônio",
  "localidade": "Belo Horizonte",
  "uf": "MG",
  "estado": "Minas Gerais",
  "regiao": "Sudeste",
  "ibge": "3106200",
  "gia": "",
  "ddd": "31",
  "siafi": "4123"
}
```

### **Health Check** (`/actuator/health`)
```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 62671097856,
        "free": 21877923840,
        "threshold": 10485760,
        "path": "/app/.",
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

## 🚀 Cenários de Teste Recomendados

### **1. Smoke Test (Teste Básico)**
```
1. GET /api/hello
2. GET /actuator/health
3. GET /api/status
```

### **2. Teste de Funcionalidades**
```
1. GET /api/cep/30350210 (CEP válido)
2. GET /api/cep/123 (CEP inválido)
3. GET /api/info
4. GET /api/getContainerName
```

### **3. Teste de Interface Web**
```
1. GET / (página inicial)
2. GET /status (dashboard)
3. GET /cep (busca CEP)
4. GET /swagger-ui.html (documentação)
```

### **4. Teste de Monitoramento**
```
1. GET /actuator/health
2. GET /actuator/info
3. GET /api/status
```

## ⚡ Dicas de Uso

### **🔄 Executar Collection Completa**
1. Clique na collection "DevOps Automation API"
2. Selecione **"Run collection"**
3. Configure iterations e delay se necessário
4. Execute e veja o relatório de testes

### **📊 Monitoring Dashboard**
- Use o **Collection Runner** para monitoramento contínuo
- Configure **Newman** para execução em CI/CD
- Exporte relatórios em HTML/JSON

### **🌍 Ambientes Diferentes**
- Clone o environment para diferentes ambientes
- Altere a `base_url` para:
  - Docker: `http://localhost:8081`
  - Produção: `https://your-domain.com`
  - Staging: `https://staging.your-domain.com`

### **🔧 Customização**
- Adicione novos CEPs nas variáveis de environment
- Modifique os testes conforme necessário
- Adicione headers de autenticação se implementar segurança

## 🐛 Solução de Problemas

### **Connection Error**
- ✅ Verifique se a aplicação está rodando (`docker ps` ou `mvnw spring-boot:run`)
- ✅ Confirme a porta 8081 está disponível
- ✅ Teste manualmente: `curl http://localhost:8081/api/hello`

### **Timeout Errors**
- ✅ Aumente o timeout no Postman (Settings > General > Request timeout)
- ✅ Verifique performance da aplicação
- ✅ Confirme se há recursos suficientes (RAM/CPU)

### **CEP API Errors**
- ✅ Verifique conectividade com internet (API ViaCEP externa)
- ✅ Teste diretamente: `curl https://viacep.com.br/ws/30350210/json/`
- ✅ Use CEPs válidos (8 dígitos)

## 📞 Suporte

🔗 **Links Úteis:**
- [Documentação Postman](https://learning.postman.com/docs/)
- [Newman CLI](https://github.com/postmanlabs/newman)
- [GitHub do Projeto](https://github.com/iesodias/devops-automation-api)

⚡ **Execução Rápida:**
```bash
# Instalar Newman (CLI do Postman)
npm install -g newman

# Executar collection via linha de comando
newman run DevOps-Automation-API.postman_collection.json \
  -e DevOps-Automation-API.postman_environment.json \
  --reporters cli,html \
  --reporter-html-export report.html
```

---

**🎯 Objetivo:** Esta collection permite testar completamente a DevOps Automation API, validar todas as funcionalidades e monitorar a saúde da aplicação de forma automatizada.
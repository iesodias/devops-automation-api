# 📚 API Reference

Documentação completa de todos os endpoints da **DevOps Automation API**.

---

## 🔗 Base URL

```
http://localhost:8081
```

Para ambientes de produção, substitua pelo domínio/IP apropriado.

---

## 🏠 Interface Web (HTML)

Páginas renderizadas com Thymeleaf para interação visual.

### GET `/`

**Descrição:** Página inicial da aplicação com menu de navegação.

**Resposta:** Página HTML com botões de navegação para as principais funcionalidades.

**Exemplo:**
```bash
curl http://localhost:8081/
# Ou abra no navegador
open http://localhost:8081/
```

---

### GET `/status`

**Descrição:** Dashboard visual com informações detalhadas do sistema.

**Resposta:** Página HTML exibindo:
- Nome do host/container
- Uso de memória (livre/total/máxima)
- Informações de CPU e processadores
- Espaço em disco
- Sistema operacional
- Versão do Java

**Exemplo:**
```bash
open http://localhost:8081/status
```

**Screenshot esperado:**
- Card com hostname
- Métricas de memória e CPU
- Gráficos visuais de recursos

---

### GET `/cep`

**Descrição:** Formulário interativo para consulta de CEP brasileiro.

**Resposta:** Página HTML com formulário de input para CEP.

**Funcionalidade:**
- Input de CEP com máscara
- Validação client-side
- Exibição do resultado formatado

**Exemplo:**
```bash
open http://localhost:8081/cep
```

---

## 🔌 API REST Endpoints

Endpoints JSON para integração programática.

---

## 🌟 Endpoints Básicos

### GET `/api/hello`

**Descrição:** Endpoint simples de teste/ping.

**Resposta:** String de texto plano.

**Código de Sucesso:** `200 OK`

**Exemplo de Request:**
```bash
curl http://localhost:8081/api/hello
```

**Exemplo de Response:**
```text
Ola, Mundo da API!
```

---

### GET `/api/status`

**Descrição:** Retorna informações detalhadas do sistema em formato JSON.

**Resposta:** JSON com métricas do sistema.

**Código de Sucesso:** `200 OK`

**Exemplo de Request:**
```bash
curl http://localhost:8081/api/status | jq
```

**Exemplo de Response:**
```json
{
  "status": "UP",
  "hostname": "java-api-container",
  "timestamp": "2025-12-28T10:30:45.123Z",
  "memory": {
    "free": "256MB",
    "total": "512MB",
    "max": "1024MB",
    "used": "256MB"
  },
  "cpu": {
    "processors": 8,
    "systemLoadAverage": 2.5
  },
  "disk": {
    "free": "45GB",
    "total": "100GB",
    "usable": "45GB"
  },
  "system": {
    "osName": "Linux",
    "osVersion": "5.10.0",
    "osArchitecture": "amd64",
    "javaVersion": "17.0.11"
  }
}
```

**Schema:**

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `status` | string | Status geral da aplicação |
| `hostname` | string | Nome do host/container |
| `timestamp` | string | Data/hora ISO 8601 |
| `memory.free` | string | Memória livre |
| `memory.total` | string | Memória total |
| `memory.max` | string | Memória máxima |
| `memory.used` | string | Memória em uso |
| `cpu.processors` | number | Número de processadores |
| `cpu.systemLoadAverage` | number | Carga média do sistema |
| `disk.free` | string | Espaço livre em disco |
| `disk.total` | string | Espaço total em disco |
| `system.osName` | string | Nome do sistema operacional |
| `system.javaVersion` | string | Versão do Java |

---

### GET `/api/info`

**Descrição:** Informações sobre o container e ambiente de execução.

**Resposta:** JSON com dados do ambiente.

**Código de Sucesso:** `200 OK`

**Exemplo de Request:**
```bash
curl http://localhost:8081/api/info | jq
```

**Exemplo de Response:**
```json
{
  "containerName": "java-api-container",
  "environment": "production",
  "appVersion": "1.0.0",
  "javaVersion": "17.0.11",
  "springBootVersion": "3.4.4"
}
```

---

### GET `/api/getContainerName`

**Descrição:** Retorna apenas o nome/hostname do container.

**Resposta:** String de texto plano.

**Código de Sucesso:** `200 OK`

**Exemplo de Request:**
```bash
curl http://localhost:8081/api/getContainerName
```

**Exemplo de Response:**
```text
java-api-container
```

---

## 📍 API de Consulta de CEP

Integração com a API pública **ViaCEP** para consulta de endereços brasileiros.

### GET `/api/cep/{cep}`

**Descrição:** Consulta informações de endereço a partir de um CEP brasileiro.

**Parâmetros:**

| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-------------|-----------|
| `cep` | string | Sim | CEP com 8 dígitos (com ou sem hífen) |

**Validação:**
- CEP deve ter exatamente 8 dígitos numéricos
- Aceita formatos: `12345678` ou `12345-678`
- Caracteres não numéricos são removidos automaticamente

**Código de Sucesso:** `200 OK`

**Códigos de Erro:**

| Código | Descrição |
|--------|-----------|
| `400 Bad Request` | CEP inválido (formato incorreto) |
| `404 Not Found` | CEP não encontrado na base ViaCEP |
| `500 Internal Server Error` | Erro ao consultar serviço externo |
| `504 Gateway Timeout` | Timeout na conexão com ViaCEP |

**Exemplo de Request:**
```bash
# Com hífen
curl http://localhost:8081/api/cep/30350-210 | jq

# Sem hífen
curl http://localhost:8081/api/cep/30350210 | jq
```

**Exemplo de Response (Sucesso):**
```json
{
  "cep": "30350-210",
  "logradouro": "Rua Matipó",
  "complemento": "",
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

**Schema da Response:**

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `cep` | string | CEP formatado com hífen |
| `logradouro` | string | Nome da rua/avenida |
| `complemento` | string | Complemento do endereço (opcional) |
| `bairro` | string | Nome do bairro |
| `localidade` | string | Nome da cidade |
| `uf` | string | Sigla do estado (2 letras) |
| `estado` | string | Nome completo do estado |
| `regiao` | string | Região do Brasil |
| `ibge` | string | Código IBGE do município |
| `gia` | string | Código GIA (SP) |
| `ddd` | string | Código DDD telefônico |
| `siafi` | string | Código SIAFI do município |

**Exemplo de Response (Erro - CEP Inválido):**
```json
{
  "timestamp": "2025-12-28T10:30:45.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "CEP inválido. Deve conter 8 dígitos.",
  "path": "/api/cep/123"
}
```

**Exemplo de Response (Erro - CEP Não Encontrado):**
```json
{
  "timestamp": "2025-12-28T10:30:45.123Z",
  "status": 404,
  "error": "Not Found",
  "message": "CEP não encontrado.",
  "path": "/api/cep/99999999"
}
```

**Testes com cURL:**
```bash
# CEP válido (Paulista Avenue, São Paulo)
curl http://localhost:8081/api/cep/01310100

# CEP válido (Copacabana, Rio de Janeiro)
curl http://localhost:8081/api/cep/22070900

# CEP inválido (muito curto)
curl http://localhost:8081/api/cep/123

# CEP não encontrado
curl http://localhost:8081/api/cep/99999999
```

---

## 📄 Recursos Estáticos

### GET `/api/json`

**Descrição:** Retorna um arquivo JSON estático de exemplo (device.json).

**Resposta:** JSON com dados de dispositivo.

**Código de Sucesso:** `200 OK`

**Exemplo de Request:**
```bash
curl http://localhost:8081/api/json | jq
```

**Exemplo de Response:**
```json
{
  "deviceId": "DEV-001",
  "deviceName": "Sample Device",
  "deviceType": "Sensor",
  "status": "active",
  "location": {
    "latitude": -23.550520,
    "longitude": -46.633308
  }
}
```

---

## 🏥 Health Check & Actuator

Endpoints de monitoramento fornecidos pelo **Spring Boot Actuator**.

### GET `/actuator/health`

**Descrição:** Health check da aplicação e seus componentes.

**Resposta:** JSON com status de saúde.

**Código de Sucesso:** `200 OK`

**Exemplo de Request:**
```bash
curl http://localhost:8081/actuator/health | jq
```

**Exemplo de Response:**
```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 107374182400,
        "free": 48318382080,
        "threshold": 10485760,
        "path": "/app",
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

**Status Possíveis:**
- `UP` - Aplicação funcionando normalmente
- `DOWN` - Aplicação com problemas
- `OUT_OF_SERVICE` - Temporariamente fora de serviço
- `UNKNOWN` - Status desconhecido

---

### GET `/actuator/info`

**Descrição:** Informações gerais da aplicação configuradas.

**Resposta:** JSON com metadados da aplicação.

**Código de Sucesso:** `200 OK`

**Exemplo de Request:**
```bash
curl http://localhost:8081/actuator/info | jq
```

**Exemplo de Response:**
```json
{
  "app": {
    "name": "java-api",
    "version": "1.0.0",
    "description": "DevOps Automation API"
  }
}
```

---

## 📖 Documentação OpenAPI

### GET `/swagger-ui.html`

**Descrição:** Interface interativa Swagger UI para testar a API.

**Resposta:** Página HTML do Swagger UI.

**Exemplo:**
```bash
open http://localhost:8081/swagger-ui.html
```

**Recursos disponíveis:**
- Listar todos os endpoints
- Ver schemas de request/response
- Testar endpoints diretamente no navegador
- Baixar especificação OpenAPI

---

### GET `/v3/api-docs`

**Descrição:** Especificação OpenAPI 3.0 em formato JSON.

**Resposta:** JSON com especificação completa da API.

**Código de Sucesso:** `200 OK`

**Exemplo de Request:**
```bash
curl http://localhost:8081/v3/api-docs | jq
```

**Uso:**
- Importar em ferramentas como Postman
- Gerar clientes de API automaticamente
- Validar contratos de API

---

## 🧪 Testando a API

### Com cURL

```bash
# Teste básico
curl -i http://localhost:8081/api/hello

# Consultar CEP com headers
curl -H "Accept: application/json" \
     http://localhost:8081/api/cep/01310100

# Health check com timeout
curl -m 5 http://localhost:8081/actuator/health
```

### Com HTTPie

```bash
# Instalar HTTPie
pip install httpie

# Fazer requisições
http GET http://localhost:8081/api/status
http GET http://localhost:8081/api/cep/30350210
```

### Com Postman

1. Importe a collection do arquivo `DevOps-Automation-API.postman_collection.json`
2. Configure o environment com `DevOps-Automation-API.postman_environment.json`
3. Execute os requests pré-configurados

---

## ⚙️ Rate Limiting

!!! warning "Sem Rate Limiting Implementado"
    Esta é uma API educacional e **não possui rate limiting** implementado. Em produção, considere adicionar:
    - Rate limiting por IP
    - Throttling de requisições
    - Circuit breaker para APIs externas

---

## 🔒 Autenticação

!!! info "API Pública"
    Atualmente, a API **não requer autenticação**. Todos os endpoints são públicos.
    
    Para ambientes de produção, considere implementar:
    - OAuth 2.0
    - JWT tokens
    - API Keys
    - Spring Security

---

## 🐛 Troubleshooting da API

### Erro 404 Not Found

**Possíveis causas:**
- Endpoint incorreto
- CEP não encontrado na base ViaCEP
- Rota não existe

**Solução:**
```bash
# Verifique a rota no Swagger
open http://localhost:8081/swagger-ui.html

# Verifique se a aplicação está rodando
curl http://localhost:8081/actuator/health
```

### Erro 500 Internal Server Error

**Possíveis causas:**
- Erro na API externa (ViaCEP)
- Timeout de conexão
- Problema no servidor

**Solução:**
```bash
# Verifique os logs da aplicação
docker logs java-api-container

# Teste conectividade com ViaCEP
curl https://viacep.com.br/ws/01001000/json/

# Verifique configurações de timeout
cat src/main/resources/application.properties | grep timeout
```

### Timeout na Consulta de CEP

**Possíveis causas:**
- Rede lenta
- API ViaCEP indisponível
- Timeout configurado muito baixo

**Solução:**
```bash
# Aumente o timeout em application.properties
api.connection.timeout=10000
api.read.timeout=20000
```

---

## 📞 Suporte

Para mais informações:

- 📘 [Documentação Completa](index.md)
- 🏗️ [Arquitetura](arquitetura.md)
- 🚢 [Guia de Deploy](deploy.md)
- 🐛 [Issues no GitHub](https://github.com/iesodias/devops-automation-api/issues)

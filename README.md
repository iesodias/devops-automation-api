# 🚀 DevOps Automation API

API Java com Spring Boot construída para ensinar automação DevOps com qualidade, integração, CI/CD e containers.
Desenvolvida como parte do curso **DevOps Automation**.

<p align="center">
  <img src="https://raw.githubusercontent.com/iesodias/devops-automation-api/main/src/main/resources/static/devops.png" alt="DevOps Automation Logo" width="120"/>
</p>

---

## 🛠️ Tecnologias utilizadas

* ☕ Java 17
* 🌱 Spring Boot 3
* 🐘 Maven Wrapper
* 🎨 Thymeleaf (HTML templates)
* 🟣 Docker
* 📘 Swagger OpenAPI

---

## 📦 Como rodar localmente (porta 8081)

```bash
git clone https://github.com/iesodias/devops-automation-api.git
cd devops-automation-api
./mvnw spring-boot:run
```

➡️ Acesse a aplicação em:
`http://localhost:8081`

---

## 🐳 Como rodar com Docker

```bash
docker build -t devops-api .
docker run -p 8081:8081 devops-api
```

---

## 📚 Endpoints disponíveis

| Método | Rota                    | Descrição                               |
| ------ | ----------------------- | --------------------------------------- |
| `GET`  | `/`                     | Página inicial com botões               |
| `GET`  | `/swagger-ui.html`      | Interface Swagger                       |
| `GET`  | `/status`               | Página visual de status                 |
| `GET`  | `/api/status`           | Status JSON detalhado                   |
| `GET`  | `/api/hello`            | Hello World da API                      |
| `GET`  | `/api/getContainerName` | Hostname do container                   |
| `GET`  | `/api/info`             | Info do ambiente de execução            |
| `GET`  | `/api/github`           | Retorna info pública do GitHub          |
| `GET`  | `/cep`                  | Página para buscar endereço por CEP     |
| `POST` | `/cep`                  | Retorna endereço formatado              |
| `GET`  | `/api/cep/{cep}`        | Retorna JSON do endereço via ViaCEP API |

---

## 🧪 Healthcheck

* 🔍 Técnico (JSON): `http://localhost:8081/actuator/health`
* 🖥️ Visual: `http://localhost:8081/status`

---

## 📍 Exemplo real

🔎 Buscar endereço por CEP:
[http://localhost:8081/cep](http://localhost:8081/cep)

🧱 Exemplo JSON direto:
[http://localhost:8081/api/cep/30350210](http://localhost:8081/api/cep/s)

---

## 🧰 Requisitos

* Java 17
* Maven 3.8+
* Docker (opcional)

---

## ✅ Licença

MIT — uso livre para fins educacionais e comerciais
Projeto mantido por [@iesodias](https://github.com/iesodias)

---

💬 Dúvidas ou sugestões? Me chama no [Instagram](https://instagram.com/) ou abre uma issue.

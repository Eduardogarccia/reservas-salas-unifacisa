# Sistema de Gerenciamento de Reservas de Salas – UNIFACISA  
API REST desenvolvida para permitir o gerenciamento de **salas**, **usuários** e **reservas** em um ambiente acadêmico.  
O sistema possibilita:

- Cadastro e consulta de salas  
- Cadastro de usuários  
- Criação, consulta, atualização e cancelamento de reservas  
- Verificação de disponibilidade de salas  
- Documentação automática via Swagger  
- Testes unitários das regras de negócio  

---

# Tecnologias Utilizadas

| Tecnologia | Função |
|-----------|--------|
| **Java 17+** | Linguagem principal |
| **Spring Boot** | Framework para criação da API REST |
| Spring Web | Controllers REST |
| Spring Data JPA | Persistência |
| Spring Validation | Validação de dados |
| **H2 Database (In-memory)** | Banco temporário |
| **Lombok** | Redução de boilerplate |
| **Swagger / OpenAPI** | Documentação da API |
| **JUnit 5 + Mockito** | Testes unitários |

---

# Como executar o projeto

## 1. Pré-requisitos

- Java 17 ou superior  
- Maven  
- Git (opcional)

---

## 2. Rodando a aplicação

Via terminal:

```bash
mvn spring-boot:run
```

A API ficará disponível em:

http://localhost:8080

## Banco de Dados H2

O sistema utiliza um banco de dados **H2 em memória**, ideal para testes durante o desenvolvimento, pois os dados são armazenados temporariamente e são apagados sempre que a aplicação é encerrada.

### Acesso ao Console do H2

Você pode acessar o console web do H2 pelo navegador:

```bash
http://localhost:8080/h2-console
```
### Configurações para login no H2

Use os seguintes parâmetros ao acessar o console:

| Campo       | Valor                       |
|-------------|------------------------------|
| **JDBC URL** | `jdbc:h2:mem:unifacisa_db`  |
| **User**     | `sa`                        |
| **Password** | *(deixe em branco)*         |

### Observações

- O banco é executado **inteiramente em memória**, logo:
  - Todos os dados são **apagados ao reiniciar** a aplicação.
  - Não é necessário instalar nenhum banco na máquina.
- As entidades são criadas automaticamente através do **JPA/Hibernate**.

---

## Documentação da API (Swagger)

A API possui documentação automática gerada com **springdoc-openapi** (Swagger), permitindo visualizar e testar todos os endpoints diretamente pelo navegador.

### Swagger UI

A interface interativa do Swagger pode ser acessada em:

```text
http://localhost:8080/swagger-ui/index.html
```
## Endpoints Principais

A seguir está a visão geral dos principais endpoints implementados na API.

---

## Salas

### **Endpoints**

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/salas` | Criar uma nova sala |
| `GET`  | `/salas` | Listar todas as salas |
| `GET`  | `/salas/{id}` | Consultar sala pelo ID |
| `PUT`  | `/salas/{id}` | Atualizar dados da sala |
| `DELETE` | `/salas/{id}` | Remover uma sala |
| `GET` | `/salas/disponiveis?data=YYYY-MM-DD&hora_inicio=HH:MM&hora_fim=HH:MM` | Salas disponíveis no período |

### **Exemplo de criação de sala**

```json
{
  "nome": "Sala 101",
  "tipo": "SALA_AULA",
  "capacidade": 40,
  "status": "ATIVA"
}
```
## Usuários

### **Endpoints**

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/usuarios` | Criar um novo usuário |
| `GET`  | `/usuarios` | Listar todos os usuários |
| `GET`  | `/usuarios/{id}` | Consultar usuário pelo ID |
| `PUT`  | `/usuarios/{id}` | Atualizar informações do usuário |
| `DELETE` | `/usuarios/{id}` | Remover um usuário |

### **Exemplo de criação de usuário**

```json
{
  "nome": "João da Silva",
  "email": "joao@exemplo.com"
}
```
## Reservas

### **Endpoints**

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/reservas` | Criar uma nova reserva |
| `GET`  | `/reservas` | Listar todas as reservas |
| `GET`  | `/reservas/{id}` | Consultar uma reserva pelo ID |
| `GET`  | `/reservas?sala_id=X&data=YYYY-MM-DD` | Buscar reservas por sala e data específica |
| `GET`  | `/reservas?usuario_id=X` | Buscar reservas feitas por um usuário |
| `PUT`  | `/reservas/{id}` | Atualizar informações de uma reserva |
| `DELETE` | `/reservas/{id}` | Cancelar uma reserva |

---

### **Exemplo de criação de reserva**

```json
{
  "usuarioId": 1,
  "salaId": 10,
  "data": "2025-12-10",
  "horaInicio": "10:00",
  "horaFim": "12:00",
  "motivo": "Aula de lógica"
}
```










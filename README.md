# 🎓 Sistema de Gerenciamento de Reservas de Salas – UNIFACISA  
API REST desenvolvida para permitir o gerenciamento de **salas**, **usuários** e **reservas** em um ambiente acadêmico.  
O sistema possibilita:

- Cadastro e consulta de salas  
- Cadastro de usuários  
- Criação, consulta, atualização e cancelamento de reservas  
- Verificação de disponibilidade de salas  
- Documentação automática via Swagger  
- Testes unitários das regras de negócio  

Essa aplicação foi construída para fins acadêmicos como parte da disciplina de Engenharia de Software e Qualidade.

---

# 🚀 Tecnologias Utilizadas

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

# ⚙️ Como executar o projeto

## ✔️ 1. Pré-requisitos

- Java 17 ou superior  
- Maven  
- Git (opcional)

---

## ✔️ 2. Rodando a aplicação

Via terminal:

```bash
mvn spring-boot:run

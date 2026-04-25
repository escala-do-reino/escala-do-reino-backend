# 👑 Escala do Reino

## 📖 Sobre o Projeto

O **Escala do Reino** é um sistema desenvolvido para gerenciar e organizar escalas de ministérios em igrejas de forma simples e eficiente.

O principal objetivo é fornecer visibilidade, planejamento e organização das equipes envolvidas em cultos e eventos.

Este projeto está sendo desenvolvido como uma iniciativa pessoal, com foco em evolução técnica, boas práticas de desenvolvimento e construção de uma arquitetura escalável.

---

## 🚀 Funcionalidades (Planejadas)

* [ ] Autenticação de usuários (JWT)
* [ ] Controle de acesso por perfil (Admin, Líder, Membro)
* [ ] Criação e gerenciamento de escalas
* [ ] Associação de membros a funções (louvor, mídia, recepção, etc.)
* [ ] Visualização em calendário
* [ ] Notificações (ideia futura)
* [ ] Dashboard com informações relevantes

---

## 🧱 Arquitetura

O projeto segue uma **arquitetura em camadas**:

* **Controller** → Responsável pelas requisições HTTP
* **Service** → Contém as regras de negócio
* **Repository** → Acesso aos dados
* **DTOs** → Transferência de dados entre camadas

Além disso:

* Banco de dados executando em container com Docker
* Flyway para versionamento do banco de dados

Possíveis evoluções futuras:

* Domain-Driven Design (DDD)
* Arquitetura orientada a eventos

---

## 🛠️ Tecnologias Utilizadas

### Backend

* Java 21+
* Spring Boot
* Spring Security (JWT)
* JPA / Hibernate
* Flyway (migrations de banco de dados)
* H2 (para testes)

### Infraestrutura

* Docker
* Docker Compose

### Ferramentas

* Git & GitHub
* IntelliJ IDEA

---

## 📂 Estrutura do Projeto (Backend)

```
src/main/java/com/seuusuario/escaladoreino
│
├── controller
├── service
├── repository
├── dto
├── entity
├── config
├── security
└── exception
```

---

## 🔌 Endpoints (Planejados)

### 🔐 Autenticação

* POST /auth/login → Autenticar usuário
* POST /auth/register → Registrar novo usuário

### 👤 Usuários

* GET /users → Listar usuários
* GET /users/{id} → Buscar usuário por ID

### 🙋 Membros

* POST /members → Criar membro
* GET /members → Listar membros

### 📅 Escalas

* POST /schedules → Criar escala
* GET /schedules → Listar escalas
* GET /schedules/{id} → Buscar escala por ID

---

## ⚙️ Como Executar o Projeto

### Pré-requisitos

* Java 21+
* Docker
* Docker Compose

### Subir banco com Docker

```
docker-compose up -d
```

### Executar aplicação

```
git clone https://github.com/seu-usuario/escala-do-reino.git
cd escala-do-reino
./mvnw spring-boot:run
```

---

## 🧪 Testes

```
./mvnw test
```

---

## 📌 Roadmap

* [ ] Finalizar módulo de autenticação
* [ ] Implementar sistema de escalas
* [ ] Desenvolver frontend (React)
* [ ] Realizar deploy da aplicação

---

## 🤝 Contribuição

Este é um projeto pessoal, mas sugestões e melhorias são bem-vindas.

---

## 📄 Licença

Este projeto está sob a licença MIT.

---

## ✍️ Autor

Desenvolvido por **Nathan Nolacio**

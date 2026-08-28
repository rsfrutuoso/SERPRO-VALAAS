# VALAAS

VALAAS é uma plataforma de Validação como Serviço para organizações que precisam registrar fontes de dados, definir critérios aplicáveis e processar solicitações de validação com segurança, rastreabilidade e escalabilidade.

## Visão geral

Esta base inicial implementa o primeiro módulo funcional: o `validator-service`, com arquitetura hexagonal e estrutura de domínio, aplicação e adapters. O objetivo é estabelecer uma base compilável e testável sobre a qual os módulos de cadastro, mensageria e jobs serão adicionados incrementalmente.

## Módulos

- `validator-service`: serviço principal de validação e negócio mínimo de solicitação.

## Requisitos

- Java 17+
- Maven 3.9+

## Execução local

```bash
mvn clean test
mvn -pl validator-service spring-boot:run
```

## Endpoints iniciais

- `POST /api/v1/validations`
- `GET /api/v1/validations/{validationId}`

## Documentação da API

A documentação OpenAPI/Swagger está disponível em:

- `http://localhost:8080/swagger-ui/index.html`
- `http://localhost:8080/v3/api-docs`

## Observações

- A solução inicial foi criada sem dependências de fornecedor específico.
- O domínio não importa Spring, JPA ou clientes HTTP.
- Os próximos incrementos incluirão PostgreSQL, NATS JetStream, cache, segurança e IaC.

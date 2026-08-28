# Premissas do projeto VALAAS

## Assumptions

- `ASSUMPTION`: Java 17 e Spring Boot 3.3.3 são adotados como linha base por compatibilidade com o ecossistema Spring e com a base corporativa que ainda não foi explicitada.
- `ASSUMPTION`: O primeiro módulo implementado é o `validator-service`, enquanto `registrar-bff`, `validation-subscriber` e `validation-jobs` permanecem como próximos incrementos.
- `ASSUMPTION`: A autenticação e autorização corporativas serão integradas em etapa posterior com OAuth2/OIDC ou mTLS conforme padrão do cliente.
- `ASSUMPTION`: O isolamento multi-tenant será implementado por `tenant_id` no domínio e em filtros da base de dados, sem confiar no payload como fonte de autorização.
- `ASSUMPTION`: A API pública inicial suportará criação e consulta de validações com resposta `202 Accepted` e idempotência por `Idempotency-Key`.
- `ASSUMPTION`: PostgreSQL será a fonte de verdade persistente; H2 é usado apenas para execução local e testes automatizados da base inicial.
- `ASSUMPTION`: NATS JetStream e Valkey/Redis serão incorporados como próximos incrementos, após a base de domínio e persistência inicial estabilizar.
- `ASSUMPTION`: O repositório permanece sem segredos e sem endereços internos; qualquer valor sensível deve ser tratado via variáveis de ambiente ou secret manager.

## Decisões relevantes

1. A arquitetura foi iniciada em formato hexagonal com separação entre domínio, aplicação e adapters.
2. O domínio foi mantido isento de dependências Spring, JPA e clientes HTTP.
3. A base inicial foi desenhada para permitir evolução com PostgreSQL, NATS, Valkey, observabilidade e IaC subsequentes.

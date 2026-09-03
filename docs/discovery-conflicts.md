# Relatório de Descoberta — Conflitos Prioritários e Recomendações

Resumo: lista priorizada de conflitos entre a POC (Kotlin/Quarkus) e o projeto-alvo (Java/Spring Boot), com recomendação objetiva e testes de caracterização necessários.

1) Java target mismatch
- Conflito: POC Java11, projeto Java17, cliente exige Java25.
- Recomendação: Plano de migração incremental — validar dependências e plugins para Java25; executar builds em toolchain JDK25; não produzir quebra de contrato funcional.
- Testes: compilar + rodar suíte unitária e integração com JDK25; containerize com Java25 e rodar smoke tests.

2) POST /v1/validation semântica (síncrono 200 vs assíncrono 202/NATS)
- Conflito: POC retorna 200 com result; projeto atual retorna 202 e publica em NATS.
- Recomendação: Apresentar ao cliente 3 opções (manter síncrono, adotar 202 async com status endpoint, oferecer ambos via versão). Recomendado: preferir compatibilidade com POC inicialmente (apoio ao sync) se requisito de latência e custo permitir; caso contrário, optar por async 202 e definir status contract.
- Testes: caracterização de contrato para ambos comportamentos; teste end-to-end com NATS se async.

3) DTO/Field naming
- Conflito: mistura de camelCase (sourceId) e snake_case (key_attribute) na POC; projeto pode usar camelCase.
- Recomendação: padronizar para camelCase na API pública; manter suporte retrocompatível por versão se necessário.
- Testes: serialização/deserialização tests; OpenAPI diff; contract tests against POC examples.

4) Segurança / PLIN
- Conflito: POC sem PLIN; projeto implementa PLIN JWT extraction.
- Recomendação: Formalizar uso de PLIN no contrato e criar mock PLIN para testes de caracterização; atualizar OpenAPI securitySchemes.
- Testes: auth integration tests with valid/invalid tokens and ACL enforcement.

5) Persistence and tests environment
- Conflito: POC uses H2 in-memory; project uses Postgres + Flyway + Testcontainers.
- Recomendação: Standardize integration tests on Postgres/Testcontainers to avoid dialect issues; keep H2 for fast unit tests only.
- Testes: run migrations and integration tests on Postgres Testcontainers.

6) External adapters and stubs
- Conflito: POC uses stubs for RECFACIAL/LIVENESS/RECDIGITAIS/VIODECODER; project has adapters but some TODOs remain.
- Recomendação: Implement robust adapters in project with retry/circuit-breaker; keep WireMock stubs for tests; treat external services as contractual dependencies.
- Testes: WireMock scenarios for match/mismatch/timeout/5xx and mapping to VALID/INVALID/TECHNICAL_ERROR.

7) ACL/Ownership
- Conflito: POC lacks ACL; project enforces ACL. Behavior must be aligned for clients.
- Recomendação: Enforce ACL as security requirement for PRIVATE sources; document PUBLIC semantics; forbid clientId in body; extract clientId from PLIN JWT.
- Testes: authorization negative/positive tests; attempt with forged X-Client-Id must fail in non-local profiles.

8) Observability
- Conflito: project has Actuator and Micrometer; POC lacks observability.
- Recomendação: Ensure minimal observability in all builds used for contract testing; document metric names and health groups.
- Testes: verify actuator endpoints and that metrics include validation counts/latency without high-cardinality labels.

Bloqueios críticos a resolver antes de implementação:
- Alinhar contrato síncrono vs assíncrono para POST /v1/validation.
- Definir target Java (cliente exige Java25) e confirmar compatibilidade de bibliotecas.
- Confirmar política de persistência para DELETE (soft vs hard) quando há histórico/auditoria.

Próximo passo recomendado (após aprovação deste relatório):
- Gerar automaticamente a suíte inicial de testes de caracterização para os itens marcados PARCIAL/NÃO_ATENDIDO (auth/ACL, POST /v1/validation behavior, DTO compat, migrations Postgres, QRCode validation).
- Executar os testes de caracterização em ambiente com JDK25 e Testcontainers/Postgres (ci local ou runner).

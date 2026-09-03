# Matriz de Rastreabilidade de Requisitos — VALAAS / VaaS

Formato: ID | Requisito | Evidência na POC | Evidência no projeto-alvo | Estado | Lacuna | Alteração proposta | Teste de aceite

RF-SOURCE-01 | Cadastrar fonte - POST /v1/source | POC: `SourceResource` e `openapi.yaml` com exemplo `sourceCreateExample` (vaas-main/src/main/resources/openapi.yaml) | Projeto: `SourceController` e DTOs em validator-service/src/main/java/... | PARCIAL | POC não implementa ACL/ownership; projeto tem ACL mas DTOs e exemplos divergem | Unificar OpenAPI; garantir resposta sem secrets (writeOnly) e aplicar ACL em endpoints | Teste: criar fonte como cliente autorizado; resposta não contém segredo; GET lista IDs; tentativa por client não autorizado retorna 403

RF-SOURCE-02 | Listar IDs das fontes acessíveis - GET /v1/source | POC: `GET /v1/source` presente, retorna lista de fontes (POC síncrono) | Projeto: `GET /v1/source` presente; implementa filtro por ownership/ACL | ATENDIDO | Compatibilidade de esquema e paginação a validar | Ajustar OpenAPI para versão acordada; preservar listagem de IDs sem expor credenciais | Teste: listar fontes autenticado; validar apenas IDs e metadados não sensíveis

RF-SOURCE-03 | Recuperar fonte - GET /v1/source/{id} | POC: presente; DTO marca secret como writeOnly | Projeto: presente; DTO de saída não retorna secrets (testes cobrem) | ATENDIDO | Nenhuma crítica encontrada | Confirmar formato do DTO e campos retornados | Teste: GET retorna metadados, sem secrets

RF-SOURCE-04 | Atualizar fonte - PUT /v1/source/{id} | POC: presente, sem controle robusto de ownership | Projeto: presente; SourceService.checkOwner implementado | PARCIAL | Definir regras de ownership/administrador global | Propor: somente owner ou admin pode atualizar; auditoria de alterações | Teste: atualização por owner sucesso; por outro clientId retorna 403; alteração auditada

RF-SOURCE-05 | Excluir fonte - DELETE /v1/source/{id} | POC: implementa DELETE física; não define política de retenção | Projeto: DELETE implementado; política não definida | BLOQUEADO_POR_INFORMAÇÃO | Decidir entre soft delete ou physical delete quando existir histórico | Propor: soft-delete por padrão; auditoria e proteção quando há histórico de validações | Teste: excluir fonte com/sem histórico; comportamento conforme decisão

RF-VALIDATION-01 | Solicitar validação - POST /v1/validation (multi-validation) | POC: `ValidationResource` aceita payload com `sourceId`, `key_attribute`, `validations[]` e retorna 200 com resultados síncronos; OpenAPI mostra `sourceId` e `key_attribute` mistos | Projeto: `ValidationController` existe, mas controller atual retorna 202 e delega a NATS para processamento assíncrono | CONFLITANTE | Diferença de semântica (sync 200 vs async 202); diferenças em nomes de campos (`key_attribute` vs camelCase) | Não alterar contrato sem decisão. Propor alternativas documentadas: (A) manter sync (compatível POC) (B) adotar async 202 com `Location` e status endpoint (C) suportar ambos via versão | Teste: caracterização do POST validando status code e schema; se 202, testar endpoint de consulta do resultado

RF-VALIDATION-01.1 | Validação por tipo: RegistrationValidation | POC: `RegistrationValidation` presente e funcional (comparação simples) | Projeto: classe `RegistrationValidation` existe; implementações variam; testes unitários presentes | PARCIAL | Normalização e regras de comparação precisam de confirmação por cliente | Padronizar normalização por configuração; documentar comportamento default | Teste: cadastro de casos com accents/upper/lower/null e validar resultado esperado

RF-VALIDATION-01.2 | Facial Validation (RECFACIAL) | POC: `FacialValidation` chama stub de RECFACIAL; openapi e exemplos presentes | Projeto: `FacialValidation` existe; adapter `RecFacialClient` e WireMock stubs em tests | PARCIAL | Integrações estão como stub em POC; projeto tem adapter porém possivelmente mockado em perfil local | Implementar adapters com retry/circuit-breaker; threshold configurável | Teste: WireMock simula match/mismatch/timeout; validar mapeamento de códigos e thresholds

RF-VALIDATION-01.3 | FacialWithLiveness (LIVENESS + RECFACIAL) | POC: `FacialWithLivenessValidation` implementa liveness como stub e decide sequência | Projeto: classe existe mas contém TODOs na chamada de liveness | PARCIAL | Liveness stub/no-op; falta tratamento claro de indisponibilidade vs reprovação | Propor: executar LIVENESS primeiro; falha definitiva impede RECFACIAL; documentar exceções | Teste: liveness fail -> overall NOT_EXECUTED or FAILED_LIVENESS; liveness unavailable -> TECHNICAL_ERROR

RF-VALIDATION-01.4 | Digital Validation (RECDIGITAIS) | POC: `DigitalValidation` implementado como stub | Projeto: `DigitalValidation` existe; adapter presente | PARCIAL | Integração stubbed; produção não aceita stubs | Implementar adapter resiliente e validação de formato/posição | Teste: sucesso, functional failure, technical error, malformed payload

RF-VALIDATION-01.5 | QRCode Validation (VIODECODER) | POC: `QRCodeValidation` presente com stub | Projeto: implementação ausente ou nome diferente (NOT_FOUND) | NÃO_ATENDIDO | QRCodeValidation ausente no projeto; POC usa stub | Implementar adapter VIODECODER e Strategy correspondente; testes de decodificação | Teste: QR invalid, unsupported doc, decoded success

RF-ACL-01 | ACL de fontes (PUBLIC/PRIVATE e lista de client_id) | POC: sem ACL (não obrigatório na POC) | Projeto: ACL implementada via SourceService.checkOwner e verificação antes de validação | PARCIAL | POC não implementa; projeto implementa e precisa ser alinhado ao contrato | Propor: ACL obrigatória para PRIVATE; PUBLIC permite qualquer client autenticado; audit logs | Teste: PRIVATE fonte restrita; PUBLIC fonte acessível por cliente autenticado; falsificação de X-Client-Id deve falhar

RNF-SEC-01 | Autenticação OAuth2 Client Credentials (PLIN) | POC: sem PLIN | Projeto: PLIN/JWKS config e PlinJwtAuthenticationFilter presente | PARCIAL | POC falta; projeto suporta PLIN — precisa ser formalizado no OpenAPI e testado | Propor: OpenAPI securitySchemes com OAuth2 client_credentials; adicionar mock PLIN para testes | Teste: requests com/sem token; extração de client_id e uso em ACL

RNF-SEC-02 | Proteção de credenciais e prevenção SSRF | POC: não aborda SSRF; permite URIs simples | Projeto: validações básicas e allowlist ausente; código contém notas de segurança | BLOQUEADO_POR_INFORMAÇÃO | Política de egress não formalizada | Propor: validação de URI, allowlist, HTTPS-only em prod, TLS checks, secret manager for credentials | Teste: tentativa SSRF para metadata endpoints deve ser bloqueada; criação de fonte com non-https em prod proibida

RNF-OBS-01 | Observabilidade mínima (Actuator/Micrometer, correlation id) | POC: ausente | Projeto: Actuator e Micrometer presentes; CorrelationIdFilter implementado | PARCIAL | POC precisa ser melhorado para testes de contrato | Propor: documentar metrics e endpoints; incluir correlation id in tracing | Teste: /actuator/health and /actuator/prometheus availability; X-Correlation-Id present

RNF-AVAIL-01 | Build e runtime target Java 25 (cliente exige Java 25) | POC: Java 11; Projeto: Java 17 | NÃO_ATENDIDO | Ambos diferentes do requisito Java25 | Propor: avaliar compatibilidade de libs, preparar migração para Java25 e adaptar CI/CD; falhar build em JDK incompatível via Enforcer/Toolchains | Teste: build and tests on JDK 25; container runtime Java25 smoke test


Observações gerais:
- Cada linha acima deve ser validada com evidência (arquivo, trecho de código e teste de caracterização).
- IDs com duplicidade no documento original devem ser preservados e mapeados conforme regra: RF-SOURCE-01..05, RF-VALIDATION-01(.1-.5), RF-ACL-01.
- Itens marcados como PARCIAL ou NÃO_ATENDIDO requerem testes de caracterização antes de qualquer alteração.

# DOCUMENTAÇÃO TÉCNICA DA API — REDE RAÍZES DO NORDESTE

## 1. APRESENTAÇÃO

A Rede Raízes do Nordeste é uma API REST desenvolvida para apoiar o gerenciamento de uma rede de restaurantes.

O sistema contempla gerenciamento de produtos, cardápios, pedidos, estoque por unidade, funcionários, unidades, combos promocionais, fidelidade e relatórios gerenciais.

A aplicação utiliza Java 21, Spring Boot, Spring Data JPA, Spring Security, MySQL, Flyway e autenticação JWT.

## 2. ENDEREÇOS DA API

Ambiente local:

`http://localhost:8080`

Documentação interativa:

`http://localhost:8080/swagger-ui/index.html`

Especificação OpenAPI:

`http://localhost:8080/v3/api-docs`

A documentação Swagger apresenta os endpoints disponibilizados pela aplicação e permite consultar os contratos das operações.

## 3. AUTENTICAÇÃO

### POST /auth/login

**Finalidade:** autenticar funcionários e fornecer um token JWT.

**Acesso:** público.

Exemplo de requisição:

```json
{
  "usuario": "gerente_brasilia",
  "senha": "SENHA_DO_USUARIO"
}
```

Resposta de sucesso — HTTP 200:

```json
{
  "token": "TOKEN_JWT",
  "tipo": "Bearer",
  "expiraEmSegundos": 3600
}
```

Nas rotas protegidas, o token deve ser enviado no cabeçalho:

`Authorization: Bearer TOKEN_JWT`

O token possui validade configurada de uma hora.

## 4. CARDÁPIO

### GET /produtos/cardapio

**Finalidade:** consultar os produtos disponíveis no cardápio.

**Acesso:** público.

**Resposta:** HTTP 200, contendo a lista de produtos disponíveis.

## 5. RELATÓRIOS GERENCIAIS

As operações de relatórios exigem autenticação e autorização conforme as regras de acesso do sistema.

Os perfis ADMIN e GERENTE possuem acesso aos relatórios, respeitando as restrições de unidade.

O administrador pode consultar informações da rede. O gerente possui acesso limitado à unidade vinculada ao seu cadastro.

### GET /relatorios/geral

Consulta indicadores gerais de vendas, pedidos pagos e cancelamentos.

Parâmetro opcional:

- `idUnidade`: identificador da unidade.

### GET /relatorios/estoque

Consulta quantidades e situações do estoque por unidade.

Parâmetro opcional:

- `idUnidade`: identificador da unidade.

### GET /relatorios/produtos/mais-vendidos

Consulta os produtos com maior quantidade de vendas.

Parâmetro opcional:

- `idUnidade`: identificador da unidade.

### GET /relatorios/fidelidade

Consulta indicadores do programa de fidelidade.

Parâmetro opcional:

- `idUnidade`: identificador da unidade.

## 6. PEDIDOS E PAGAMENTOS

O sistema permite cadastrar pedidos vinculados a clientes e unidades, considerando os produtos solicitados, o canal de origem e as regras de estoque.

O campo `canalPedido` é obrigatório.

O cadastro válido de pedidos foi verificado com resposta HTTP 201.

### PATCH /pedidos/{id}/pagamento

**Finalidade:** simular o processamento do pagamento de um pedido.

**Acesso:** ADMIN ou GERENTE.

Parâmetro de caminho:

- `id`: identificador do pedido.

Exemplo de pagamento aprovado:

```json
{
  "resultado": "APROVADO"
}
```

Exemplo de pagamento recusado:

```json
{
  "resultado": "RECUSADO"
}
```

Resultados previstos:

- Pagamento aprovado: pedido atualizado para `PAGO`.
- Pagamento recusado: pedido atualizado para `PAGAMENTO_RECUSADO`.

O processamento é uma simulação acadêmica, sem integração com instituições financeiras.

## 7. PERFIS E CONTROLE DE ACESSO

O sistema utiliza Spring Security e autenticação JWT.

Perfis de acesso:

- **ADMIN:** administração geral da rede.
- **GERENTE:** gerenciamento da unidade vinculada.
- **ATENDENTE:** operações de atendimento e pedidos, conforme permissões configuradas.
- **COZINHA:** operações de preparo, conforme permissões configuradas.

As permissões são verificadas pelas regras de segurança e autorização dos endpoints.

O acesso às unidades é controlado para impedir que um gerente consulte dados de unidades às quais não está vinculado.

## 8. CÓDIGOS DE RESPOSTA HTTP

| Código | Significado |
|---|---|
| 200 | Requisição realizada com sucesso |
| 201 | Recurso criado com sucesso |
| 400 | Requisição inválida |
| 401 | Autenticação ausente ou inválida |
| 403 | Usuário sem permissão |
| 404 | Recurso não encontrado |
| 409 | Conflito com o estado atual do recurso, como estoque insuficiente |
| 500 | Erro interno do servidor |

## 9. SEGURANÇA E PROTEÇÃO DE DADOS

A aplicação utiliza autenticação JWT, controle de acesso por perfil e proteção das rotas por meio do Spring Security.

As credenciais e chaves secretas são configuradas por variáveis de ambiente:

- `DB_PASSWORD`
- `ADMIN_PASSWORD`
- `JWT_SECRET`

As credenciais não devem ser armazenadas no código-fonte nem publicadas no repositório.

O projeto utiliza codificação segura de senhas por meio do mecanismo de criptografia configurado na aplicação.

A documentação e a implementação das medidas de privacidade e proteção de dados devem ser consideradas em conjunto com os requisitos da LGPD.

## 10. TESTES DA API

Foram realizados testes manuais utilizando Postman, contemplando:

- Login de ADMIN e GERENTE.
- Acesso a relatórios com autenticação.
- Bloqueio de relatórios sem autenticação.
- Consulta pública ao cardápio.
- Controle de acesso entre unidades.
- Consulta de produtos inexistentes.
- Bloqueio de pedidos sem autenticação.
- Cadastro de pedido válido.
- Validação de campo obrigatório.
- Validação de estoque insuficiente.
- Simulação de pagamento aprovado e recusado.

### Resultados observados

| Cenário | Resultado |
|---|---|
| Login ADMIN | HTTP 200 |
| Login GERENTE | HTTP 200 |
| Relatórios com ADMIN | HTTP 200 |
| GERENTE na unidade permitida | HTTP 200 |
| GERENTE em outra unidade | HTTP 403 |
| ADMIN em outra unidade | HTTP 200 |
| Produto inexistente | HTTP 404 |
| Pedido sem autenticação | HTTP 401 |
| Cadastro de pedido válido | HTTP 201 |
| Pedido sem canal obrigatório | HTTP 400 |
| Estoque insuficiente | HTTP 409 |
| Pagamento aprovado | HTTP 200 |
| Pagamento recusado | HTTP 200 |
| Relatório sem autenticação | HTTP 401 |
| Cardápio público | HTTP 200 |

Os resultados correspondem aos testes manuais realizados no ambiente local.

## 11. DOCUMENTAÇÃO COMPLEMENTAR

Os materiais do projeto estão disponíveis na pasta `docs`:

- `API.md`: documentação técnica da API.
- `LEIA-ME.txt`: orientações complementares.
- `Rede_Raizes_Postman_SS.json`: coleção de requisições do Postman.
- `UNINTER.drawio`: diagrama do projeto.

## 12. REPOSITÓRIO

Código-fonte:

https://github.com/adriellysouzax/rede-raizes-do-nordeste

A documentação interativa dos endpoints pode ser consultada pelo Swagger UI após iniciar a aplicação.
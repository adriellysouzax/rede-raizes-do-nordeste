# Documentação da API — Rede Raízes do Nordeste

## 1. Apresentação

A API Rede Raízes do Nordeste foi desenvolvida utilizando Java, Spring Boot e MySQL.

O sistema permite gerenciar produtos, pedidos, estoque por unidade, funcionários, unidades, combos promocionais e programas de fidelidade.

A autenticação dos funcionários utiliza tokens JWT, com permissões definidas conforme o perfil de acesso.

## 2. Endereço da API

Ambiente local:

`http://localhost:8080`

As requisições protegidas devem enviar o token JWT no cabeçalho:

`Authorization: Bearer TOKEN_JWT`

## 3. Autenticação

### POST /auth/login

**Finalidade:** autenticar funcionários e fornecer um token de acesso.

**Permissão:** pública.

**Corpo da requisição:**

```json
{
  "usuario": "gerente_brasilia",
  "senha": "SENHA_DO_USUARIO"
}
```

**Resposta de sucesso — HTTP 200:**

```json
{
  "token": "TOKEN_JWT",
  "tipo": "Bearer",
  "expiraEmSegundos": 3600
}
```

O token deve ser enviado nas requisições que exigem autenticação.

## 4. Cardápio

### GET /produtos/cardapio

**Finalidade:** consultar os produtos disponíveis no cardápio.

**Permissão:** pública.

**Resposta de sucesso:** HTTP 200, contendo uma lista de produtos disponíveis.

## 5. Relatórios gerenciais

As rotas abaixo exigem autenticação com perfil ADMIN ou GERENTE.

O administrador pode consultar informações de diferentes unidades. O gerente possui acesso restrito à unidade vinculada ao seu cadastro.

### GET /relatorios/geral

**Finalidade:** consultar indicadores gerais de vendas, pedidos pagos e cancelamentos.

**Parâmetro opcional:**
- `idUnidade`: identifica a unidade para consulta.

**Exemplo de resposta:**

```json
{
  "totalPedidos": 2,
  "idUnidade": null,
  "pedidosCancelados": 0,
  "totalVendas": 50.00,
  "pedidosPagos": 1
}
```

### GET /relatorios/estoque

**Finalidade:** consultar as quantidades e situações do estoque por unidade.

**Parâmetro opcional:**
- `idUnidade`: identifica a unidade para consulta.

**Campos retornados:**
- idUnidade
- unidade
- idProduto
- produto
- quantidade
- situacao

### GET /relatorios/produtos/mais-vendidos

**Finalidade:** identificar os produtos mais vendidos.

**Parâmetro opcional:**
- `idUnidade`: identifica a unidade para consulta.

**Campos retornados:**
- idProduto
- nome
- quantidadeVendida

### GET /relatorios/fidelidade

**Finalidade:** consultar indicadores do programa de fidelidade.

**Parâmetro opcional:**
- `idUnidade`: identifica a unidade para consulta.

**Campos retornados:**
- idUnidade
- pedidosComResgate
- pontosGeradosEstimados
- resgatesRealizadosEstimados
- totalDescontos

## 6. Pedidos e pagamentos

### Cadastro de pedidos

A API permite cadastrar pedidos vinculados a clientes e unidades, considerando o canal de origem, os produtos solicitados e as regras de estoque.

O campo `canalPedido` é obrigatório.

Entre os canais utilizados está `APP`.

**Resultado do teste de cadastro válido:** HTTP 201 — Created.

### PATCH /pedidos/{id}/pagamento

**Finalidade:** realizar a simulação do processamento de pagamento de um pedido.

**Permissão:** ADMIN ou GERENTE.

**Parâmetro de caminho:**
- `id`: identificador do pedido.

**Corpo da requisição — pagamento aprovado:**

```json
{
  "resultado": "APROVADO"
}
```

**Corpo da requisição — pagamento recusado:**

```json
{
  "resultado": "RECUSADO"
}
```

**Resultados esperados:**

- Pagamento aprovado: pedido atualizado para `PAGO`.
- Pagamento recusado: pedido atualizado para `PAGAMENTO_RECUSADO`.

O processamento é uma simulação de pagamento, não uma integração com instituição financeira.

## 7. Segurança e permissões

Os perfis de acesso utilizados pelo sistema são:

- **ADMIN:** acesso administrativo à rede.
- **GERENTE:** gerenciamento restrito à unidade vinculada.
- **ATENDENTE:** operações de atendimento e pedidos, conforme permissões configuradas.
- **COZINHA:** operações de preparo, conforme permissões configuradas.

As permissões são verificadas pelo Spring Security e pelas regras de autorização dos endpoints.

A autenticação utiliza tokens JWT com validade configurada de uma hora.

## 8. Códigos HTTP

| Código | Significado |
|---|---|
| 200 | Requisição realizada com sucesso |
| 201 | Recurso criado com sucesso |
| 400 | Requisição inválida |
| 401 | Autenticação ausente ou inválida |
| 403 | Usuário autenticado, mas sem permissão |
| 404 | Recurso não encontrado |
| 409 | Conflito com o estado atual do recurso, como estoque insuficiente |
| 500 | Erro interno do servidor |

## 9. Testes realizados

Foram realizados testes manuais utilizando o Postman para verificar o funcionamento da API.

Os cenários testados incluíram:

- Login de administrador e gerente.
- Acesso aos relatórios com autenticação.
- Bloqueio de relatório sem autenticação.
- Consulta pública ao cardápio.
- Controle de acesso entre unidades.
- Consulta de produtos inexistentes.
- Bloqueio de pedidos sem autenticação.
- Cadastro de pedido válido.
- Validação de campo obrigatório no pedido.
- Validação de estoque insuficiente.
- Simulação de pagamento aprovado.
- Simulação de pagamento recusado.

### Resultados observados

| Cenário | Resultado |
|---|---|
| Login ADMIN | HTTP 200 |
| Login GERENTE | HTTP 200 |
| Relatórios com ADMIN | HTTP 200 |
| Relatórios com GERENTE na unidade permitida | HTTP 200 |
| GERENTE acessando outra unidade | HTTP 403 |
| ADMIN acessando outra unidade | HTTP 200 |
| Produto inexistente | HTTP 404 |
| Pedido sem autenticação | HTTP 401 |
| Cadastro de pedido válido | HTTP 201 |
| Pedido sem canal obrigatório | HTTP 400 |
| Pedido com estoque insuficiente | HTTP 409 |
| Pagamento aprovado | HTTP 200 |
| Pagamento recusado | HTTP 200 |
| Relatório sem autenticação | HTTP 401 |
| Cardápio público | HTTP 200 |

Os resultados acima correspondem aos testes manuais realizados no ambiente local.

## 10. Documentação complementar

Os materiais complementares estão disponíveis na pasta `docs` do projeto:

- `README.md`: apresentação e instruções de execução.
- `API.md`: documentação dos endpoints.
- `LEIA-ME.txt`: orientações complementares.
- `Rede_Raizes_Postman_SS.json`: coleção de requisições e testes do Postman.
- `UNINTER.drawio`: diagrama do projeto.

## 11. Considerações finais

A API Rede Raízes do Nordeste contempla autenticação, controle de acesso por perfil, gerenciamento de pedidos, consultas gerenciais, controle de estoque e simulação de pagamentos.

Os testes manuais realizados permitem verificar os principais cenários de funcionamento, autenticação, autorização e validação do sistema.
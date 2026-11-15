
# Documentação da API — Rede Raízes do Nordeste

## 1. Apresentação

A API Rede Raízes do Nordeste foi desenvolvida utilizando Java, Spring Boot e MySQL.

O sistema permite gerenciar produtos, pedidos, estoque, funcionários, unidades, combos promocionais e programas de fidelidade.

A autenticação dos funcionários utiliza tokens JWT, com permissões definidas conforme o perfil de acesso.

## 2. Endereço da API

Ambiente local: http://localhost:8080

As requisições protegidas devem enviar o token JWT:

Authorization: Bearer TOKEN

## 3. Autenticação

### POST /auth/login

**Finalidade:** autenticar funcionários e fornecer um token de acesso.

**Permissão:** pública.

**Exemplo de requisição:**

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

## 4. Cardápio

### GET /produtos/cardapio

**Finalidade:** consultar os produtos disponíveis no cardápio.

**Permissão:** pública.

**Resposta de sucesso:** HTTP 200, com uma lista de produtos.

## 5. Relatórios gerenciais

As rotas abaixo exigem autenticação com perfil ADMIN ou GERENTE.

O administrador pode consultar informações da rede. O gerente fica limitado à sua unidade.

### GET /relatorios/geral

**Finalidade:** consultar vendas, pedidos pagos e cancelamentos.

**Parâmetro opcional:** `idUnidade`.

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

**Parâmetro opcional:** `idUnidade`.

**Campos retornados:** idUnidade, unidade, idProduto, produto, quantidade e situacao.

### GET /relatorios/produtos/mais-vendidos

**Finalidade:** identificar os produtos mais vendidos.

**Parâmetro opcional:** `idUnidade`.

**Campos retornados:** idProduto, nome e quantidadeVendida.

### GET /relatorios/fidelidade

**Finalidade:** consultar indicadores do programa de fidelidade.

**Parâmetro opcional:** `idUnidade`.

**Campos retornados:** idUnidade, pedidosComResgate, pontosGeradosEstimados, resgatesRealizadosEstimados e totalDescontos.

## 6. Segurança e permissões

- ADMIN: acesso administrativo à rede.
- GERENTE: acesso gerencial restrito à unidade vinculada.
- ATENDENTE: operações de atendimento e pedidos, conforme permissões.
- COZINHA: operações de preparo, conforme permissões.

As permissões são verificadas pelo Spring Security.

## 7. Códigos HTTP

| Código | Significado |
|---|---|
| 200 | Requisição realizada com sucesso |
| 201 | Recurso criado com sucesso, quando aplicável |
| 400 | Requisição inválida |
| 401 | Autenticação ausente ou inválida |
| 403 | Usuário sem permissão |
| 404 | Recurso não encontrado |
| 500 | Erro interno do servidor |

Os códigos listados representam situações possíveis. O retorno específico de cada endpoint deve ser confirmado na implementação.

## 8. Testes realizados

Foram realizados testes de autenticação JWT, consultas aos quatro relatórios e controle de acesso por perfil.

Foi confirmado que o administrador consegue consultar o estoque de diferentes unidades e que o gerente de Brasília recebe HTTP 403 ao tentar consultar o relatório de estoque de Salvador.

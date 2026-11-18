# Rede Raízes do Nordeste

## Sobre o projeto

O Rede Raízes do Nordeste é um sistema Back-End desenvolvido para o gerenciamento de uma rede de restaurantes.

A aplicação permite controlar produtos, cardápios, pedidos, estoque por unidade, funcionários, combos promocionais e fidelidade dos clientes.

O projeto foi desenvolvido como atividade acadêmica do curso de Análise e Desenvolvimento de Sistemas da UNINTER.

## Tecnologias utilizadas

- Java 21
- Spring Boot 4.1.1
- Spring Security
- Spring Data JPA
- MySQL
- Flyway
- JWT
- Maven
- Swagger/OpenAPI

## Organização do projeto

- `controller`: endpoints da API.
- `service`: regras de negócio.
- `repository`: acesso ao banco de dados.
- `model`: entidades do sistema.
- `config`: configurações de segurança e aplicação.
- `docs`: documentação complementar.

## Funcionalidades

- Autenticação de funcionários com JWT.
- Controle de permissões por perfil.
- Gerenciamento de unidades.
- Cadastro e consulta de produtos.
- Consulta de cardápio.
- Controle de estoque por unidade.
- Gerenciamento de pedidos.
- Simulação de pagamentos.
- Combos promocionais.
- Programa de fidelidade.
- Relatórios gerenciais.

## Requisitos para execução

- Java JDK 21.
- MySQL Server.
- Maven Wrapper incluído no projeto.

## Configuração do ambiente

### 1. Banco de dados

Crie o banco de dados MySQL:

```sql
CREATE DATABASE IF NOT EXISTS raizes_nordeste;
```

A aplicação utiliza o banco `raizes_nordeste` e o usuário `raizes_app`.

Configure o acesso ao banco e a senha correspondente por meio das variáveis de ambiente, sem publicar credenciais no repositório.

### 2. Variáveis de ambiente

Configure as seguintes variáveis antes de iniciar a aplicação:

- `DB_PASSWORD`: senha do usuário MySQL utilizado pela aplicação.
- `ADMIN_PASSWORD`: senha inicial do administrador.
- `JWT_SECRET`: chave secreta utilizada na geração e validação dos tokens JWT, com pelo menos 32 bytes.

Não compartilhe essas credenciais nem as publique no GitHub.

### 3. Migrações

As migrações do banco de dados são gerenciadas pelo Flyway, conforme a configuração da aplicação.

## Executando a aplicação

No Windows, abra o terminal na pasta que contém o arquivo `pom.xml` e execute:

```powershell
.\mvnw.cmd spring-boot:run
```

Com as configurações e o banco de dados disponíveis, a API será iniciada em:

http://localhost:8080

## Documentação Swagger

Com a aplicação em execução, acesse:

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Especificação OpenAPI: http://localhost:8080/v3/api-docs

Documentação complementar:

`docs/API.md`

## Autenticação

O login dos funcionários é realizado pelo endpoint:

`POST /auth/login`

Exemplo de requisição:

```json
{
  "usuario": "gerente_brasilia",
  "senha": "SENHA_DO_USUARIO"
}
```

Após a autenticação, a API retorna um token JWT.

As rotas protegidas exigem o cabeçalho:

`Authorization: Bearer TOKEN_JWT`

## Perfis de acesso

- ADMIN: administração da rede.
- GERENTE: gerenciamento da unidade vinculada.
- ATENDENTE: operações de atendimento.
- COZINHA: operações de preparo.

As permissões específicas são definidas nos endpoints e nas regras de segurança da aplicação.

## Relatórios

Principais endpoints de relatórios:

- `GET /relatorios/geral`
- `GET /relatorios/estoque`
- `GET /relatorios/fidelidade`
- `GET /relatorios/produtos/mais-vendidos`

O acesso depende das permissões configuradas para cada endpoint.

## Testes

Foram realizados testes manuais utilizando o Postman, contemplando:

- Autenticação de ADMIN e GERENTE.
- Controle de acesso por perfil e unidade.
- Acesso a rotas públicas e protegidas.
- Consulta de relatórios.
- Cadastro de pedidos.
- Validação de campos obrigatórios.
- Validação de estoque insuficiente.
- Simulação de pagamento aprovado e recusado.

A coleção utilizada nos testes está disponível em:

`docs/Rede_Raizes_Postman_SS.json`

Para executar os testes automatizados do projeto, utilize:

```powershell
.\mvnw.cmd clean test
```

O resultado dos testes automatizados deve ser conferido no terminal após a execução.

## Segurança

O projeto utiliza Spring Security, autenticação JWT e controle de permissões por perfil.

As senhas e chaves secretas devem ser mantidas em variáveis de ambiente e nunca publicadas no repositório.

## Materiais complementares

A documentação do projeto está organizada na pasta `docs`:

- `API.md`: documentação dos endpoints.
- `LEIA-ME.txt`: orientações complementares.
- `Rede_Raizes_Postman_SS.json`: coleção de requisições e testes do Postman.
- `UNINTER.drawio`: diagrama do projeto.

## Repositório

Código-fonte disponível em:

https://github.com/adriellysouzax/rede-raizes-do-nordeste
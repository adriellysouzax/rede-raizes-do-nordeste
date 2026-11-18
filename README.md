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

Para executar o projeto, é necessário ter:

- Java JDK 21.
- MySQL Server.
- Maven Wrapper, incluído no projeto.
- Git, para clonar o repositório.

## Configuração do ambiente

### 1. Clonar o repositório

```bash
git clone https://github.com/adriellysouzax/rede-raizes-do-nordeste.git
```

Entre na pasta do projeto Java, onde está localizado o arquivo `pom.xml`.

### 2. Banco de dados

Crie o banco de dados MySQL:

```sql
CREATE DATABASE IF NOT EXISTS raizes_nordeste;
```

A aplicação utiliza o banco `raizes_nordeste` e o usuário `raizes_app`.

O usuário do banco deve possuir permissões de acesso e alteração nesse banco.

### 3. Variáveis de ambiente

Antes de iniciar a aplicação, configure as seguintes variáveis:

- `DB_PASSWORD`: senha do usuário MySQL utilizado pela aplicação.
- `ADMIN_PASSWORD`: senha inicial do administrador.
- `JWT_SECRET`: chave secreta utilizada na geração e validação dos tokens JWT, com pelo menos 32 bytes.

No Windows, utilizando PowerShell, configure as variáveis na mesma janela do terminal em que a aplicação será iniciada:

```powershell
$env:DB_PASSWORD = "SUA_SENHA_DO_MYSQL"
$env:ADMIN_PASSWORD = "SUA_SENHA_DO_ADMIN"
```

Para gerar uma chave JWT aleatória:

```powershell
$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
$rng.Dispose()
```

Substitua os valores de exemplo pelas credenciais definidas no ambiente local.

As variáveis configuradas dessa forma permanecem disponíveis somente na sessão atual do terminal. Caso o terminal seja fechado, será necessário configurá-las novamente.

**Importante:** nunca publique senhas, tokens ou chaves secretas no GitHub.

### 4. Migrações do banco de dados

As migrações do banco de dados são gerenciadas pelo Flyway, conforme a configuração da aplicação.

As migrações são executadas durante a inicialização, de acordo com a configuração do projeto.

## Executando a aplicação

No Windows, abra o PowerShell na pasta que contém o arquivo `pom.xml` e execute:

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
- Validação de campos obrigatórios
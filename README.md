
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
- Acesso ao Maven Wrapper incluído no projeto.

## Configuração

1. Criar o banco de dados MySQL:

```sql
CREATE DATABASE IF NOT EXISTS raizes_nordeste;
```

2. Configurar a conexão com o banco no arquivo de propriedades da aplicação, sem publicar senhas no repositório.

3. Configurar a variável de ambiente `JWT_SECRET` com pelo menos 32 bytes.

4. Executar as migrações Flyway conforme a configuração do projeto.

## Executando a aplicação

No Windows, abra um terminal na pasta que contém o arquivo `pom.xml` e execute:

```powershell
.\mvnw.cmd spring-boot:run
```

A API estará disponível em:

http://localhost:8080

## Documentação Swagger

Com a aplicação em execução, acesse:

http://localhost:8080/swagger-ui/index.html

Especificação OpenAPI:

http://localhost:8080/v3/api-docs

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

As permissões específicas são definidas nos endpoints.

## Relatórios

- `GET /relatorios/geral`
- `GET /relatorios/estoque`
- `GET /relatorios/fidelidade`
- `GET /relatorios/produtos/mais-vendidos`

Os relatórios exigem perfil ADMIN ou GERENTE.

## Testes

Para executar os testes automatizados:

```powershell
.\mvnw.cmd clean test
```

Também foram realizados testes manuais de autenticação, autorização e consulta aos relatórios.

## Segurança

O projeto utiliza Spring Security, autenticação JWT e controle de permissões por perfil.

As credenciais e chaves secretas não devem ser publicadas no repositório.

## Materiais complementares

A entrega acadêmica deverá incluir:

- Diagramas do sistema.
- Documentação da API.
- Coleção de testes Postman/Insomnia.
- Evidências dos testes realizados.
- Repositório público do projeto.

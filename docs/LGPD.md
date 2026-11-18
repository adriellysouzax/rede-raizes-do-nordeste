# Privacidade e proteÃ§Ã£o de dados â€” Rede RaÃ­zes do Nordeste

## Dados tratados e finalidades
O cadastro de clientes utiliza nome, CPF, e-mail, telefone e senha para identificaÃ§Ã£o, comunicaÃ§Ã£o e operaÃ§Ã£o dos pedidos. A senha Ã© armazenada como hash BCrypt, nunca em texto puro. A participaÃ§Ã£o no programa de fidelidade Ã© opcional e separada das demais finalidades do cadastro.

## Consentimento para fidelidade
Quando o cliente opta por participar do programa de fidelidade, o sistema registra a escolha (`aceitaFidelidade`), a data e hora (`dataConsentimentoFidelidade`) e a versÃ£o do texto apresentado (`versaoConsentimentoFidelidade`, atualmente `1.0`). A ausÃªncia de aceite nÃ£o impede o cadastro. A versÃ£o `1.0` deve corresponder ao texto de consentimento efetivamente apresentado ao cliente pela aplicaÃ§Ã£o.

## Auditoria
A tabela `auditoria_evento` registra eventos de cadastro de cliente, criaÃ§Ã£o de pedido, aprovaÃ§Ã£o ou recusa de pagamento, cancelamento de pedido e login bem-sucedido. Cada registro contÃ©m evento, entidade, identificador do registro, responsÃ¡vel, data/hora e descriÃ§Ã£o operacional. NÃ£o registrar senhas, hashes de senha, tokens JWT, CPF, e-mail, telefone ou dados completos de cartÃ£o.

## SeguranÃ§a e acesso
As senhas de clientes e funcionÃ¡rios devem permanecer protegidas com BCrypt. As rotas devem continuar protegidas pela autenticaÃ§Ã£o JWT e pelas regras de perfil jÃ¡ existentes. O acesso aos registros de auditoria deve ser restrito a pessoal autorizado; nÃ£o disponibilizar consulta pÃºblica.

## RetenÃ§Ã£o e descarte
A organizaÃ§Ã£o deve aprovar e documentar prazos de retenÃ§Ã£o por categoria de dado, considerando obrigaÃ§Ãµes legais, fiscais, consumeristas e a necessidade de defesa de direitos. Ao fim do prazo aplicÃ¡vel, eliminar ou anonimizar os dados pessoais, preservando apenas registros que precisem ser mantidos por obrigaÃ§Ã£o legal ou exercÃ­cio regular de direitos. A rotina de descarte deve ser definida pelo responsÃ¡vel pelo sistema e executada de forma controlada; esta implementaÃ§Ã£o nÃ£o apaga dados automaticamente.

## TransparÃªncia e base legal
A finalidade e a hipÃ³tese legal de cada tratamento devem ser definidas pelo controlador e informadas ao titular em aviso de privacidade. O aceite do programa de fidelidade nÃ£o deve ser usado como consentimento genÃ©rico para todos os tratamentos. A adequaÃ§Ã£o jurÃ­dica das bases legais e dos prazos deve ser validada pelo responsÃ¡vel pela proteÃ§Ã£o de dados da organizaÃ§Ã£o.

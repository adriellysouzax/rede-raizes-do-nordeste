

package br.com.raizesdonordeste.service;

import br.com.raizesdonordeste.dto.ClienteCadastroDTO;
import br.com.raizesdonordeste.model.Cliente;
import br.com.raizesdonordeste.repository.ClienteRepository;
import br.com.raizesdonordeste.service.AuditoriaService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public ClienteService(
            ClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder,
            AuditoriaService auditoriaService) {

        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    // Lista os clientes cadastrados
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    // Cadastra um novo cliente
    public Cliente cadastrarCliente(ClienteCadastroDTO dados) {

        if (clienteRepository.existsByEmail(dados.email())) {
            throw new IllegalArgumentException(
                "Este e-mail jÃ¡ estÃ¡ cadastrado."
            );
        }

        Cliente cliente = new Cliente();

        cliente.setNome(dados.nome());
        cliente.setCpf(dados.cpf());
        cliente.setEmail(dados.email());
        cliente.setTelefone(dados.telefone());
        cliente.setAceitaFidelidade(Boolean.TRUE.equals(dados.aceitaFidelidade()));
        if (Boolean.TRUE.equals(dados.aceitaFidelidade())) {
            cliente.setDataConsentimentoFidelidade(java.time.LocalDateTime.now());
            cliente.setVersaoConsentimentoFidelidade("1.0");
        }

        // Protege a senha usando BCrypt
        String senhaProtegida = passwordEncoder.encode(dados.senha());
        cliente.setSenhaHash(senhaProtegida);

        Cliente salvo = clienteRepository.save(cliente);
        auditoriaService.registrar("CLIENTE_CADASTRADO", "CLIENTE", salvo.getIdCliente(), "CLIENTE", "Cadastro de cliente realizado");
        return salvo;
    }
}


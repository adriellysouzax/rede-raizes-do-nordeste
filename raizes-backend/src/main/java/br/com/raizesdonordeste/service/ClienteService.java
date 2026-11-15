

package br.com.raizesdonordeste.service;

import br.com.raizesdonordeste.dto.ClienteCadastroDTO;
import br.com.raizesdonordeste.model.Cliente;
import br.com.raizesdonordeste.repository.ClienteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(
            ClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder) {

        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Lista os clientes cadastrados
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    // Cadastra um novo cliente
    public Cliente cadastrarCliente(ClienteCadastroDTO dados) {

        if (clienteRepository.existsByEmail(dados.email())) {
            throw new IllegalArgumentException(
                "Este e-mail já está cadastrado."
            );
        }

        Cliente cliente = new Cliente();

        cliente.setNome(dados.nome());
        cliente.setCpf(dados.cpf());
        cliente.setEmail(dados.email());
        cliente.setTelefone(dados.telefone());
        cliente.setAceitaFidelidade(dados.aceitaFidelidade());

        // Protege a senha usando BCrypt
        String senhaProtegida = passwordEncoder.encode(dados.senha());
        cliente.setSenhaHash(senhaProtegida);

        return clienteRepository.save(cliente);
    }
}

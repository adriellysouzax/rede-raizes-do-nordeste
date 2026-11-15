
package br.com.raizesdonordeste.controller;

import br.com.raizesdonordeste.dto.ClienteCadastroDTO;
import br.com.raizesdonordeste.model.Cliente;
import br.com.raizesdonordeste.service.ClienteService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Lista os clientes cadastrados
    @GetMapping
    public List<Cliente> listarClientes() {
        return clienteService.listarClientes();
    }

    // Cadastra um cliente
    @PostMapping
    public Cliente cadastrarCliente(
            @Valid @RequestBody ClienteCadastroDTO dados) {

        return clienteService.cadastrarCliente(dados);
    }
}

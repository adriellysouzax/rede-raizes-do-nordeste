
package br.com.raizesdonordeste.controller;

import br.com.raizesdonordeste.model.Unidade;
import br.com.raizesdonordeste.repository.UnidadeRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/unidades")
public class UnidadeController {

    private final UnidadeRepository unidadeRepository;

    public UnidadeController(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }

    // Administrador e gerente podem consultar unidades
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public List<Unidade> listar() {
        return unidadeRepository.findAll();
    }

    // Somente o administrador geral pode cadastrar unidades
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Unidade cadastrar(@RequestBody Unidade unidade) {

        if (unidade.getNome() == null ||
                unidade.getNome().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome da unidade é obrigatório.");
        }

        return unidadeRepository.save(unidade);
    }
}

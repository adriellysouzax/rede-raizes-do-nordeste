
package br.com.raizesdonordeste.controller;

import br.com.raizesdonordeste.service.FidelidadeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fidelidade")
public class FidelidadeController {

    private final FidelidadeService fidelidadeService;

    public FidelidadeController(
            FidelidadeService fidelidadeService) {
        this.fidelidadeService = fidelidadeService;
    }

    // Consulta os pontos de um cliente
    @PreAuthorize("hasRole('GERENTE')")
    @GetMapping("/cliente/{idCliente}")
    public Integer consultarPontos(
            @PathVariable Long idCliente) {

        return fidelidadeService.consultarPontos(idCliente);
    }
}

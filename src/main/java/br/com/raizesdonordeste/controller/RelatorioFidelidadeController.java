
package br.com.raizesdonordeste.controller;

import br.com.raizesdonordeste.service.RelatorioFidelidadeService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/relatorios/fidelidade")
public class RelatorioFidelidadeController {

    private final RelatorioFidelidadeService service;

    public RelatorioFidelidadeController(
            RelatorioFidelidadeService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Map<String, Object>> consultar(
            @RequestParam(required = false) Long idUnidade,
            Authentication authentication) {

        boolean administrador = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (!administrador) {

            Jwt jwt = (Jwt) authentication.getPrincipal();
            Number unidadeToken = jwt.getClaim("idUnidade");

            if (unidadeToken == null) {
                return ResponseEntity.status(403).build();
            }

            Long unidadeGerente = unidadeToken.longValue();

            if (idUnidade != null &&
                    !idUnidade.equals(unidadeGerente)) {
                return ResponseEntity.status(403).build();
            }

            idUnidade = unidadeGerente;
        }

        return ResponseEntity.ok(service.consultar(idUnidade));
    }
}

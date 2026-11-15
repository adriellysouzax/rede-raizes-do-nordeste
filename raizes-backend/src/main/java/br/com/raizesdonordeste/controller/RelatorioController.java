
package br.com.raizesdonordeste.controller;

import br.com.raizesdonordeste.service.RelatorioService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/geral")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Map<String, Object>> relatorioGeral(
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

        return ResponseEntity.ok(
                relatorioService.relatorioGeral(idUnidade));
    }
}

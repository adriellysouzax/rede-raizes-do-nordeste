
package br.com.raizesdonordeste.controller;

import br.com.raizesdonordeste.model.Pedido;
import br.com.raizesdonordeste.repository.PedidoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/pedidos")
public class PreparoController {

    private final PedidoRepository pedidoRepository;

    public PreparoController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @PatchMapping("/{id}/preparo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'COZINHA')")
    public Pedido atualizarPreparo(
            @PathVariable Long id,
            @Valid @RequestBody AtualizacaoPreparo dados,
            Authentication auth) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pedido não encontrado."));

        boolean admin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!admin) {
            Jwt jwt = (Jwt) auth.getPrincipal();
            Number idUnidade = jwt.getClaim("idUnidade");

            if (pedido.getUnidade() == null
                    || idUnidade == null
                    || !pedido.getUnidade().getIdUnidade()
                            .equals(idUnidade.longValue())) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Pedido de outra unidade.");
            }
        }

        if (!"PAGO".equals(pedido.getStatus())
                && !"EM_PREPARO".equals(pedido.getStatus())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O pedido precisa estar pago ou em preparo.");
        }

        if ("EM_PREPARO".equals(pedido.getStatus())
                && dados.status() != StatusPreparo.PRONTO) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O pedido em preparo só pode avançar para PRONTO.");
        }

        pedido.setStatus(dados.status().name());

        return pedidoRepository.save(pedido);
    }

    public enum StatusPreparo {
        EM_PREPARO,
        PRONTO
    }

    public record AtualizacaoPreparo(
            @NotNull StatusPreparo status
    ) {}
}

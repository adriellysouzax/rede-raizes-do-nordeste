
package br.com.raizesdonordeste.controller;

import br.com.raizesdonordeste.dto.PedidoCadastroDTO;
import br.com.raizesdonordeste.model.*;
import br.com.raizesdonordeste.service.PedidoService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE', 'COZINHA')")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    private boolean ehAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private Long unidadeDoFuncionario(Authentication auth) {
        Jwt jwt = (Jwt) auth.getPrincipal();
        Number id = jwt.getClaim("idUnidade");

        if (id == null || id.longValue() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Funcionário sem unidade vinculada.");
        }

        return id.longValue();
    }

    private void verificarUnidade(
            Long idUnidade, Authentication auth) {

        if (!ehAdmin(auth)
                && !unidadeDoFuncionario(auth).equals(idUnidade)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Pedido de outra unidade.");
        }
    }

    private void verificarPedido(
            Pedido pedido, Authentication auth) {

        if (ehAdmin(auth)) {
            return;
        }

        if (pedido.getUnidade() == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Pedido antigo sem unidade vinculada.");
        }

        verificarUnidade(
                pedido.getUnidade().getIdUnidade(), auth);
    }

    // ADMIN, GERENTE e ATENDENTE cadastram pedidos
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    @ResponseStatus(HttpStatus.CREATED)
    public Pedido cadastrarPedido(
            @Valid @RequestBody PedidoCadastroDTO dados,
            Authentication auth) {

        verificarUnidade(dados.idUnidade(), auth);

        return pedidoService.cadastrarPedido(dados);
    }

    // Funcionários consultam somente pedidos da própria unidade
    @GetMapping
    public List<Pedido> listarPedidos(
            @RequestParam(required = false) CanalPedido canalPedido,
            Authentication auth) {

        List<Pedido> pedidos = canalPedido != null
                ? pedidoService.listarPorCanal(canalPedido)
                : pedidoService.listarPedidos();

        if (ehAdmin(auth)) {
            return pedidos;
        }

        Long idUnidade = unidadeDoFuncionario(auth);

        return pedidos.stream()
                .filter(p -> p.getUnidade() != null)
                .filter(p -> idUnidade.equals(
                        p.getUnidade().getIdUnidade()))
                .toList();
    }

    @GetMapping("/{id}")
    public Pedido buscarPorId(
            @PathVariable Long id,
            Authentication auth) {

        Pedido pedido = pedidoService.buscarPorId(id);
        verificarPedido(pedido, auth);

        return pedido;
    }

    @GetMapping("/cliente/{idCliente}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public List<Pedido> listarPorCliente(
            @PathVariable Long idCliente,
            Authentication auth) {

        List<Pedido> pedidos = pedidoService.listarPorCliente(idCliente);

        if (ehAdmin(auth)) {
            return pedidos;
        }

        Long idUnidade = unidadeDoFuncionario(auth);

        return pedidos.stream()
                .filter(p -> p.getUnidade() != null)
                .filter(p -> idUnidade.equals(
                        p.getUnidade().getIdUnidade()))
                .toList();
    }

    // Somente ADMIN e GERENTE confirmam pagamento
    @PatchMapping("/{id}/pagamento")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public Pedido confirmarPagamento(
            @PathVariable Long id,
            @RequestBody @Valid PagamentoRequest dados,
            Authentication auth) {

        Pedido pedido = pedidoService.buscarPorId(id);
        verificarPedido(pedido, auth);

        return pedidoService.confirmarPagamento(
                id, dados.resultado());
    }

    // ADMIN e GERENTE cancelam pedidos não pagos
    @PatchMapping("/{id}/cancelamento")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public Pedido cancelarPedido(
            @PathVariable Long id,
            Authentication auth) {

        Pedido pedido = pedidoService.buscarPorId(id);
        verificarPedido(pedido, auth);

        return pedidoService.cancelarPedido(id);
    }

    public record PagamentoRequest(
            @NotNull(message = "O resultado é obrigatório") ResultadoPagamento resultado) {
    }
}

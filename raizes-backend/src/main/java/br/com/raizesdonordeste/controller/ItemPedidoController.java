
package br.com.raizesdonordeste.controller;

import br.com.raizesdonordeste.model.ItemPedido;
import br.com.raizesdonordeste.service.ItemPedidoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/itens-pedido")
public class ItemPedidoController {

    private final ItemPedidoService itemPedidoService;

    public ItemPedidoController(
            ItemPedidoService itemPedidoService) {
        this.itemPedidoService = itemPedidoService;
    }

    @PreAuthorize("hasRole('GERENTE')")
    @GetMapping("/pedido/{idPedido}")
    public List<ItemPedido> listarPorPedido(
            @PathVariable Long idPedido) {
        return itemPedidoService.listarPorPedido(idPedido);
    }
}

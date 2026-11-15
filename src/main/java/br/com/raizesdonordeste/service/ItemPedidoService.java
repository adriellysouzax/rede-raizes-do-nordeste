
package br.com.raizesdonordeste.service;

import br.com.raizesdonordeste.model.ItemPedido;
import br.com.raizesdonordeste.repository.ItemPedidoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ItemPedidoService {

    private final ItemPedidoRepository itemPedidoRepository;

    public ItemPedidoService(
            ItemPedidoRepository itemPedidoRepository) {
        this.itemPedidoRepository = itemPedidoRepository;
    }

    // Lista os itens de um pedido
    public List<ItemPedido> listarPorPedido(Long idPedido) {
        return itemPedidoRepository.findByPedido_IdPedido(idPedido);
    }
}

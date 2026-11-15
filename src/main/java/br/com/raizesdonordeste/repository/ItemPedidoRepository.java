
package br.com.raizesdonordeste.repository;

import br.com.raizesdonordeste.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    // Busca os itens de um pedido
    List<ItemPedido> findByPedido_IdPedido(Long idPedido);
}

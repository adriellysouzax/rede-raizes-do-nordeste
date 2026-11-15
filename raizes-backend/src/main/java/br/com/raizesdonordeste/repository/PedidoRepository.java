
package br.com.raizesdonordeste.repository;

import br.com.raizesdonordeste.model.CanalPedido;
import br.com.raizesdonordeste.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByCliente_IdCliente(Long idCliente);

    List<Pedido> findByCanalPedido(CanalPedido canalPedido);
}

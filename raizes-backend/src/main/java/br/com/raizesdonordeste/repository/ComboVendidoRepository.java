
package br.com.raizesdonordeste.repository;

import br.com.raizesdonordeste.model.ComboVendido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComboVendidoRepository
        extends JpaRepository<ComboVendido, Long> {

    List<ComboVendido> findByPedido_IdPedido(Long idPedido);
}

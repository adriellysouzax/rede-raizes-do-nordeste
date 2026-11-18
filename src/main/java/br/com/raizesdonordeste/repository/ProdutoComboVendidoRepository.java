
package br.com.raizesdonordeste.repository;

import br.com.raizesdonordeste.model.ProdutoComboVendido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoComboVendidoRepository
        extends JpaRepository<ProdutoComboVendido, Long> {

    List<ProdutoComboVendido> findByComboVendido_IdComboVendido(
            Long idComboVendido);
}

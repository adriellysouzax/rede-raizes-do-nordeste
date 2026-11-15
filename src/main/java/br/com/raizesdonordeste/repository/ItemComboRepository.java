
package br.com.raizesdonordeste.repository;

import br.com.raizesdonordeste.model.ItemCombo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemComboRepository extends JpaRepository<ItemCombo, Long> {

    List<ItemCombo> findByCombo_IdCombo(Long idCombo);

    boolean existsByCombo_IdComboAndProduto_IdProduto(
            Long idCombo, Long idProduto);
}

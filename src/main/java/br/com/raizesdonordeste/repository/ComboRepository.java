
package br.com.raizesdonordeste.repository;

import br.com.raizesdonordeste.model.Combo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComboRepository extends JpaRepository<Combo, Long> {

    List<Combo> findByAtivoTrue();
}

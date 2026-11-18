
package br.com.raizesdonordeste.repository;

import br.com.raizesdonordeste.model.Fidelidade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FidelidadeRepository
        extends JpaRepository<Fidelidade, Long> {

    Optional<Fidelidade> findByCliente_IdCliente(Long idCliente);
}

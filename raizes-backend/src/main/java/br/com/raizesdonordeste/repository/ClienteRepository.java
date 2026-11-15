
package br.com.raizesdonordeste.repository;

import br.com.raizesdonordeste.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Verifica se o e-mail já está cadastrado
    boolean existsByEmail(String email);
}

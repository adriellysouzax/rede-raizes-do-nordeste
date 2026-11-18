
package br.com.raizesdonordeste.repository;

import br.com.raizesdonordeste.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProdutoRepository
        extends JpaRepository<Produto, Long> {

    // Busca apenas os produtos disponíveis
    List<Produto> findByDisponivelTrue();
}

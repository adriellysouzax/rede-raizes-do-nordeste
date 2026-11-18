
package br.com.raizesdonordeste.repository;

import br.com.raizesdonordeste.model.Estoque;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    List<Estoque> findByUnidade_IdUnidade(Long idUnidade);

    Optional<Estoque> findByProduto_IdProdutoAndUnidade_IdUnidade(
            Long idProduto,
            Long idUnidade
    );

    boolean existsByProduto_IdProdutoAndUnidade_IdUnidade(
            Long idProduto,
            Long idUnidade
    );

    Optional<Estoque> findByProduto_IdProduto(Long idProduto);

    // Bloqueia o registro até a transação terminar
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT e FROM Estoque e
        WHERE e.produto.idProduto = :idProduto
          AND e.unidade.idUnidade = :idUnidade
        """)
    Optional<Estoque> buscarComBloqueio(
            @Param("idProduto") Long idProduto,
            @Param("idUnidade") Long idUnidade
    );
}

package br.com.raizesdonordeste.repository;

import br.com.raizesdonordeste.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository
        extends JpaRepository<Funcionario, Long> {

    // Busca funcionário pelo usuário para autenticação
    Optional<Funcionario> findByUsuario(String usuario);

    // Verifica se o usuário já está cadastrado
    boolean existsByUsuario(String usuario);

    // Lista funcionários de uma unidade
    List<Funcionario> findByUnidade_IdUnidade(Long idUnidade);
}

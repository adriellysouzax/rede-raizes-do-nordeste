
package br.com.raizesdonordeste.service;

import br.com.raizesdonordeste.model.Estoque;
import br.com.raizesdonordeste.repository.EstoqueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;

    public EstoqueService(EstoqueRepository estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }

    // Lista todos os estoques
    public List<Estoque> listarEstoque() {
        return estoqueRepository.findAll();
    }

    // Lista os estoques de uma unidade
    public List<Estoque> listarPorUnidade(Long idUnidade) {
        return estoqueRepository.findByUnidade_IdUnidade(idUnidade);
    }

    // Consulta o estoque de um produto em uma unidade
    public Estoque buscarPorProdutoEUnidade(
            Long idProduto,
            Long idUnidade) {

        return estoqueRepository
                .findByProduto_IdProdutoAndUnidade_IdUnidade(
                        idProduto,
                        idUnidade
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Estoque não encontrado nesta unidade."
                        )
                );
    }

    // Método temporário para manter o controller atual compilando
    public Estoque buscarPorProduto(Long idProduto) {
        return estoqueRepository.findByProduto_IdProduto(idProduto)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Estoque não encontrado."
                        )
                );
    }
}


package br.com.raizesdonordeste.service;

import br.com.raizesdonordeste.model.Produto;
import br.com.raizesdonordeste.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    // Mostra apenas os produtos disponíveis
    public List<Produto> listarCardapio() {
        return produtoRepository.findByDisponivelTrue();
    }

    // Lista todos os produtos
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    // Cadastra um produto
    public Produto cadastrarProduto(Produto produto) {
        return produtoRepository.save(produto);
    }

    // Altera a disponibilidade de um produto
    public Produto alterarDisponibilidade(
            Long idProduto, Boolean disponivel) {

        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Produto não encontrado."
                    )
                );

        produto.setDisponivel(disponivel);

        return produtoRepository.save(produto);
    }
}

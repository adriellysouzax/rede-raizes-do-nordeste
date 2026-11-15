
package br.com.raizesdonordeste.service;

import br.com.raizesdonordeste.model.Estoque;
import br.com.raizesdonordeste.repository.EstoqueRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RelatorioEstoqueService {

    private final EstoqueRepository estoqueRepository;

    public RelatorioEstoqueService(
            EstoqueRepository estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }

    public List<Map<String, Object>> consultarEstoque(Long idUnidade) {

        List<Estoque> estoques = idUnidade == null
                ? estoqueRepository.findAll()
                : estoqueRepository.findByUnidade_IdUnidade(idUnidade);

        // Evita erros com registros sem unidade ou produto
        estoques = new ArrayList<>(estoques);

        estoques.removeIf(estoque ->
                estoque.getUnidade() == null ||
                estoque.getProduto() == null
        );

        // Organiza o estoque por unidade e produto
        estoques.sort((a, b) -> {

            int compararUnidade = Long.compare(
                    a.getUnidade().getIdUnidade(),
                    b.getUnidade().getIdUnidade()
            );

            if (compararUnidade != 0) {
                return compararUnidade;
            }

            return Long.compare(
                    a.getProduto().getIdProduto(),
                    b.getProduto().getIdProduto()
            );
        });

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Estoque estoque : estoques) {

            Map<String, Object> linha = new LinkedHashMap<>();

            linha.put("idUnidade",
                    estoque.getUnidade().getIdUnidade());

            linha.put("unidade",
                    estoque.getUnidade().getNome());

            linha.put("idProduto",
                    estoque.getProduto().getIdProduto());

            linha.put("produto",
                    estoque.getProduto().getNome());

            linha.put("quantidade",
                    estoque.getQuantidade());

            String situacao;

            if (estoque.getQuantidade() == null) {
                situacao = "NAO_INFORMADO";
            } else if (estoque.getQuantidade() == 0) {
                situacao = "ESGOTADO";
            } else if (estoque.getQuantidade() <= 5) {
                situacao = "ESTOQUE_BAIXO";
            } else {
                situacao = "DISPONIVEL";
            }

            linha.put("situacao", situacao);

            resultado.add(linha);
        }

        return resultado;
    }
}

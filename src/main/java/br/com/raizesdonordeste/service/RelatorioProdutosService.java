
package br.com.raizesdonordeste.service;

import br.com.raizesdonordeste.model.*;
import br.com.raizesdonordeste.repository.*;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RelatorioProdutosService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final ComboVendidoRepository comboVendidoRepository;
    private final ProdutoComboVendidoRepository produtoComboVendidoRepository;

    public RelatorioProdutosService(
            PedidoRepository pedidoRepository,
            ItemPedidoRepository itemPedidoRepository,
            ComboVendidoRepository comboVendidoRepository,
            ProdutoComboVendidoRepository produtoComboVendidoRepository) {

        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.comboVendidoRepository = comboVendidoRepository;
        this.produtoComboVendidoRepository = produtoComboVendidoRepository;
    }

    public List<Map<String, Object>> produtosMaisVendidos(Long idUnidade) {

        Map<Long, Integer> quantidades = new HashMap<>();
        Map<Long, String> nomes = new HashMap<>();

        for (Pedido pedido : pedidoRepository.findAll()) {

            if (idUnidade != null &&
                    (pedido.getUnidade() == null ||
                    !idUnidade.equals(pedido.getUnidade().getIdUnidade()))) {
                continue;
            }

            if (!List.of("PAGO", "EM_PREPARO", "PRONTO")
                    .contains(pedido.getStatus())) {
                continue;
            }

            for (ItemPedido item :
                    itemPedidoRepository.findByPedido_IdPedido(
                            pedido.getIdPedido())) {

                Long id = item.getProduto().getIdProduto();

                quantidades.merge(id, item.getQuantidade(), Integer::sum);
                nomes.put(id, item.getProduto().getNome());
            }

            for (ComboVendido combo :
                    comboVendidoRepository.findByPedido_IdPedido(
                            pedido.getIdPedido())) {

                for (ProdutoComboVendido produto :
                        produtoComboVendidoRepository
                                .findByComboVendido_IdComboVendido(
                                        combo.getIdComboVendido())) {

                    Long id = produto.getProduto().getIdProduto();

                    quantidades.merge(
                            id, produto.getQuantidade(), Integer::sum);

                    nomes.put(id, produto.getProduto().getNome());
                }
            }
        }

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (var entrada : quantidades.entrySet()) {

            Map<String, Object> linha = new LinkedHashMap<>();
            linha.put("idProduto", entrada.getKey());
            linha.put("nome", nomes.get(entrada.getKey()));
            linha.put("quantidadeVendida", entrada.getValue());

            resultado.add(linha);
        }

        resultado.sort((a, b) -> Integer.compare(
                (Integer) b.get("quantidadeVendida"),
                (Integer) a.get("quantidadeVendida")));

        return resultado;
    }
}

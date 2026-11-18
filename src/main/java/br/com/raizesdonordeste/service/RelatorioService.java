
package br.com.raizesdonordeste.service;

import br.com.raizesdonordeste.model.Pedido;
import br.com.raizesdonordeste.repository.PedidoRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RelatorioService {

    private final PedidoRepository pedidoRepository;

    public RelatorioService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public Map<String, Object> relatorioGeral(Long idUnidade) {

        List<Pedido> pedidos = pedidoRepository.findAll();

        if (idUnidade != null) {
            pedidos = pedidos.stream()
                    .filter(p -> p.getUnidade() != null
                            && idUnidade.equals(
                                    p.getUnidade().getIdUnidade()))
                    .toList();
        }

        long totalPedidos = pedidos.size();

        long pedidosPagos = pedidos.stream()
                .filter(p -> "PAGO".equals(p.getStatus())
                        || "EM_PREPARO".equals(p.getStatus())
                        || "PRONTO".equals(p.getStatus()))
                .count();

        long pedidosCancelados = pedidos.stream()
                .filter(p -> "CANCELADO".equals(p.getStatus()))
                .count();

        BigDecimal totalVendas = pedidos.stream()
                .filter(p -> "PAGO".equals(p.getStatus())
                        || "EM_PREPARO".equals(p.getStatus())
                        || "PRONTO".equals(p.getStatus()))
                .map(Pedido::getValorTotal)
                .filter(valor -> valor != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> relatorio = new HashMap<>();

        relatorio.put("idUnidade", idUnidade);
        relatorio.put("totalPedidos", totalPedidos);
        relatorio.put("pedidosPagos", pedidosPagos);
        relatorio.put("pedidosCancelados", pedidosCancelados);
        relatorio.put("totalVendas", totalVendas);

        return relatorio;
    }
}

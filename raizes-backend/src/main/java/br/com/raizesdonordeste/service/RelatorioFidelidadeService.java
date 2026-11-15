
package br.com.raizesdonordeste.service;

import br.com.raizesdonordeste.model.Pedido;
import br.com.raizesdonordeste.repository.PedidoRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RelatorioFidelidadeService {

    private final PedidoRepository pedidoRepository;

    public RelatorioFidelidadeService(
            PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public Map<String, Object> consultar(Long idUnidade) {

        List<Pedido> pedidos = pedidoRepository.findAll();

        long pedidosComResgate = 0;
        long pontosGeradosEstimados = 0;
        long pontosResgatadosEstimados = 0;
        BigDecimal totalDescontos = BigDecimal.ZERO;

        for (Pedido pedido : pedidos) {

            if (idUnidade != null &&
                    (pedido.getUnidade() == null ||
                    !idUnidade.equals(
                            pedido.getUnidade().getIdUnidade()))) {
                continue;
            }

            boolean pago = List.of(
                    "PAGO", "EM_PREPARO", "PRONTO"
            ).contains(pedido.getStatus());

            if (!pago) {
                continue;
            }

            BigDecimal desconto = pedido.getDesconto() == null
                    ? BigDecimal.ZERO
                    : pedido.getDesconto();

            BigDecimal taxaEntrega = pedido.getTaxaEntrega() == null
                    ? BigDecimal.ZERO
                    : pedido.getTaxaEntrega();

            BigDecimal valorTotal = pedido.getValorTotal() == null
                    ? BigDecimal.ZERO
                    : pedido.getValorTotal();

            BigDecimal valorConsiderado = valorTotal
                    .subtract(taxaEntrega)
                    .max(BigDecimal.ZERO);

            long pontos = valorConsiderado
                    .divide(new BigDecimal("5.00"), 0,
                            RoundingMode.DOWN)
                    .longValue();

            pontosGeradosEstimados += pontos;

            if (Boolean.TRUE.equals(pedido.getUsarPontos())) {
                pedidosComResgate++;
                pontosResgatadosEstimados++;
                totalDescontos = totalDescontos.add(desconto);
            }
        }

        Map<String, Object> resultado = new LinkedHashMap<>();

        resultado.put("idUnidade", idUnidade);
        resultado.put("pedidosComResgate", pedidosComResgate);
        resultado.put("pontosGeradosEstimados",
                pontosGeradosEstimados);
        resultado.put("resgatesRealizadosEstimados",
                pontosResgatadosEstimados);
        resultado.put("totalDescontos", totalDescontos);

        return resultado;
    }
}


package br.com.raizesdonordeste.service;

import br.com.raizesdonordeste.model.ResultadoPagamento;
import org.springframework.stereotype.Service;

@Service
public class PagamentoGatewaySimulado {

    public ResultadoPagamento processarPagamento(
            ResultadoPagamento resultadoSimulado) {

        if (resultadoSimulado == null) {
            throw new IllegalArgumentException(
                    "O resultado do pagamento é obrigatório.");
        }

        return resultadoSimulado;
    }
}

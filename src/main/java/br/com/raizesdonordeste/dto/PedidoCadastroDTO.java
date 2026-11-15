
package br.com.raizesdonordeste.dto;

import br.com.raizesdonordeste.model.CanalPedido;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record PedidoCadastroDTO(

    @NotNull(message = "O cliente é obrigatório")
    @Positive
    Long idCliente,

    @NotNull(message = "A unidade é obrigatória")
    @Positive
    Long idUnidade,

    List<@Valid ItemPedidoDTO> itens,

    List<@Valid ComboPedidoDTO> combos,

    @NotNull(message = "O canal do pedido é obrigatório")
    CanalPedido canalPedido,

    boolean entrega,

    boolean usarPontos

) {
}

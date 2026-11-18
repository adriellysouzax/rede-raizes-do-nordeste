
package br.com.raizesdonordeste.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemPedidoDTO(

    @NotNull(message = "O produto é obrigatório")
    Long idProduto,

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade mínima é 1")
    Integer quantidade

) {
}



package br.com.raizesdonordeste.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ComboPedidoDTO(

    @NotNull(message = "O combo é obrigatório")
    @Positive
    Long idCombo,

    @NotNull(message = "A quantidade é obrigatória")
    @Positive
    Integer quantidade

) {
}

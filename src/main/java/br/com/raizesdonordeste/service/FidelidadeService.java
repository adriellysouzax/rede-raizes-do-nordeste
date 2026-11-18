
package br.com.raizesdonordeste.service;

import br.com.raizesdonordeste.model.Cliente;
import br.com.raizesdonordeste.model.Fidelidade;
import br.com.raizesdonordeste.repository.ClienteRepository;
import br.com.raizesdonordeste.repository.FidelidadeRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class FidelidadeService {

    private final FidelidadeRepository fidelidadeRepository;
    private final ClienteRepository clienteRepository;

    public FidelidadeService(
            FidelidadeRepository fidelidadeRepository,
            ClienteRepository clienteRepository) {

        this.fidelidadeRepository = fidelidadeRepository;
        this.clienteRepository = clienteRepository;
    }

    public Integer consultarPontos(Long idCliente) {
        return fidelidadeRepository
                .findByCliente_IdCliente(idCliente)
                .map(Fidelidade::getPontos)
                .orElse(0);
    }

    public void validarResgate(Long idCliente) {

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cliente não encontrado."));

        if (!Boolean.TRUE.equals(cliente.getAceitaFidelidade())) {
            throw new IllegalArgumentException(
                    "Cliente não participa da fidelidade.");
        }

        if (consultarPontos(idCliente) < 100) {
            throw new IllegalArgumentException(
                    "São necessários pelo menos 100 pontos.");
        }
    }

    @Transactional
    public void adicionarPontos(
            Long idCliente, BigDecimal valorCompra) {

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cliente não encontrado."));

        if (!Boolean.TRUE.equals(cliente.getAceitaFidelidade())) {
            return;
        }

        if (valorCompra == null
                || valorCompra.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "O valor da compra deve ser positivo.");
        }

        int pontosGanhos = valorCompra
                .divide(new BigDecimal("5"), 0, RoundingMode.DOWN)
                .intValueExact();

        Fidelidade fidelidade = fidelidadeRepository
                .findByCliente_IdCliente(idCliente)
                .orElseGet(() -> {
                    Fidelidade nova = new Fidelidade();
                    nova.setCliente(cliente);
                    nova.setPontos(0);
                    return nova;
                });

        fidelidade.setPontos(
                fidelidade.getPontos() + pontosGanhos);

        fidelidadeRepository.save(fidelidade);
    }

    @Transactional
    public BigDecimal resgatarPontos(Long idCliente) {

        validarResgate(idCliente);

        Fidelidade fidelidade = fidelidadeRepository
                .findByCliente_IdCliente(idCliente)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Saldo não encontrado."));

        fidelidade.setPontos(fidelidade.getPontos() - 100);
        fidelidadeRepository.save(fidelidade);

        return new BigDecimal("5.00");
    }
}

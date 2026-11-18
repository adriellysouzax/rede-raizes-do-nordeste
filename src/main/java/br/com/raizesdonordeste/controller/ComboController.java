
package br.com.raizesdonordeste.controller;

import br.com.raizesdonordeste.model.Combo;
import br.com.raizesdonordeste.model.ItemCombo;
import br.com.raizesdonordeste.repository.ComboRepository;
import br.com.raizesdonordeste.repository.ItemComboRepository;
import br.com.raizesdonordeste.repository.ProdutoRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/combos")
public class ComboController {

    private final ComboRepository comboRepository;
    private final ItemComboRepository itemComboRepository;
    private final ProdutoRepository produtoRepository;

    public ComboController(
            ComboRepository comboRepository,
            ItemComboRepository itemComboRepository,
            ProdutoRepository produtoRepository) {

        this.comboRepository = comboRepository;
        this.itemComboRepository = itemComboRepository;
        this.produtoRepository = produtoRepository;
    }

    // Administrador e gerente podem consultar os combos
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public List<Combo> listar() {
        return comboRepository.findAll();
    }

    // Administrador e gerente podem cadastrar combos
    @PostMapping
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @ResponseStatus(HttpStatus.CREATED)
    public Combo cadastrar(@Valid @RequestBody ComboCadastro dados) {

        if (dados.dataFim().isBefore(dados.dataInicio())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data final não pode ser anterior à inicial.");
        }

        if (dados.itens().stream()
                .map(ItemComboCadastro::idProduto)
                .distinct().count() != dados.itens().size()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não repita produtos no mesmo combo.");
        }

        Combo combo = new Combo();
        combo.setNome(dados.nome());
        combo.setDescricao(dados.descricao());
        combo.setPrecoPromocional(dados.precoPromocional());
        combo.setDataInicio(dados.dataInicio());
        combo.setDataFim(dados.dataFim());
        combo.setAtivo(true);

        Combo salvo = comboRepository.save(combo);

        for (ItemComboCadastro dadosItem : dados.itens()) {

            var produto = produtoRepository
                    .findById(dadosItem.idProduto())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Produto não encontrado: "
                                    + dadosItem.idProduto()));

            ItemCombo item = new ItemCombo();
            item.setCombo(salvo);
            item.setProduto(produto);
            item.setQuantidade(dadosItem.quantidade());

            itemComboRepository.save(item);
        }

        return salvo;
    }

    // Consulta os produtos de um combo
    @GetMapping("/{id}/itens")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public List<ItemCombo> listarItens(@PathVariable Long id) {

        if (!comboRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Combo não encontrado.");
        }

        return itemComboRepository.findByCombo_IdCombo(id);
    }

    public record ComboCadastro(
            @NotBlank String nome,
            String descricao,
            @NotNull @DecimalMin("0.01")
            BigDecimal precoPromocional,
            @NotNull LocalDate dataInicio,
            @NotNull LocalDate dataFim,
            @NotEmpty List<@Valid ItemComboCadastro> itens
    ) {}

    public record ItemComboCadastro(
            @NotNull @Positive Long idProduto,
            @NotNull @Positive Integer quantidade
    ) {}
}

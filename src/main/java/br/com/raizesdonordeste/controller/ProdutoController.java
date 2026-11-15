
package br.com.raizesdonordeste.controller;

import br.com.raizesdonordeste.model.Produto;
import br.com.raizesdonordeste.service.ProdutoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    // Cardápio disponível para consulta
    @GetMapping("/cardapio")
    public List<Produto> listarCardapio() {
        return produtoService.listarCardapio();
    }

    // Consulta administrativa
    @PreAuthorize("hasRole('GERENTE')")
    @GetMapping
    public List<Produto> listarTodos() {
        return produtoService.listarTodos();
    }

    // Cadastro exclusivo do gerente
    @PreAuthorize("hasRole('GERENTE')")
    @PostMapping
    public Produto cadastrarProduto(@RequestBody Produto produto) {
        return produtoService.cadastrarProduto(produto);
    }

    // Alteração exclusiva do gerente
    @PreAuthorize("hasRole('GERENTE')")
    @PatchMapping("/{id}/disponibilidade")
    public Produto alterarDisponibilidade(
            @PathVariable Long id,
            @RequestParam Boolean disponivel) {

        return produtoService.alterarDisponibilidade(id, disponivel);
    }
}




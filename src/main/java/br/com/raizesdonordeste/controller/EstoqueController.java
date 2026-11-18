
package br.com.raizesdonordeste.controller;

import br.com.raizesdonordeste.model.Estoque;
import br.com.raizesdonordeste.repository.EstoqueRepository;
import br.com.raizesdonordeste.repository.ProdutoRepository;
import br.com.raizesdonordeste.repository.UnidadeRepository;
import br.com.raizesdonordeste.service.EstoqueService;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/estoque")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
public class EstoqueController {

    private final EstoqueService estoqueService;
    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;
    private final UnidadeRepository unidadeRepository;

    public EstoqueController(
            EstoqueService estoqueService,
            EstoqueRepository estoqueRepository,
            ProdutoRepository produtoRepository,
            UnidadeRepository unidadeRepository) {

        this.estoqueService = estoqueService;
        this.estoqueRepository = estoqueRepository;
        this.produtoRepository = produtoRepository;
        this.unidadeRepository = unidadeRepository;
    }

    // Identifica se o funcionário é administrador geral
    private boolean ehAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // Confere se o gerente pertence à unidade solicitada
    private void verificarUnidade(
            Long idUnidade,
            Authentication authentication) {

        if (ehAdmin(authentication)) {
            return;
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Number unidadeToken = jwt.getClaim("idUnidade");

        if (unidadeToken == null ||
                unidadeToken.longValue() != idUnidade.longValue()) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não tem permissão para acessar esta unidade.");
        }
    }

    // Administrador consulta tudo; gerente consulta sua unidade
    @GetMapping
    public List<Estoque> listarEstoque(
            Authentication authentication) {

        if (ehAdmin(authentication)) {
            return estoqueService.listarEstoque();
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Number unidadeToken = jwt.getClaim("idUnidade");

        if (unidadeToken == null || unidadeToken.longValue() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Funcionário sem unidade vinculada.");
        }

        return estoqueService.listarPorUnidade(
                unidadeToken.longValue());
    }

    // Consulta estoque de uma unidade
    @GetMapping("/unidade/{idUnidade}")
    public List<Estoque> listarPorUnidade(
            @PathVariable Long idUnidade,
            Authentication authentication) {

        verificarUnidade(idUnidade, authentication);

        return estoqueService.listarPorUnidade(idUnidade);
    }

    // Consulta um produto em uma unidade
    @GetMapping("/produto/{idProduto}/unidade/{idUnidade}")
    public Estoque buscarPorProdutoEUnidade(
            @PathVariable Long idProduto,
            @PathVariable Long idUnidade,
            Authentication authentication) {

        verificarUnidade(idUnidade, authentication);

        return estoqueService.buscarPorProdutoEUnidade(
                idProduto, idUnidade);
    }

    // Cadastra estoque, respeitando a unidade do gerente
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Estoque cadastrarEstoque(
            @RequestBody Map<String, Integer> dados,
            Authentication authentication) {

        Integer idProduto = dados.get("idProduto");
        Integer idUnidade = dados.get("idUnidade");
        Integer quantidade = dados.get("quantidade");
        Integer estoqueMinimo = dados.get("estoqueMinimo");

        if (idProduto == null || idUnidade == null
                || quantidade == null || estoqueMinimo == null
                || idProduto <= 0 || idUnidade <= 0
                || quantidade < 0 || estoqueMinimo < 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe produto, unidade e quantidades válidas.");
        }

        verificarUnidade(idUnidade.longValue(), authentication);

        var produto = produtoRepository
                .findById(idProduto.longValue())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Produto não encontrado."));

        var unidade = unidadeRepository
                .findById(idUnidade.longValue())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Unidade não encontrada."));

        if (estoqueRepository
                .existsByProduto_IdProdutoAndUnidade_IdUnidade(
                        idProduto.longValue(),
                        idUnidade.longValue())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este produto já possui estoque nesta unidade.");
        }

        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setUnidade(unidade);
        estoque.setQuantidade(quantidade);
        estoque.setEstoqueMinimo(estoqueMinimo);

        return estoqueRepository.save(estoque);
    }
}

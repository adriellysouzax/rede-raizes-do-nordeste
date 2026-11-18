
package br.com.raizesdonordeste.service;

import br.com.raizesdonordeste.dto.*;
import br.com.raizesdonordeste.model.*;
import br.com.raizesdonordeste.repository.*;
import br.com.raizesdonordeste.service.AuditoriaService;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final AuditoriaService auditoriaService;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final UnidadeRepository unidadeRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final EstoqueRepository estoqueRepository;
    private final FidelidadeService fidelidadeService;
    private final PagamentoGatewaySimulado pagamentoGateway;
    private final ComboRepository comboRepository;
    private final ItemComboRepository itemComboRepository;
    private final ComboVendidoRepository comboVendidoRepository;
    private final ProdutoComboVendidoRepository produtoComboVendidoRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            AuditoriaService auditoriaService,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository,
            UnidadeRepository unidadeRepository,
            ItemPedidoRepository itemPedidoRepository,
            EstoqueRepository estoqueRepository,
            FidelidadeService fidelidadeService,
            PagamentoGatewaySimulado pagamentoGateway,
            ComboRepository comboRepository,
            ItemComboRepository itemComboRepository,
            ComboVendidoRepository comboVendidoRepository,
            ProdutoComboVendidoRepository produtoComboVendidoRepository) {

        this.pedidoRepository = pedidoRepository;
        this.auditoriaService = auditoriaService;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.unidadeRepository = unidadeRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.estoqueRepository = estoqueRepository;
        this.fidelidadeService = fidelidadeService;
        this.pagamentoGateway = pagamentoGateway;
        this.comboRepository = comboRepository;
        this.itemComboRepository = itemComboRepository;
        this.comboVendidoRepository = comboVendidoRepository;
        this.produtoComboVendidoRepository = produtoComboVendidoRepository;
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> listarPorCanal(CanalPedido canal) {
        return pedidoRepository.findByCanalPedido(canal);
    }

    public List<Pedido> listarPorCliente(Long idCliente) {
        return pedidoRepository.findByCliente_IdCliente(idCliente);
    }

    public Pedido buscarPorId(Long idPedido) {
        return pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Pedido nÃ£o encontrado."));
    }

    @Transactional
    public Pedido cadastrarPedido(PedidoCadastroDTO dados) {

        Cliente cliente = clienteRepository.findById(dados.idCliente())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cliente nÃ£o encontrado."));

        Unidade unidade = unidadeRepository.findById(dados.idUnidade())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unidade nÃ£o encontrada."));

        if (!Boolean.TRUE.equals(unidade.getAtiva())) {
            throw new IllegalArgumentException("Unidade inativa.");
        }

        List<ItemPedidoDTO> produtos = dados.itens() == null
                ? List.of() : dados.itens();

        List<ComboPedidoDTO> combos = dados.combos() == null
                ? List.of() : dados.combos();

        if (produtos.isEmpty() && combos.isEmpty()) {
            throw new IllegalArgumentException(
                    "Informe pelo menos um produto ou combo.");
        }

        if (dados.usarPontos() && !combos.isEmpty()) {
            throw new IllegalArgumentException(
                    "Pedidos com combos nÃ£o permitem resgate de pontos.");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setUnidade(unidade);
        pedido.setCanalPedido(dados.canalPedido());
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus("AGUARDANDO_PAGAMENTO");
        pedido.setUsarPontos(dados.usarPontos());

        Map<Long, Integer> quantidadesEstoque = new TreeMap<>();
        Map<Long, Integer> quantidadesProdutos = new LinkedHashMap<>();
        Map<Long, Integer> quantidadesCombos = new LinkedHashMap<>();
        Map<Long, Combo> combosCarregados = new HashMap<>();
        Map<Long, List<ItemCombo>> composicoes = new HashMap<>();

        BigDecimal subtotal = BigDecimal.ZERO;

        // Produtos individuais
        for (ItemPedidoDTO item : produtos) {
            if (item == null || item.idProduto() == null
                    || item.quantidade() == null
                    || item.quantidade() <= 0) {
                throw new IllegalArgumentException(
                        "Produto ou quantidade invÃ¡lida.");
            }

            quantidadesProdutos.merge(
                    item.idProduto(), item.quantidade(), Math::addExact);

            quantidadesEstoque.merge(
                    item.idProduto(), item.quantidade(), Math::addExact);
        }

        // Agrupa combos repetidos
        for (ComboPedidoDTO item : combos) {
            if (item == null || item.idCombo() == null
                    || item.quantidade() == null
                    || item.quantidade() <= 0) {
                throw new IllegalArgumentException(
                        "Combo ou quantidade invÃ¡lida.");
            }

            quantidadesCombos.merge(
                    item.idCombo(), item.quantidade(), Math::addExact);
        }

        LocalDate hoje = LocalDate.now();

        for (var entrada : quantidadesCombos.entrySet()) {

            Long idCombo = entrada.getKey();
            Integer quantidadeCombo = entrada.getValue();

            Combo combo = comboRepository.findById(idCombo)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Combo nÃ£o encontrado."));

            if (!Boolean.TRUE.equals(combo.getAtivo())
                    || combo.getDataInicio() == null
                    || combo.getDataFim() == null
                    || hoje.isBefore(combo.getDataInicio())
                    || hoje.isAfter(combo.getDataFim())) {
                throw new IllegalArgumentException(
                        "Combo fora da validade ou inativo.");
            }

            if (combo.getPrecoPromocional() == null
                    || combo.getPrecoPromocional()
                            .compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "PreÃ§o do combo invÃ¡lido.");
            }

            List<ItemCombo> composicao =
                    itemComboRepository.findByCombo_IdCombo(idCombo);

            if (composicao.isEmpty()) {
                throw new IllegalArgumentException(
                        "Combo sem produtos cadastrados.");
            }

            for (ItemCombo componente : composicao) {

                if (componente.getQuantidade() == null
                        || componente.getQuantidade() <= 0) {
                    throw new IllegalArgumentException(
                            "Quantidade invÃ¡lida na composiÃ§Ã£o do combo.");
                }

                Long idProduto =
                        componente.getProduto().getIdProduto();

                int quantidadeNecessaria = Math.multiplyExact(
                        componente.getQuantidade(), quantidadeCombo);

                quantidadesEstoque.merge(
                        idProduto, quantidadeNecessaria, Math::addExact);
            }

            combosCarregados.put(idCombo, combo);
            composicoes.put(idCombo, composicao);

            subtotal = subtotal.add(
                    combo.getPrecoPromocional().multiply(
                            BigDecimal.valueOf(quantidadeCombo)));
        }

        List<ItemPedido> itensPedido = new ArrayList<>();

        for (var entrada : quantidadesProdutos.entrySet()) {

            Produto produto = produtoRepository
                    .findById(entrada.getKey())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Produto nÃ£o encontrado."));

            if (produto.getPreco() == null
                    || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "PreÃ§o do produto invÃ¡lido.");
            }

            BigDecimal valor = produto.getPreco().multiply(
                    BigDecimal.valueOf(entrada.getValue()));

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(entrada.getValue());
            item.setPrecoUnitario(produto.getPreco());
            item.setSubtotal(valor);

            itensPedido.add(item);
            subtotal = subtotal.add(valor);
        }

        // Bloqueia cada registro de estoque durante a transaÃ§Ã£o.
        // TreeMap mantÃ©m a ordem dos produtos, reduzindo deadlocks.
        for (var entrada : quantidadesEstoque.entrySet()) {

            Produto produto = produtoRepository
                    .findById(entrada.getKey())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Produto nÃ£o encontrado."));

            if (!Boolean.TRUE.equals(produto.getDisponivel())) {
                throw new IllegalArgumentException(
                        "Produto indisponÃ­vel: " + produto.getNome());
            }

            Estoque estoque = estoqueRepository
                    .buscarComBloqueio(
                            entrada.getKey(),
                            unidade.getIdUnidade())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Produto sem estoque nesta unidade."));

            if (estoque.getQuantidade() == null
                    || estoque.getQuantidade() < entrada.getValue()) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente: " + produto.getNome());
            }

            estoque.setQuantidade(
                    estoque.getQuantidade() - entrada.getValue());

            estoqueRepository.save(estoque);
        }

        BigDecimal desconto = BigDecimal.ZERO;

        if (dados.usarPontos()) {
            if (subtotal.compareTo(new BigDecimal("5.00")) < 0) {
                throw new IllegalArgumentException(
                        "Valor insuficiente para o desconto.");
            }

            fidelidadeService.validarResgate(cliente.getIdCliente());
            desconto = new BigDecimal("5.00");
        }

        BigDecimal taxaEntrega = BigDecimal.ZERO;

        if (dados.entrega()
                && subtotal.compareTo(new BigDecimal("80.00")) < 0) {
            taxaEntrega = new BigDecimal("8.00");
        }

        pedido.setDesconto(desconto);
        pedido.setTaxaEntrega(taxaEntrega);
        pedido.setValorTotal(
                subtotal.subtract(desconto).add(taxaEntrega));

        Pedido salvo = pedidoRepository.save(pedido);
        auditoriaService.registrar("PEDIDO_CRIADO", "PEDIDO", salvo.getIdPedido(), null, "Pedido criado");
        itemPedidoRepository.saveAll(itensPedido);

        // Guarda os combos vendidos e sua composiÃ§Ã£o original
        for (var entrada : quantidadesCombos.entrySet()) {

            Combo combo = combosCarregados.get(entrada.getKey());

            ComboVendido vendido = new ComboVendido();
            vendido.setPedido(salvo);
            vendido.setCombo(combo);
            vendido.setQuantidade(entrada.getValue());
            vendido.setPrecoUnitario(combo.getPrecoPromocional());
            vendido.setSubtotal(
                    combo.getPrecoPromocional().multiply(
                            BigDecimal.valueOf(entrada.getValue())));

            ComboVendido vendidoSalvo =
                    comboVendidoRepository.save(vendido);

            for (ItemCombo componente :
                    composicoes.get(entrada.getKey())) {

                ProdutoComboVendido produtoVendido =
                        new ProdutoComboVendido();

                produtoVendido.setComboVendido(vendidoSalvo);
                produtoVendido.setProduto(componente.getProduto());
                produtoVendido.setQuantidade(Math.multiplyExact(
                        componente.getQuantidade(),
                        entrada.getValue()));

                produtoComboVendidoRepository.save(produtoVendido);
            }
        }

        return salvo;
    }

    @Transactional
    public Pedido confirmarPagamento(
            Long idPedido,
            ResultadoPagamento resultadoSimulado) {

        Pedido pedido = buscarPorId(idPedido);

        if (!"AGUARDANDO_PAGAMENTO".equals(pedido.getStatus())
                && !"PAGAMENTO_RECUSADO".equals(pedido.getStatus())) {
            throw new IllegalArgumentException(
                    "Este pedido nÃ£o permite pagamento.");
        }

        ResultadoPagamento resultado =
                pagamentoGateway.processarPagamento(resultadoSimulado);

        if (resultado == ResultadoPagamento.RECUSADO) {
            pedido.setStatus("PAGAMENTO_RECUSADO");
            Pedido salvo = pedidoRepository.save(pedido);
            auditoriaService.registrar("PAGAMENTO_RECUSADO", "PEDIDO", salvo.getIdPedido(), null, "Pagamento recusado pelo gateway simulado");
            return salvo;
        }

        if (Boolean.TRUE.equals(pedido.getUsarPontos())) {
            fidelidadeService.resgatarPontos(
                    pedido.getCliente().getIdCliente());
        }

        BigDecimal valorProdutos = pedido.getValorTotal()
                .subtract(pedido.getTaxaEntrega());

        fidelidadeService.adicionarPontos(
                pedido.getCliente().getIdCliente(),
                valorProdutos);

        pedido.setStatus("PAGO");
        Pedido salvo = pedidoRepository.save(pedido);
        auditoriaService.registrar("PAGAMENTO_APROVADO", "PEDIDO", salvo.getIdPedido(), null, "Pagamento aprovado pelo gateway simulado");
        return salvo;
    }

    @Transactional
    public Pedido cancelarPedido(Long idPedido) {

        Pedido pedido = buscarPorId(idPedido);

        if (!"AGUARDANDO_PAGAMENTO".equals(pedido.getStatus())
                && !"PAGAMENTO_RECUSADO".equals(pedido.getStatus())) {
            throw new IllegalArgumentException(
                    "Somente pedidos nÃ£o pagos podem ser cancelados.");
        }

        if (pedido.getUnidade() == null) {
            throw new IllegalArgumentException(
                    "Pedido antigo sem unidade. VerificaÃ§Ã£o manual necessÃ¡ria.");
        }

        Map<Long, Integer> devolucoes = new TreeMap<>();

        for (ItemPedido item :
                itemPedidoRepository.findByPedido_IdPedido(idPedido)) {

            devolucoes.merge(
                    item.getProduto().getIdProduto(),
                    item.getQuantidade(),
                    Math::addExact);
        }

        for (ComboVendido combo :
                comboVendidoRepository.findByPedido_IdPedido(idPedido)) {

            for (ProdutoComboVendido produto :
                    produtoComboVendidoRepository
                            .findByComboVendido_IdComboVendido(
                                    combo.getIdComboVendido())) {

                devolucoes.merge(
                        produto.getProduto().getIdProduto(),
                        produto.getQuantidade(),
                        Math::addExact);
            }
        }

        // Devolve os produtos com bloqueio de estoque
        for (var entrada : devolucoes.entrySet()) {

            Estoque estoque = estoqueRepository
                    .buscarComBloqueio(
                            entrada.getKey(),
                            pedido.getUnidade().getIdUnidade())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Estoque nÃ£o encontrado para devoluÃ§Ã£o."));

            estoque.setQuantidade(
                    Math.addExact(
                            estoque.getQuantidade(),
                            entrada.getValue()));

            estoqueRepository.save(estoque);
        }

        pedido.setStatus("CANCELADO");
        Pedido salvo = pedidoRepository.save(pedido);
        auditoriaService.registrar("PEDIDO_CANCELADO", "PEDIDO", salvo.getIdPedido(), null, "Pedido cancelado e estoque devolvido");
        return salvo;
    }
}


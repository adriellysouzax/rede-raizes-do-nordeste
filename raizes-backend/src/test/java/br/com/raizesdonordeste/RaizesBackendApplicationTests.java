
package br.com.raizesdonordeste;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.raizesdonordeste.model.Pedido;
import br.com.raizesdonordeste.model.Produto;
import br.com.raizesdonordeste.model.Pedido;

class RaizesBackendApplicationTests {

	// TESTES DE SEGURANÇA

	@Test
	void deveCriptografarSenha() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		String senhaCriptografada = encoder.encode("SenhaTeste123");

		assertNotNull(senhaCriptografada);
		assertFalse(senhaCriptografada.equals("SenhaTeste123"));
	}

	@Test
	void deveValidarSenhaCorreta() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		String senhaCriptografada = encoder.encode("SenhaTeste123");

		assertTrue(encoder.matches("SenhaTeste123", senhaCriptografada));
	}

	@Test
	void deveRejeitarSenhaIncorreta() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		String senhaCriptografada = encoder.encode("SenhaTeste123");

		assertFalse(encoder.matches("SenhaErrada", senhaCriptografada));
	}

	// TESTES DE PRODUTOS

	@Test
	void deveCadastrarDadosDoProduto() {
		Produto produto = new Produto();

		produto.setIdProduto(1L);
		produto.setNome("Baião de Dois");
		produto.setDescricao("Prato típico nordestino");
		produto.setPreco(new BigDecimal("25.00"));
		produto.setDisponivel(true);

		assertEquals(1L, produto.getIdProduto());
		assertEquals("Baião de Dois", produto.getNome());
		assertEquals("Prato típico nordestino", produto.getDescricao());
		assertEquals(0, new BigDecimal("25.00").compareTo(produto.getPreco()));
		assertTrue(produto.getDisponivel());
	}

	@Test
	void devePermitirProdutoDisponivel() {
		Produto produto = new Produto();

		produto.setDisponivel(true);

		assertTrue(produto.getDisponivel());
	}

	@Test
	void devePermitirProdutoIndisponivel() {
		Produto produto = new Produto();

		produto.setDisponivel(false);

		assertFalse(produto.getDisponivel());
	}

	@Test
	void deveAtualizarPrecoDoProduto() {
		Produto produto = new Produto();

		produto.setPreco(new BigDecimal("20.00"));
		produto.setPreco(new BigDecimal("25.00"));

		assertEquals(0, new BigDecimal("25.00").compareTo(produto.getPreco()));
	}

    // TESTES DE PEDIDOS

    @Test
    void deveRegistrarValorTotalDoPedido() {
        Pedido pedido = new Pedido();

        pedido.setValorTotal(new BigDecimal("75.00"));

        assertEquals(
            0,
            new BigDecimal("75.00").compareTo(pedido.getValorTotal())
        );
    }

    @Test
    void deveAtualizarStatusDoPedido() {
        Pedido pedido = new Pedido();

        pedido.setStatus("RECEBIDO");
        pedido.setStatus("EM_PREPARO");

        assertEquals("EM_PREPARO", pedido.getStatus());
    }

    @Test
    void deveIniciarPedidoSemDescontoEPontos() {
        Pedido pedido = new Pedido();

        assertFalse(pedido.getUsarPontos());
        assertEquals(
            0,
            BigDecimal.ZERO.compareTo(pedido.getDesconto())
        );
    }

}

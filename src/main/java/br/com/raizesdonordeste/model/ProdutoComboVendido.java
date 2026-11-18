
package br.com.raizesdonordeste.model;

import jakarta.persistence.*;

@Entity
@Table(name = "produto_combo_vendido")
public class ProdutoComboVendido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_combo_vendido", nullable = false)
    private ComboVendido comboVendido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ComboVendido getComboVendido() {
        return comboVendido;
    }

    public void setComboVendido(ComboVendido comboVendido) {
        this.comboVendido = comboVendido;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}


package br.com.raizesdonordeste.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "item_combo",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"id_combo", "id_produto"}
    )
)
public class ItemCombo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItemCombo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_combo", nullable = false)
    private Combo combo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    public Long getIdItemCombo() {
        return idItemCombo;
    }

    public void setIdItemCombo(Long idItemCombo) {
        this.idItemCombo = idItemCombo;
    }

    public Combo getCombo() {
        return combo;
    }

    public void setCombo(Combo combo) {
        this.combo = combo;
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

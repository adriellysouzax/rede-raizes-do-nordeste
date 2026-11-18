
package br.com.raizesdonordeste.model;

import jakarta.persistence.*;

@Entity
@Table(name = "fidelidade")
public class Fidelidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFidelidade;

    @OneToOne
    @JoinColumn(name = "id_cliente", nullable = false, unique = true)
    private Cliente cliente;

    @Column(nullable = false)
    private Integer pontos = 0;

    public Fidelidade() {
    }

    public Long getIdFidelidade() {
        return idFidelidade;
    }

    public void setIdFidelidade(Long idFidelidade) {
        this.idFidelidade = idFidelidade;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Integer getPontos() {
        return pontos;
    }

    public void setPontos(Integer pontos) {
        this.pontos = pontos;
    }
}

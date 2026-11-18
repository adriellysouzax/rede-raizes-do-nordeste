package br.com.raizesdonordeste.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_evento")
public class AuditoriaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String evento;

    @Column(nullable = false, length = 60)
    private String entidade;

    private Long idRegistro;

    @Column(nullable = false, length = 100)
    private String responsavel;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(length = 500)
    private String detalhes;

    @PrePersist
    public void antesDeSalvar() {
        if (dataHora == null) dataHora = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getEvento() { return evento; }
    public void setEvento(String evento) { this.evento = evento; }
    public String getEntidade() { return entidade; }
    public void setEntidade(String entidade) { this.entidade = entidade; }
    public Long getIdRegistro() { return idRegistro; }
    public void setIdRegistro(Long idRegistro) { this.idRegistro = idRegistro; }
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getDetalhes() { return detalhes; }
    public void setDetalhes(String detalhes) { this.detalhes = detalhes; }
}

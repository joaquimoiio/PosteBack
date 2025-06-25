package com.vendas.postes.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "estoque")
@Data
@NoArgsConstructor
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poste_id", nullable = false)
    private Poste poste;

    @Column(name = "quantidade_atual", nullable = false)
    private Integer quantidadeAtual = 0;

    @Column(name = "quantidade_minima")
    private Integer quantidadeMinima = 0;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao = LocalDateTime.now();

    @Column(name = "tenant_id", nullable = false, length = 20)
    private String tenantId = "vermelho";

    public Estoque(Poste poste, Integer quantidadeAtual) {
        this.poste = poste;
        this.quantidadeAtual = quantidadeAtual;
        this.dataAtualizacao = LocalDateTime.now();
        if (poste != null && poste.getTenantId() != null) {
            this.tenantId = poste.getTenantId();
        }
    }

    public void adicionarQuantidade(Integer quantidade) {
        this.quantidadeAtual += quantidade;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void removerQuantidade(Integer quantidade) {
        this.quantidadeAtual -= quantidade;
        this.dataAtualizacao = LocalDateTime.now();
    }
}
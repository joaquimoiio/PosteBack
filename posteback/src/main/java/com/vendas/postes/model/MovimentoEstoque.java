// Nova entidade para registrar movimentos de estoque

package com.vendas.postes.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimento_estoque")
@Data
@NoArgsConstructor
public class MovimentoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poste_id", nullable = false)
    private Poste poste;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimento", nullable = false)
    private TipoMovimento tipoMovimento;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "data_movimento", nullable = false)
    private LocalDate dataMovimento;

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro = LocalDateTime.now();

    @Column(name = "observacao", length = 500)
    private String observacao;

    @Column(name = "quantidade_anterior")
    private Integer quantidadeAnterior;

    @Column(name = "quantidade_atual")
    private Integer quantidadeAtual;

    @Column(name = "tenant_id", length = 20)
    private String tenantId = "vermelho";

    @PrePersist
    @PreUpdate
    public void ensureTenantId() {
        if (this.tenantId == null || this.tenantId.trim().isEmpty()) {
            this.tenantId = "vermelho";
        }
    }

    public enum TipoMovimento {
        ENTRADA("Entrada"),
        SAIDA("Saída"),
        VENDA("Venda"),
        AJUSTE("Ajuste"),
        TRANSFERENCIA("Transferência");

        private final String descricao;

        TipoMovimento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public MovimentoEstoque(Poste poste, TipoMovimento tipo, Integer quantidade, LocalDate dataMovimento,
                            Integer quantidadeAnterior, Integer quantidadeAtual, String observacao) {
        this.poste = poste;
        this.tipoMovimento = tipo;
        this.quantidade = quantidade;
        this.dataMovimento = dataMovimento;
        this.quantidadeAnterior = quantidadeAnterior;
        this.quantidadeAtual = quantidadeAtual;
        this.observacao = observacao;
        this.dataRegistro = LocalDateTime.now();

        if (poste != null && poste.getTenantId() != null) {
            this.tenantId = poste.getTenantId();
        }
    }
}
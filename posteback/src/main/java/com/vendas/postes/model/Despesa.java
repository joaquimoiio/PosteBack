package com.vendas.postes.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "despesas")
@Data
@NoArgsConstructor
public class Despesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDespesa tipo;

    @Column(name = "data_despesa", nullable = false)
    private LocalDate dataDespesa = LocalDate.now();

    // Removido nullable = false para permitir migração
    @Column(name = "tenant_id", length = 20)
    private String tenantId = "vermelho";

    @PrePersist
    @PreUpdate
    public void ensureTenantId() {
        if (this.tenantId == null || this.tenantId.trim().isEmpty()) {
            this.tenantId = "vermelho";
        }
    }

    public enum TipoDespesa {
        FUNCIONARIO, OUTRAS
    }
}
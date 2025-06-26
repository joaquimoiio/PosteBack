package com.vendas.postes.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "postes")
@Data
@NoArgsConstructor
public class Poste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private Boolean ativo = true;

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
}
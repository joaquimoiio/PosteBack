package com.vendas.postes.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "postes")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    /**
     * Campo para identificar a qual caminhão o poste pertence
     * Valores possíveis: "vermelho", "branco"
     */
    @Column(name = "tenant_id", nullable = false, length = 20)
    private String tenantId = "vermelho"; // Default para compatibilidade

    /**
     * Verifica se o poste pertence ao caminhão vermelho
     */
    public boolean isVermelho() {
        return "vermelho".equalsIgnoreCase(tenantId);
    }

    /**
     * Verifica se o poste pertence ao caminhão branco
     */
    public boolean isBranco() {
        return "branco".equalsIgnoreCase(tenantId);
    }

    /**
     * Define o tenant como caminhão vermelho
     */
    public void setAsVermelho() {
        this.tenantId = "vermelho";
    }

    /**
     * Define o tenant como caminhão branco
     */
    public void setAsBranco() {
        this.tenantId = "branco";
    }
}
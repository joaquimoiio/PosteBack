package com.vendas.postes.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "despesas")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    /**
     * Campo para identificar a qual caminhão a despesa pertence
     * Valores possíveis: "vermelho", "branco"
     */
    @Column(name = "tenant_id", nullable = false, length = 20)
    private String tenantId = "vermelho"; // Default para compatibilidade

    public enum TipoDespesa {
        FUNCIONARIO, OUTRAS
    }

    /**
     * Verifica se a despesa pertence ao caminhão vermelho
     */
    public boolean isVermelho() {
        return "vermelho".equalsIgnoreCase(tenantId);
    }

    /**
     * Verifica se a despesa pertence ao caminhão branco
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
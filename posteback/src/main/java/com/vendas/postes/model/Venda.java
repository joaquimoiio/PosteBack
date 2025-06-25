package com.vendas.postes.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_venda", nullable = false)
    private LocalDateTime dataVenda = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_venda", nullable = false)
    private TipoVenda tipoVenda;

    // Para tipo V e L
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poste_id")
    private Poste poste;

    // Para tipo V e L
    @Column(name = "quantidade")
    private Integer quantidade;

    // Para tipo L
    @Column(name = "frete_eletrons", precision = 10, scale = 2)
    private BigDecimal freteEletrons;

    // Para tipo V
    @Column(name = "valor_venda", precision = 10, scale = 2)
    private BigDecimal valorVenda;

    // Para tipo E
    @Column(name = "valor_extra", precision = 10, scale = 2)
    private BigDecimal valorExtra;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;

    /**
     * Campo para identificar a qual caminhão a venda pertence
     * Valores possíveis: "vermelho", "branco"
     */
    @Column(name = "tenant_id", nullable = false, length = 20)
    private String tenantId = "vermelho"; // Default para compatibilidade

    public enum TipoVenda {
        E("Extra"),
        V("Venda Normal"),
        L("Venda Loja");

        private final String descricao;

        TipoVenda(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    /**
     * Verifica se a venda pertence ao caminhão vermelho
     */
    public boolean isVermelho() {
        return "vermelho".equalsIgnoreCase(tenantId);
    }

    /**
     * Verifica se a venda pertence ao caminhão branco
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
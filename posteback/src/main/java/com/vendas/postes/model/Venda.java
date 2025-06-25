package com.vendas.postes.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendas")
@Data
@NoArgsConstructor
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_venda", nullable = false)
    private LocalDateTime dataVenda = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_venda", nullable = false)
    private TipoVenda tipoVenda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poste_id")
    private Poste poste;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "frete_eletrons", precision = 10, scale = 2)
    private BigDecimal freteEletrons;

    @Column(name = "valor_venda", precision = 10, scale = 2)
    private BigDecimal valorVenda;

    @Column(name = "valor_extra", precision = 10, scale = 2)
    private BigDecimal valorExtra;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;

    @Column(name = "tenant_id", nullable = false, length = 20)
    private String tenantId = "vermelho";

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
}
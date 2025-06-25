package com.vendas.postes.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "estoque")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    /**
     * Campo para identificar a qual caminhão o estoque pertence
     * Herda automaticamente do poste associado
     */
    @Column(name = "tenant_id", nullable = false, length = 20)
    private String tenantId = "vermelho"; // Default para compatibilidade

    // Construtor para facilitar criação
    public Estoque(Poste poste, Integer quantidadeAtual) {
        this.poste = poste;
        this.quantidadeAtual = quantidadeAtual;
        this.dataAtualizacao = LocalDateTime.now();
        // Herdar tenant do poste
        if (poste != null && poste.getTenantId() != null) {
            this.tenantId = poste.getTenantId();
        }
    }

    public void adicionarQuantidade(Integer quantidade) {
        if (quantidade > 0) {
            this.quantidadeAtual += quantidade;
            this.dataAtualizacao = LocalDateTime.now();
        }
    }

    public boolean removerQuantidade(Integer quantidade) {
        if (quantidade > 0 && this.quantidadeAtual >= quantidade) {
            this.quantidadeAtual -= quantidade;
            this.dataAtualizacao = LocalDateTime.now();
            return true;
        }
        return false;
    }

    public boolean temEstoqueSuficiente(Integer quantidadeNecessaria) {
        return this.quantidadeAtual >= quantidadeNecessaria;
    }

    public boolean estoqueAbaixoDoMinimo() {
        return this.quantidadeAtual <= this.quantidadeMinima;
    }

    /**
     * Verifica se o estoque pertence ao caminhão vermelho
     */
    public boolean isVermelho() {
        return "vermelho".equalsIgnoreCase(tenantId);
    }

    /**
     * Verifica se o estoque pertence ao caminhão branco
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

    /**
     * Sincroniza o tenant com o poste associado
     */
    public void syncTenantWithPoste() {
        if (this.poste != null && this.poste.getTenantId() != null) {
            this.tenantId = this.poste.getTenantId();
        }
    }
}
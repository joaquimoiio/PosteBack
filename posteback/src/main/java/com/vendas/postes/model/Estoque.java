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

    // Construtor para facilitar criação
    public Estoque(Poste poste, Integer quantidadeAtual) {
        this.poste = poste;
        this.quantidadeAtual = quantidadeAtual;
        this.dataAtualizacao = LocalDateTime.now();
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
}
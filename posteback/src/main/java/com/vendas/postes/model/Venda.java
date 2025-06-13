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

    @Column(name = "total_frete_eletrons", precision = 10, scale = 2)
    private BigDecimal totalFreteEletrons = BigDecimal.ZERO;

    @Column(name = "total_comissao", precision = 10, scale = 2)
    private BigDecimal totalComissao = BigDecimal.ZERO;

    @Column(name = "valor_total_informado", precision = 10, scale = 2)
    private BigDecimal valorTotalInformado = BigDecimal.ZERO;

    @Column(name = "data_venda", nullable = false)
    private LocalDateTime dataVenda = LocalDateTime.now();

    @Column(name = "observacoes", length = 1000)
    private String observacoes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalFreteEletrons() {
        return totalFreteEletrons;
    }

    public void setTotalFreteEletrons(BigDecimal totalFreteEletrons) {
        this.totalFreteEletrons = totalFreteEletrons;
    }

    public BigDecimal getTotalComissao() {
        return totalComissao;
    }

    public void setTotalComissao(BigDecimal totalComissao) {
        this.totalComissao = totalComissao;
    }

    public BigDecimal getValorTotalInformado() {
        return valorTotalInformado;
    }

    public void setValorTotalInformado(BigDecimal valorTotalInformado) {
        this.valorTotalInformado = valorTotalInformado;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
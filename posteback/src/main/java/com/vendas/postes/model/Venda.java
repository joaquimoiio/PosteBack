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

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "indic", nullable = false)
    private TipoIndicacao indic;

    @Column(name = "vendedor", nullable = false, length = 200)
    private String vendedor;

    @Column(name = "cliente", nullable = false, length = 200)
    private String cliente;

    @Column(name = "cidade", nullable = false, length = 100)
    private String cidade;

    @Column(name = "valor_extra", precision = 10, scale = 2)
    private BigDecimal valorExtra = BigDecimal.ZERO;

    @Column(name = "frete_eletrons", precision = 10, scale = 2)
    private BigDecimal freteEletrons = BigDecimal.ZERO;

    @Column(name = "valor_loja", precision = 10, scale = 2, nullable = false)
    private BigDecimal valorLoja;

    @Column(name = "valor_venda", precision = 10, scale = 2, nullable = false)
    private BigDecimal valorVenda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poste_id", nullable = false)
    private Poste poste;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;

    public enum TipoIndicacao {
        V, E
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public TipoIndicacao getIndic() {
        return indic;
    }

    public void setIndic(TipoIndicacao indic) {
        this.indic = indic;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public BigDecimal getValorExtra() {
        return valorExtra;
    }

    public void setValorExtra(BigDecimal valorExtra) {
        this.valorExtra = valorExtra;
    }

    public BigDecimal getFreteEletrons() {
        return freteEletrons;
    }

    public void setFreteEletrons(BigDecimal freteEletrons) {
        this.freteEletrons = freteEletrons;
    }

    public BigDecimal getValorLoja() {
        return valorLoja;
    }

    public void setValorLoja(BigDecimal valorLoja) {
        this.valorLoja = valorLoja;
    }

    public BigDecimal getValorVenda() {
        return valorVenda;
    }

    public void setValorVenda(BigDecimal valorVenda) {
        this.valorVenda = valorVenda;
    }

    public Poste getPoste() {
        return poste;
    }

    public void setPoste(Poste poste) {
        this.poste = poste;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
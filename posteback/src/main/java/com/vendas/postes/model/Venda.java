package com.vendas.postes.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "total_frete_eletrons", precision = 10, scale = 2)
    private BigDecimal totalFreteEletrons = BigDecimal.ZERO;

    @Column(name = "total_comissao", precision = 10, scale = 2)
    private BigDecimal totalComissao = BigDecimal.ZERO;

    @Column(name = "valor_total_informado", precision = 10, scale = 2)
    private BigDecimal valorTotalInformado = BigDecimal.ZERO;

    @Column(name = "valor_extra", precision = 10, scale = 2)
    private BigDecimal valorExtra = BigDecimal.ZERO;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemVenda> itens;

    public enum TipoVenda {
        E("Extra"),
        V("Venda Normal"),
        L("Venda Livre");

        private final String descricao;

        TipoVenda(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    // Métodos para calcular totais baseados no tipo de venda
    public BigDecimal calcularTotalItens() {
        if (tipoVenda == TipoVenda.E) {
            return BigDecimal.ZERO; // Para tipo E, não considera postes
        }

        return itens != null ?
                itens.stream()
                        .map(ItemVenda::getSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;
    }

    public BigDecimal calcularValorTotal() {
        switch (tipoVenda) {
            case E:
                // Para tipo E: apenas valor extra
                return valorExtra != null ? valorExtra : BigDecimal.ZERO;
            case V:
                // Para tipo V: valor dos postes + frete + comissão
                return calcularTotalItens()
                        .add(totalFreteEletrons != null ? totalFreteEletrons : BigDecimal.ZERO)
                        .add(totalComissao != null ? totalComissao : BigDecimal.ZERO);
            case L:
                // Para tipo L: valor informado + frete
                return (valorTotalInformado != null ? valorTotalInformado : BigDecimal.ZERO)
                        .add(totalFreteEletrons != null ? totalFreteEletrons : BigDecimal.ZERO);
            default:
                return BigDecimal.ZERO;
        }
    }

    // Método para calcular contribuição para o lucro
    public BigDecimal calcularContribuicaoLucro() {
        switch (tipoVenda) {
            case E:
                // Para tipo E: valor extra contribui diretamente para o lucro
                return valorExtra != null ? valorExtra : BigDecimal.ZERO;
            case V:
                // Para tipo V: valor dos postes - valor de venda (se houver diferença positiva)
                BigDecimal valorPostes = calcularTotalItens();
                BigDecimal valorVenda = valorTotalInformado != null ? valorTotalInformado : BigDecimal.ZERO;
                return valorPostes.subtract(valorVenda);
            case L:
                // Para tipo L: valor de venda contribui diretamente para o lucro
                return valorTotalInformado != null ? valorTotalInformado : BigDecimal.ZERO;
            default:
                return BigDecimal.ZERO;
        }
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

    public TipoVenda getTipoVenda() {
        return tipoVenda;
    }

    public void setTipoVenda(TipoVenda tipoVenda) {
        this.tipoVenda = tipoVenda;
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

    public BigDecimal getValorExtra() {
        return valorExtra;
    }

    public void setValorExtra(BigDecimal valorExtra) {
        this.valorExtra = valorExtra;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
    }
}
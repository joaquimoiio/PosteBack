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

    @Column(name = "total_frete_eletrons", precision = 10, scale = 2)
    private BigDecimal totalFreteEletrons = BigDecimal.ZERO;

    @Column(name = "total_comissao", precision = 10, scale = 2)
    private BigDecimal totalComissao = BigDecimal.ZERO;

    @Column(name = "valor_total_informado", precision = 10, scale = 2)
    private BigDecimal valorTotalInformado = BigDecimal.ZERO;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemVenda> itens;

    // Métodos para calcular totais
    public BigDecimal calcularTotalItens() {
        return itens != null ?
                itens.stream()
                        .map(ItemVenda::getSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;
    }

    public BigDecimal calcularValorTotal() {
        return calcularTotalItens()
                .add(totalFreteEletrons != null ? totalFreteEletrons : BigDecimal.ZERO)
                .add(totalComissao != null ? totalComissao : BigDecimal.ZERO);
    }
}
package com.vendas.postes.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumoVendasDTO {
    private BigDecimal totalVendaPostes;
    private BigDecimal totalFreteEletrons;
    private BigDecimal totalComissao;
    private BigDecimal valorTotalVendas;
    private BigDecimal despesasFuncionario;
    private BigDecimal outrasDespesas;
    private BigDecimal totalDespesas;
    private BigDecimal lucro;
    private BigDecimal parteCicero;
    private BigDecimal parteGuilhermeJefferson;
    private BigDecimal parteGuilherme;
    private BigDecimal parteJefferson;
    private BigDecimal totalContribuicoesExtras; // Contribuições dos tipos E e L

    // Estatísticas por tipo de venda
    private Long totalVendasE;
    private Long totalVendasV;
    private Long totalVendasL;
    private BigDecimal valorTotalExtras; // Soma dos valores dos tipos E
    private BigDecimal valorTotalLivres; // Soma dos valores dos tipos L
}
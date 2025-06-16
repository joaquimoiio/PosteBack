package com.vendas.postes.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumoVendasDTO {
    // Valores básicos
    private BigDecimal totalVendaPostes;
    private BigDecimal valorTotalVendas;
    private BigDecimal totalFreteEletrons;
    private BigDecimal valorTotalExtras;

    // Estatísticas por tipo
    private Long totalVendasE;
    private Long totalVendasV;
    private Long totalVendasL;

    // Para cálculos no frontend
    private BigDecimal despesasFuncionario;
    private BigDecimal outrasDespesas;
    private BigDecimal totalContribuicoesExtras;
}
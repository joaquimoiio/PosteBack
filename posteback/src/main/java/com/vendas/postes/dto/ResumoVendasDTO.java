package com.vendas.postes.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumoVendasDTO {
    private BigDecimal totalVendaPostes;      // Custo dos postes vendidos (tipo V)
    private BigDecimal totalFreteEletrons;    // Frete do tipo L
    private BigDecimal totalComissao;         // Removido - não usado mais
    private BigDecimal valorTotalVendas;      // Valor arrecadado tipo V
    private BigDecimal despesasFuncionario;   // Calculado no frontend
    private BigDecimal outrasDespesas;        // Calculado no frontend
    private BigDecimal totalDespesas;         // Calculado no frontend
    private BigDecimal lucro;                 // Calculado no frontend
    private BigDecimal parteCicero;           // Calculado no frontend
    private BigDecimal parteGuilhermeJefferson; // Calculado no frontend
    private BigDecimal parteGuilherme;        // Calculado no frontend
    private BigDecimal parteJefferson;        // Calculado no frontend
    private BigDecimal totalContribuicoesExtras; // Calculado no frontend

    // Estatísticas por tipo de venda (apenas E, V, L agora)
    private Long totalVendasE;
    private Long totalVendasV;
    private Long totalVendasL;
    private BigDecimal valorTotalExtras;      // Soma dos valores tipo E
    private BigDecimal valorTotalLivres;      // Frete do tipo L (mesmo que totalFreteEletrons)
}
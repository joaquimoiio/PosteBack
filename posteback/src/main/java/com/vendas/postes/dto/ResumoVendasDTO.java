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
    private BigDecimal totalValorExtra; // Campo mantido para compatibilidade
}
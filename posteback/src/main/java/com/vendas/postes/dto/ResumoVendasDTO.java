package com.vendas.postes.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ResumoVendasDTO {
    private BigDecimal totalVendaPostes;
    private BigDecimal valorTotalVendas;
    private BigDecimal totalFreteEletrons;
    private BigDecimal valorTotalExtras;
    private Long totalVendasE;
    private Long totalVendasV;
    private Long totalVendasL;
}
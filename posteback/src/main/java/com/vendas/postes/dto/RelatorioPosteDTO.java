package com.vendas.postes.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioPosteDTO {
    private Long posteId;
    private String codigoPoste;
    private String descricaoPoste;
    private BigDecimal precoUnitario;
    private Long quantidadeVendida;
    private BigDecimal valorTotal;
    private Long numeroVendas;
    private Double percentualDoTotal;
    private Integer ranking;
}
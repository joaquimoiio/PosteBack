package com.vendas.postes.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemVendaDTO {
    private Long id;
    private Long vendaId;
    private Long posteId;
    private String codigoPoste;
    private String descricaoPoste;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;
}
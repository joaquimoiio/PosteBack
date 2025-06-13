package com.vendas.postes.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendaDTO {
    private Long id;
    private BigDecimal totalFreteEletrons;
    private BigDecimal totalComissao;
    private BigDecimal valorTotalInformado;
    private LocalDateTime dataVenda;
    private String observacoes;
    private List<ItemVendaDTO> itens;
}
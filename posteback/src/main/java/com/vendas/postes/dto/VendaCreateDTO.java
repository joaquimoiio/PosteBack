package com.vendas.postes.dto;

import com.vendas.postes.model.Venda;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendaCreateDTO {
    private LocalDateTime dataVenda;
    private Venda.TipoVenda tipoVenda;
    private Long posteId;
    private Integer quantidade;
    private BigDecimal freteEletrons;
    private BigDecimal valorVenda;
    private BigDecimal valorExtra;
    private String observacoes;
}
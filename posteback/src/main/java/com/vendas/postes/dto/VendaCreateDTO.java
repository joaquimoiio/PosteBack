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

    // Campos para tipo V e L
    private Integer quantidade;
    private Long posteId;

    // Campo para frete (apenas tipo L)
    private BigDecimal freteEletrons;

    // Campo para valor de venda (apenas tipo V)
    private BigDecimal valorVenda;

    // Campo específico para tipo E
    private BigDecimal valorExtra;

    private String observacoes;
}
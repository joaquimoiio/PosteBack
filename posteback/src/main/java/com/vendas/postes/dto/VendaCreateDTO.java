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
    private BigDecimal freteEletrons;

    // Campo para tipo E e L
    private BigDecimal valorVenda;

    // Campo específico para tipo E
    private BigDecimal valorExtra;

    private String observacoes;
}
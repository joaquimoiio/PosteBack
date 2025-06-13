package com.vendas.postes.dto;

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
    private Integer quantidade;
    private String indic;
    private String vendedor;
    private String cliente;
    private String cidade;
    private BigDecimal valorExtra;
    private BigDecimal freteEletrons;
    private BigDecimal valorVenda;
    private Long posteId;
    private String observacoes;
}
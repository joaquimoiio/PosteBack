package com.vendas.postes.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EstoqueDTO {
    private Long id;
    private Long posteId;
    private String codigoPoste;
    private String descricaoPoste;
    private BigDecimal precoPoste;
    private Boolean posteAtivo;
    private Integer quantidadeAtual;
    private Integer quantidadeMinima;
    private LocalDateTime dataAtualizacao;
    private Boolean estoqueAbaixoMinimo;
}
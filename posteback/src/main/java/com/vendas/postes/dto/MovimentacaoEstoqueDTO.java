package com.vendas.postes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
class MovimentacaoEstoqueDTO {
    private Long id;
    private Long posteId;
    private String codigoPoste;
    private String tipoMovimentacao; // ENTRADA, SAIDA_VENDA
    private Integer quantidade;
    private Integer quantidadeAnterior;
    private Integer quantidadeAtual;
    private String observacao;
    private LocalDateTime dataMovimentacao;
    private Long vendaId; // Para movimentações de saída por venda
}
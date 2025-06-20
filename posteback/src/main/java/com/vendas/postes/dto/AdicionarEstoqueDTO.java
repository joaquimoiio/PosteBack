package com.vendas.postes.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdicionarEstoqueDTO {
    private Long posteId;
    private Integer quantidade;
    private String observacao;
}
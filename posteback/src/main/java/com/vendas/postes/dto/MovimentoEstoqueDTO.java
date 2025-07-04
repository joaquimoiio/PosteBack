package com.vendas.postes.dto;

import com.vendas.postes.model.MovimentoEstoque;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MovimentoEstoqueDTO {
    private Long id;
    private Long posteId;
    private String codigoPoste;
    private String descricaoPoste;
    private BigDecimal precoPoste;
    private MovimentoEstoque.TipoMovimento tipoMovimento;
    private String tipoMovimentoDescricao;
    private Integer quantidade;
    private LocalDate dataMovimento;
    private LocalDateTime dataRegistro;
    private String observacao;
    private Integer quantidadeAnterior;
    private Integer quantidadeAtual;
    private String tenantId;
    private BigDecimal valorMovimento;

    // Campos calculados
    private Integer variacaoQuantidade;
    private Boolean isEntrada;
    private Boolean isSaida;
    private String statusMovimento;

    // Campos para relatórios
    private String periodoFormatado;
    private String resumoMovimento;

    public MovimentoEstoqueDTO() {}

    public MovimentoEstoqueDTO(MovimentoEstoque movimento) {
        this.id = movimento.getId();
        this.posteId = movimento.getPoste().getId();
        this.codigoPoste = movimento.getPoste().getCodigo();
        this.descricaoPoste = movimento.getPoste().getDescricao();
        this.precoPoste = movimento.getPoste().getPreco();
        this.tipoMovimento = movimento.getTipoMovimento();
        this.tipoMovimentoDescricao = movimento.getTipoMovimento().getDescricao();
        this.quantidade = movimento.getQuantidade();
        this.dataMovimento = movimento.getDataMovimento();
        this.dataRegistro = movimento.getDataRegistro();
        this.observacao = movimento.getObservacao();
        this.quantidadeAnterior = movimento.getQuantidadeAnterior();
        this.quantidadeAtual = movimento.getQuantidadeAtual();
        this.tenantId = movimento.getTenantId();

        // Campos calculados
        this.calcularCamposDerivedos();
    }

    private void calcularCamposDerivedos() {
        // Calcular valor do movimento
        if (this.precoPoste != null && this.quantidade != null) {
            this.valorMovimento = this.precoPoste.multiply(BigDecimal.valueOf(this.quantidade));
        }

        // Calcular variação
        if (this.quantidadeAnterior != null && this.quantidadeAtual != null) {
            this.variacaoQuantidade = this.quantidadeAtual - this.quantidadeAnterior;
        }

        // Determinar tipo de movimento
        if (this.tipoMovimento != null) {
            this.isEntrada = this.tipoMovimento == MovimentoEstoque.TipoMovimento.ENTRADA ||
                    this.tipoMovimento == MovimentoEstoque.TipoMovimento.AJUSTE;
            this.isSaida = this.tipoMovimento == MovimentoEstoque.TipoMovimento.SAIDA ||
                    this.tipoMovimento == MovimentoEstoque.TipoMovimento.VENDA;

            // Status do movimento
            this.statusMovimento = this.isEntrada ? "POSITIVO" : "NEGATIVO";
        }

        // Resumo do movimento
        if (this.codigoPoste != null && this.tipoMovimento != null) {
            this.resumoMovimento = String.format("%s: %s %d unidades",
                    this.codigoPoste,
                    this.tipoMovimentoDescricao,
                    this.quantidade);
        }

        // Período formatado (para relatórios)
        if (this.dataMovimento != null) {
            this.periodoFormatado = String.format("%02d/%04d",
                    this.dataMovimento.getMonthValue(),
                    this.dataMovimento.getYear());
        }
    }
}
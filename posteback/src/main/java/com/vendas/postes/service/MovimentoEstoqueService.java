package com.vendas.postes.service;

import com.vendas.postes.config.TenantContext;
import com.vendas.postes.dto.MovimentoEstoqueDTO;
import com.vendas.postes.model.MovimentoEstoque;
import com.vendas.postes.model.Poste;
import com.vendas.postes.repository.MovimentoEstoqueRepository;
import com.vendas.postes.repository.PosteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovimentoEstoqueService {

    private final MovimentoEstoqueRepository movimentoEstoqueRepository;
    private final PosteRepository posteRepository;

    /**
     * Lista últimos movimentos do tenant atual
     */
    public List<MovimentoEstoqueDTO> listarUltimosMovimentos() {
        String tenantId = TenantContext.getCurrentTenantValue();

        List<MovimentoEstoque> movimentos = movimentoEstoqueRepository
                .findByTenantIdOrderByDataRegistroDesc(tenantId)
                .stream()
                .limit(100) // Limitar a 100 registros
                .collect(Collectors.toList());

        return movimentos.stream()
                .map(MovimentoEstoqueDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Lista movimentos por período
     */
    public List<MovimentoEstoqueDTO> listarMovimentosPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        String tenantId = TenantContext.getCurrentTenantValue();

        LocalDate inicio = dataInicio != null ? dataInicio : LocalDate.now().minusMonths(1);
        LocalDate fim = dataFim != null ? dataFim : LocalDate.now();

        List<MovimentoEstoque> movimentos = movimentoEstoqueRepository
                .findByTenantIdAndDataMovimentoBetweenOrderByDataRegistroDesc(tenantId, inicio, fim);

        return movimentos.stream()
                .map(MovimentoEstoqueDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Lista movimentos de um poste específico
     */
    public List<MovimentoEstoqueDTO> listarMovimentosPorPoste(Long posteId) {
        String tenantId = TenantContext.getCurrentTenantValue();

        List<MovimentoEstoque> movimentos = movimentoEstoqueRepository
                .findByPosteIdAndTenantIdOrderByDataRegistroDesc(posteId, tenantId);

        return movimentos.stream()
                .map(MovimentoEstoqueDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Calcula estoque em uma data específica
     */
    public Integer calcularEstoqueNaData(Long posteId, LocalDate dataReferencia) {
        String tenantId = TenantContext.getCurrentTenantValue();

        List<MovimentoEstoque> movimentos = movimentoEstoqueRepository
                .findByPosteIdAndTenantIdOrderByDataRegistroDesc(posteId, tenantId);

        int quantidadeCalculada = 0;

        for (MovimentoEstoque movimento : movimentos) {
            if (movimento.getDataMovimento().isAfter(dataReferencia)) {
                continue; // Pular movimentos posteriores à data de referência
            }

            switch (movimento.getTipoMovimento()) {
                case ENTRADA:
                case AJUSTE:
                    quantidadeCalculada += movimento.getQuantidade();
                    break;
                case SAIDA:
                case VENDA:
                    quantidadeCalculada -= movimento.getQuantidade();
                    break;
                case TRANSFERENCIA:
                    // Para transferências, verificar se é entrada ou saída baseado no tenant
                    // (implementação pode variar conforme regra de negócio)
                    break;
            }
        }

        return quantidadeCalculada;
    }

    /**
     * Registra movimento manual de estoque
     */
    @Transactional
    public MovimentoEstoqueDTO registrarMovimentoManual(Long posteId, String tipoMovimentoStr,
                                                        Integer quantidade, LocalDate dataMovimento, String observacao) {

        Poste poste = posteRepository.findById(posteId)
                .orElseThrow(() -> new RuntimeException("Poste não encontrado"));

        MovimentoEstoque.TipoMovimento tipoMovimento;
        try {
            tipoMovimento = MovimentoEstoque.TipoMovimento.valueOf(tipoMovimentoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de movimento inválido: " + tipoMovimentoStr);
        }

        // Obter quantidade atual (simulada - em produção viria do EstoqueService)
        Integer quantidadeAnterior = 0; // TODO: buscar do EstoqueService
        Integer quantidadeAtual = quantidadeAnterior;

        // Calcular nova quantidade baseada no tipo de movimento
        switch (tipoMovimento) {
            case ENTRADA:
            case AJUSTE:
                quantidadeAtual += quantidade;
                break;
            case SAIDA:
            case VENDA:
                quantidadeAtual -= quantidade;
                break;
        }

        MovimentoEstoque movimento = new MovimentoEstoque(
                poste, tipoMovimento, quantidade, dataMovimento,
                quantidadeAnterior, quantidadeAtual, observacao
        );

        movimento = movimentoEstoqueRepository.save(movimento);

        log.info("✅ Movimento manual registrado: {} {} unidades do poste {}",
                tipoMovimento, quantidade, poste.getCodigo());

        return new MovimentoEstoqueDTO(movimento);
    }

    /**
     * Gera relatório de movimentos por período
     */
    public Map<String, Object> gerarRelatorioMovimentos(LocalDate dataInicio, LocalDate dataFim) {
        String tenantId = TenantContext.getCurrentTenantValue();

        List<MovimentoEstoque> movimentos = movimentoEstoqueRepository
                .findByTenantIdAndDataMovimentoBetweenOrderByDataRegistroDesc(tenantId, dataInicio, dataFim);

        Map<String, Object> relatorio = new HashMap<>();

        // Estatísticas gerais
        relatorio.put("totalMovimentos", movimentos.size());
        relatorio.put("periodoInicio", dataInicio);
        relatorio.put("periodoFim", dataFim);
        relatorio.put("tenantId", tenantId);

        // Agrupamento por tipo de movimento
        Map<MovimentoEstoque.TipoMovimento, Long> porTipo = movimentos.stream()
                .collect(Collectors.groupingBy(
                        MovimentoEstoque::getTipoMovimento,
                        Collectors.counting()
                ));
        relatorio.put("movimentosPorTipo", porTipo);

        // Agrupamento por data
        Map<LocalDate, Long> porData = movimentos.stream()
                .collect(Collectors.groupingBy(
                        MovimentoEstoque::getDataMovimento,
                        Collectors.counting()
                ));
        relatorio.put("movimentosPorData", porData);

        // Valor total dos movimentos
        BigDecimal valorTotal = movimentos.stream()
                .filter(m -> m.getPoste().getPreco() != null)
                .map(m -> m.getPoste().getPreco().multiply(BigDecimal.valueOf(m.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        relatorio.put("valorTotalMovimentos", valorTotal);

        // Top 5 postes com mais movimentos
        Map<String, Long> topPostes = movimentos.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getPoste().getCodigo(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        relatorio.put("topPostesMovimento", topPostes);

        return relatorio;
    }

    /**
     * Lista movimentos consolidados (para Jefferson)
     */
    public List<MovimentoEstoqueDTO> listarMovimentosConsolidados(LocalDate dataInicio, LocalDate dataFim, Integer limite) {
        List<MovimentoEstoque> movimentos;

        if (dataInicio != null || dataFim != null) {
            LocalDate inicio = dataInicio != null ? dataInicio : LocalDate.now().minusMonths(3);
            LocalDate fim = dataFim != null ? dataFim : LocalDate.now();

            movimentos = movimentoEstoqueRepository.findAllMovimentosConsolidados()
                    .stream()
                    .filter(m -> !m.getDataMovimento().isBefore(inicio) && !m.getDataMovimento().isAfter(fim))
                    .limit(limite != null ? limite : 200)
                    .collect(Collectors.toList());
        } else {
            movimentos = movimentoEstoqueRepository.findAllMovimentosConsolidados()
                    .stream()
                    .limit(limite != null ? limite : 200)
                    .collect(Collectors.toList());
        }

        return movimentos.stream()
                .map(MovimentoEstoqueDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Obtém estatísticas de movimentos
     */
    public Map<String, Object> obterEstatisticasMovimentos(LocalDate dataInicio, LocalDate dataFim) {
        String tenantId = TenantContext.getCurrentTenantValue();

        List<MovimentoEstoque> movimentos = movimentoEstoqueRepository
                .findByTenantIdAndDataMovimentoBetweenOrderByDataRegistroDesc(tenantId, dataInicio, dataFim);

        Map<String, Object> estatisticas = new HashMap<>();

        // Contadores básicos
        long totalEntradas = movimentos.stream()
                .filter(m -> m.getTipoMovimento() == MovimentoEstoque.TipoMovimento.ENTRADA)
                .count();

        long totalSaidas = movimentos.stream()
                .filter(m -> m.getTipoMovimento() == MovimentoEstoque.TipoMovimento.SAIDA)
                .count();

        long totalVendas = movimentos.stream()
                .filter(m -> m.getTipoMovimento() == MovimentoEstoque.TipoMovimento.VENDA)
                .count();

        long totalAjustes = movimentos.stream()
                .filter(m -> m.getTipoMovimento() == MovimentoEstoque.TipoMovimento.AJUSTE)
                .count();

        // Quantidades
        int quantidadeEntradas = movimentos.stream()
                .filter(m -> m.getTipoMovimento() == MovimentoEstoque.TipoMovimento.ENTRADA)
                .mapToInt(MovimentoEstoque::getQuantidade)
                .sum();

        int quantidadeSaidas = movimentos.stream()
                .filter(m -> m.getTipoMovimento() == MovimentoEstoque.TipoMovimento.SAIDA ||
                        m.getTipoMovimento() == MovimentoEstoque.TipoMovimento.VENDA)
                .mapToInt(MovimentoEstoque::getQuantidade)
                .sum();

        estatisticas.put("totalMovimentos", movimentos.size());
        estatisticas.put("totalEntradas", totalEntradas);
        estatisticas.put("totalSaidas", totalSaidas);
        estatisticas.put("totalVendas", totalVendas);
        estatisticas.put("totalAjustes", totalAjustes);
        estatisticas.put("quantidadeEntradas", quantidadeEntradas);
        estatisticas.put("quantidadeSaidas", quantidadeSaidas);
        estatisticas.put("saldoQuantidade", quantidadeEntradas - quantidadeSaidas);
        estatisticas.put("periodoInicio", dataInicio);
        estatisticas.put("periodoFim", dataFim);

        return estatisticas;
    }
}
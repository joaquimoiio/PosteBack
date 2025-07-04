package com.vendas.postes.controller;

import com.vendas.postes.config.TenantContext;
import com.vendas.postes.dto.MovimentoEstoqueDTO;
import com.vendas.postes.service.MovimentoEstoqueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movimento-estoque")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class MovimentoEstoqueController {

    private final MovimentoEstoqueService movimentoEstoqueService;

    /**
     * Lista todos os movimentos de estoque com filtros opcionais
     */
    @GetMapping
    public ResponseEntity<List<MovimentoEstoqueDTO>> listarMovimentos(
            @RequestParam(value = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(value = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(value = "posteId", required = false) Long posteId,
            @RequestParam(value = "tipoMovimento", required = false) String tipoMovimento) {

        try {
            String tenantId = TenantContext.getCurrentTenantValue();
            log.info("📋 Listando movimentos de estoque para tenant: {}", tenantId);

            List<MovimentoEstoqueDTO> movimentos;

            if (posteId != null) {
                // Movimentos de um poste específico
                movimentos = movimentoEstoqueService.listarMovimentosPorPoste(posteId);
            } else if (dataInicio != null || dataFim != null) {
                // Movimentos por período
                movimentos = movimentoEstoqueService.listarMovimentosPorPeriodo(dataInicio, dataFim);
            } else {
                // Todos os movimentos (últimos 100)
                movimentos = movimentoEstoqueService.listarUltimosMovimentos();
            }

            log.info("📊 Retornando {} movimentos de estoque", movimentos.size());
            return ResponseEntity.ok(movimentos);

        } catch (Exception e) {
            log.error("❌ Erro ao listar movimentos de estoque: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Busca histórico de movimentos de um poste específico
     */
    @GetMapping("/poste/{posteId}")
    public ResponseEntity<List<MovimentoEstoqueDTO>> obterHistoricoPoste(@PathVariable Long posteId) {
        try {
            log.info("📈 Buscando histórico do poste ID: {}", posteId);

            List<MovimentoEstoqueDTO> historico = movimentoEstoqueService.listarMovimentosPorPoste(posteId);

            return ResponseEntity.ok(historico);

        } catch (Exception e) {
            log.error("❌ Erro ao buscar histórico do poste {}: ", posteId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Calcula estoque em uma data específica
     */
    @GetMapping("/estoque-data")
    public ResponseEntity<Map<String, Object>> calcularEstoqueNaData(
            @RequestParam Long posteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataReferencia) {

        try {
            log.info("🔍 Calculando estoque do poste {} na data {}", posteId, dataReferencia);

            Integer quantidade = movimentoEstoqueService.calcularEstoqueNaData(posteId, dataReferencia);

            Map<String, Object> resultado = Map.of(
                    "posteId", posteId,
                    "dataReferencia", dataReferencia,
                    "quantidadeCalculada", quantidade
            );

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            log.error("❌ Erro ao calcular estoque na data: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Relatório de movimentos por período e tipo
     */
    @GetMapping("/relatorio")
    public ResponseEntity<Map<String, Object>> gerarRelatorioMovimentos(
            @RequestParam(value = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(value = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        try {
            String tenantId = TenantContext.getCurrentTenantValue();
            log.info("📊 Gerando relatório de movimentos para tenant: {}", tenantId);

            // Definir período padrão se não informado
            LocalDate inicio = dataInicio != null ? dataInicio : LocalDate.now().minusMonths(1);
            LocalDate fim = dataFim != null ? dataFim : LocalDate.now();

            Map<String, Object> relatorio = movimentoEstoqueService.gerarRelatorioMovimentos(inicio, fim);

            return ResponseEntity.ok(relatorio);

        } catch (Exception e) {
            log.error("❌ Erro ao gerar relatório de movimentos: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Registra um movimento manual de estoque
     */
    @PostMapping("/manual")
    public ResponseEntity<MovimentoEstoqueDTO> registrarMovimentoManual(
            @RequestBody Map<String, Object> request) {

        try {
            // Validar dados obrigatórios
            if (!request.containsKey("posteId") || !request.containsKey("tipoMovimento") ||
                    !request.containsKey("quantidade") || !request.containsKey("dataMovimento")) {

                log.warn("⚠️ Dados incompletos para movimento manual: {}", request);
                return ResponseEntity.badRequest().build();
            }

            Long posteId = Long.valueOf(request.get("posteId").toString());
            String tipoMovimento = request.get("tipoMovimento").toString();
            Integer quantidade = Integer.valueOf(request.get("quantidade").toString());
            LocalDate dataMovimento = LocalDate.parse(request.get("dataMovimento").toString());
            String observacao = request.containsKey("observacao") ?
                    request.get("observacao").toString() : "Movimento manual";

            // Validações
            if (quantidade <= 0) {
                log.warn("⚠️ Quantidade inválida: {}", quantidade);
                return ResponseEntity.badRequest().build();
            }

            log.info("📝 Registrando movimento manual: {} {} unidades do poste {} na data {}",
                    tipoMovimento, quantidade, posteId, dataMovimento);

            MovimentoEstoqueDTO movimento = movimentoEstoqueService.registrarMovimentoManual(
                    posteId, tipoMovimento, quantidade, dataMovimento, observacao);

            return ResponseEntity.ok(movimento);

        } catch (NumberFormatException e) {
            log.warn("⚠️ Erro de conversão numérica: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("❌ Erro de negócio ao registrar movimento: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("❌ Erro interno ao registrar movimento: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint específico para Jefferson - movimentos consolidados
     */
    @GetMapping("/consolidado")
    public ResponseEntity<List<MovimentoEstoqueDTO>> listarMovimentosConsolidados(
            @RequestParam(value = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(value = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(value = "limite", defaultValue = "200") Integer limite) {

        try {
            String tenantId = TenantContext.getCurrentTenantValue();

            // Verificar se é Jefferson
            if (!"jefferson".equals(tenantId)) {
                return ResponseEntity.status(403).build();
            }

            log.info("🔍 Listando movimentos consolidados para Jefferson");

            List<MovimentoEstoqueDTO> movimentos = movimentoEstoqueService.listarMovimentosConsolidados(
                    dataInicio, dataFim, limite);

            return ResponseEntity.ok(movimentos);

        } catch (Exception e) {
            log.error("❌ Erro ao listar movimentos consolidados: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Estatísticas rápidas de movimentos
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticasMovimentos(
            @RequestParam(value = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(value = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        try {
            String tenantId = TenantContext.getCurrentTenantValue();
            log.info("📈 Gerando estatísticas de movimentos para tenant: {}", tenantId);

            // Período padrão: último mês
            LocalDate inicio = dataInicio != null ? dataInicio : LocalDate.now().minusMonths(1);
            LocalDate fim = dataFim != null ? dataFim : LocalDate.now();

            Map<String, Object> estatisticas = movimentoEstoqueService.obterEstatisticasMovimentos(inicio, fim);

            return ResponseEntity.ok(estatisticas);

        } catch (Exception e) {
            log.error("❌ Erro ao gerar estatísticas: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
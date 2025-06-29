package com.vendas.postes.controller;

import com.vendas.postes.config.TenantContext;
import com.vendas.postes.dto.EstoqueDTO;
import com.vendas.postes.service.EstoqueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estoque")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class EstoqueController {

    private final EstoqueService estoqueService;

    /**
     * Lista todo o estoque - consolidado para Jefferson, específico por tenant para outros
     */
    @GetMapping
    public ResponseEntity<List<EstoqueDTO>> listarTodoEstoque() {
        try {
            String tenantAtual = TenantContext.getCurrentTenantValue();
            log.info("🔍 Buscando estoque para tenant: {}", tenantAtual);

            List<EstoqueDTO> estoque = estoqueService.listarTodoEstoque();

            log.info("📦 Retornando {} itens de estoque para {}", estoque.size(), tenantAtual);
            return ResponseEntity.ok(estoque);

        } catch (Exception e) {
            log.error("❌ Erro ao listar estoque: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Lista apenas estoques com quantidade > 0
     */
    @GetMapping("/com-quantidade")
    public ResponseEntity<List<EstoqueDTO>> listarEstoquesComQuantidade() {
        try {
            List<EstoqueDTO> estoque = estoqueService.listarEstoquesComQuantidade();
            return ResponseEntity.ok(estoque);
        } catch (Exception e) {
            log.error("❌ Erro ao listar estoque com quantidade: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Adiciona estoque - funciona de forma consolidada
     */
    @PostMapping("/adicionar")
    public ResponseEntity<EstoqueDTO> adicionarEstoque(@RequestBody Map<String, Object> request) {
        try {
            // Validar dados de entrada
            if (!request.containsKey("posteId") || !request.containsKey("quantidade")) {
                log.warn("⚠️ Dados incompletos na requisição: {}", request);
                return ResponseEntity.badRequest().build();
            }

            Long posteId = Long.valueOf(request.get("posteId").toString());
            Integer quantidade = Integer.valueOf(request.get("quantidade").toString());

            // Validações
            if (posteId <= 0) {
                log.warn("⚠️ ID de poste inválido: {}", posteId);
                return ResponseEntity.badRequest().build();
            }

            if (quantidade <= 0) {
                log.warn("⚠️ Quantidade inválida: {}", quantidade);
                return ResponseEntity.badRequest().build();
            }

            log.info("➕ Adicionando {} unidades ao poste ID {} (tenant: {})",
                    quantidade, posteId, TenantContext.getCurrentTenantValue());

            EstoqueDTO estoque = estoqueService.adicionarEstoque(posteId, quantidade);

            log.info("✅ Estoque adicionado com sucesso");
            return ResponseEntity.ok(estoque);

        } catch (NumberFormatException e) {
            log.warn("⚠️ Erro de conversão numérica: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("❌ Erro de negócio ao adicionar estoque: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("❌ Erro interno ao adicionar estoque: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Remove estoque manualmente (para ajustes)
     */
    @PostMapping("/remover")
    public ResponseEntity<String> removerEstoque(@RequestBody Map<String, Object> request) {
        try {
            if (!request.containsKey("posteId") || !request.containsKey("quantidade")) {
                return ResponseEntity.badRequest().body("Dados incompletos");
            }

            Long posteId = Long.valueOf(request.get("posteId").toString());
            Integer quantidade = Integer.valueOf(request.get("quantidade").toString());

            if (quantidade <= 0) {
                return ResponseEntity.badRequest().body("Quantidade deve ser positiva");
            }

            log.info("➖ Removendo {} unidades do poste ID {} (tenant: {})",
                    quantidade, posteId, TenantContext.getCurrentTenantValue());

            estoqueService.reduzirEstoque(posteId, quantidade);

            return ResponseEntity.ok("Estoque reduzido com sucesso");

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Erro de conversão numérica");
        } catch (RuntimeException e) {
            log.error("❌ Erro ao remover estoque: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("❌ Erro interno ao remover estoque: ", e);
            return ResponseEntity.internalServerError().body("Erro interno do servidor");
        }
    }

    /**
     * Endpoint para Jefferson verificar estoque consolidado
     */
    @GetMapping("/consolidado")
    public ResponseEntity<Map<String, Object>> obterEstatisticasConsolidadas() {
        try {
            String tenantAtual = TenantContext.getCurrentTenantValue();

            if (!"jefferson".equals(tenantAtual)) {
                return ResponseEntity.status(403).build(); // CORREÇÃO: usar status(403) ao invés de forbidden()
            }

            List<EstoqueDTO> estoque = estoqueService.listarTodoEstoque();

            long totalTipos = estoque.size();
            long estoquePositivo = estoque.stream().mapToLong(e -> e.getQuantidadeAtual() > 0 ? 1 : 0).sum();
            long estoqueZero = estoque.stream().mapToLong(e -> e.getQuantidadeAtual() == 0 ? 1 : 0).sum();
            long estoqueNegativo = estoque.stream().mapToLong(e -> e.getQuantidadeAtual() < 0 ? 1 : 0).sum();
            long estoqueBaixo = estoque.stream().mapToLong(e -> e.getQuantidadeAtual() > 0 && e.getQuantidadeAtual() <= 5 ? 1 : 0).sum();

            Map<String, Object> estatisticas = Map.of(
                    "totalTipos", totalTipos,
                    "estoquePositivo", estoquePositivo,
                    "estoqueZero", estoqueZero,
                    "estoqueNegativo", estoqueNegativo,
                    "estoqueBaixo", estoqueBaixo
            );

            return ResponseEntity.ok(estatisticas);

        } catch (Exception e) {
            log.error("❌ Erro ao obter estatísticas consolidadas: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
package com.vendas.postes.controller;

import com.vendas.postes.config.TenantContext;
import com.vendas.postes.dto.EstoqueDTO;
import com.vendas.postes.service.EstoqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/estoque")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EstoqueController {

    private final EstoqueService estoqueService;

    /**
     * Lista todo o estoque do tenant atual
     * Inclui postes com estoque zero
     */
    @GetMapping
    public ResponseEntity<List<EstoqueDTO>> listarTodoEstoque() {
        try {
            List<EstoqueDTO> estoque = estoqueService.listarTodoEstoquePorTenant();
            return ResponseEntity.ok(estoque);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista apenas estoques com quantidade diferente de zero do tenant atual
     */
    @GetMapping("/com-quantidade")
    public ResponseEntity<List<EstoqueDTO>> listarEstoquesComQuantidade() {
        try {
            List<EstoqueDTO> estoque = estoqueService.listarEstoquesComQuantidadePorTenant();
            return ResponseEntity.ok(estoque);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista estoques abaixo do mínimo do tenant atual
     */
    @GetMapping("/abaixo-minimo")
    public ResponseEntity<List<EstoqueDTO>> listarEstoquesAbaixoMinimo() {
        try {
            List<EstoqueDTO> estoque = estoqueService.listarEstoquesAbaixoMinimoPorTenant();
            return ResponseEntity.ok(estoque);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca estoque de um poste específico do tenant atual
     */
    @GetMapping("/poste/{posteId}")
    public ResponseEntity<EstoqueDTO> buscarEstoquePorPoste(@PathVariable Long posteId) {
        try {
            Optional<EstoqueDTO> estoque = estoqueService.buscarEstoquePorPostePorTenant(posteId);
            return estoque.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Adiciona quantidade ao estoque de um poste
     */
    @PostMapping("/adicionar")
    public ResponseEntity<EstoqueDTO> adicionarEstoque(@RequestBody Map<String, Object> request) {
        try {
            Long posteId = Long.valueOf(request.get("posteId").toString());
            Integer quantidade = Integer.valueOf(request.get("quantidade").toString());
            String observacao = request.get("observacao") != null ? request.get("observacao").toString() : null;

            EstoqueDTO estoque = estoqueService.adicionarEstoque(posteId, quantidade, observacao);
            return ResponseEntity.ok(estoque);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Atualiza a quantidade mínima de um poste
     */
    @PutMapping("/poste/{posteId}/quantidade-minima")
    public ResponseEntity<EstoqueDTO> atualizarQuantidadeMinima(
            @PathVariable Long posteId,
            @RequestBody Map<String, Object> request) {
        try {
            Integer quantidadeMinima = Integer.valueOf(request.get("quantidadeMinima").toString());

            EstoqueDTO estoque = estoqueService.atualizarQuantidadeMinima(posteId, quantidadeMinima);
            return ResponseEntity.ok(estoque);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Verifica se há estoque suficiente para uma quantidade específica
     * Sistema permite estoque negativo, então sempre retorna disponível
     */
    @PostMapping("/verificar-disponibilidade")
    public ResponseEntity<Map<String, Object>> verificarDisponibilidade(@RequestBody Map<String, Object> request) {
        try {
            Long posteId = Long.valueOf(request.get("posteId").toString());
            Integer quantidade = Integer.valueOf(request.get("quantidade").toString());

            // Buscar estoque atual
            Optional<EstoqueDTO> estoqueOpt = estoqueService.buscarEstoquePorPostePorTenant(posteId);

            if (estoqueOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            EstoqueDTO estoque = estoqueOpt.get();
            Integer quantidadeAtual = estoque.getQuantidadeAtual();
            Integer quantidadeAposOperacao = quantidadeAtual - quantidade;

            // Sistema permite estoque negativo
            boolean disponivel = true;
            String observacao;

            if (quantidadeAposOperacao < 0) {
                observacao = String.format("Estoque ficará negativo: %d (atual: %d, solicitado: %d)",
                        quantidadeAposOperacao, quantidadeAtual, quantidade);
            } else {
                observacao = String.format("Estoque suficiente. Restará: %d unidades", quantidadeAposOperacao);
            }

            Map<String, Object> response = Map.of(
                    "disponivel", disponivel,
                    "posteId", posteId,
                    "quantidadeSolicitada", quantidade,
                    "quantidadeAtual", quantidadeAtual,
                    "quantidadeAposOperacao", quantidadeAposOperacao,
                    "observacao", observacao,
                    "tenant", TenantContext.getCurrentTenantValue() != null ?
                            TenantContext.getCurrentTenantValue() : "vermelho"
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para obter estatísticas do estoque do tenant atual
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas() {
        try {
            List<EstoqueDTO> todoEstoque = estoqueService.listarTodoEstoquePorTenant();
            List<EstoqueDTO> comQuantidade = estoqueService.listarEstoquesComQuantidadePorTenant();
            List<EstoqueDTO> abaixoMinimo = estoqueService.listarEstoquesAbaixoMinimoPorTenant();

            long totalPostes = todoEstoque.size();
            long postesComEstoque = comQuantidade.size();
            long postesAbaixoMinimo = abaixoMinimo.size();
            long postesZerados = totalPostes - postesComEstoque;

            // Calcular estoque negativo
            long postesNegativos = todoEstoque.stream()
                    .mapToLong(e -> e.getQuantidadeAtual() < 0 ? 1 : 0)
                    .sum();

            Map<String, Object> estatisticas = Map.of(
                    "totalPostes", totalPostes,
                    "postesComEstoque", postesComEstoque,
                    "postesZerados", postesZerados,
                    "postesNegativos", postesNegativos,
                    "postesAbaixoMinimo", postesAbaixoMinimo,
                    "tenant", TenantContext.getCurrentTenantValue() != null ?
                            TenantContext.getCurrentTenantValue() : "vermelho"
            );

            return ResponseEntity.ok(estatisticas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
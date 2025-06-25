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

    @GetMapping
    public ResponseEntity<List<EstoqueDTO>> listarTodoEstoque() {
        try {
            List<EstoqueDTO> estoque = estoqueService.listarTodoEstoquePorTenant();
            return ResponseEntity.ok(estoque);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/com-quantidade")
    public ResponseEntity<List<EstoqueDTO>> listarEstoquesComQuantidade() {
        try {
            List<EstoqueDTO> estoque = estoqueService.listarEstoquesComQuantidadePorTenant();
            return ResponseEntity.ok(estoque);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

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

    @PostMapping("/verificar-disponibilidade")
    public ResponseEntity<Map<String, Object>> verificarDisponibilidade(@RequestBody Map<String, Object> request) {
        try {
            Long posteId = Long.valueOf(request.get("posteId").toString());
            Integer quantidade = Integer.valueOf(request.get("quantidade").toString());

            // Sistema permite estoque negativo
            boolean disponivel = true;

            Map<String, Object> response = Map.of(
                    "disponivel", disponivel,
                    "posteId", posteId,
                    "quantidadeSolicitada", quantidade,
                    "observacao", "Sistema permite estoque negativo"
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
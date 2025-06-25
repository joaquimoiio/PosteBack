package com.vendas.postes.controller;

import com.vendas.postes.dto.EstoqueDTO;
import com.vendas.postes.service.EstoqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estoque")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping
    public List<EstoqueDTO> listarTodoEstoque() {
        return estoqueService.listarTodoEstoque();
    }

    @GetMapping("/com-quantidade")
    public List<EstoqueDTO> listarEstoquesComQuantidade() {
        return estoqueService.listarEstoquesComQuantidade();
    }

    @PostMapping("/adicionar")
    public ResponseEntity<EstoqueDTO> adicionarEstoque(@RequestBody Map<String, Object> request) {
        try {
            Long posteId = Long.valueOf(request.get("posteId").toString());
            Integer quantidade = Integer.valueOf(request.get("quantidade").toString());

            EstoqueDTO estoque = estoqueService.adicionarEstoque(posteId, quantidade);
            return ResponseEntity.ok(estoque);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
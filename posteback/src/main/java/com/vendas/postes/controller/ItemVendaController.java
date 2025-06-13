package com.vendas.postes.controller;

import com.vendas.postes.model.ItemVenda;
import com.vendas.postes.repository.ItemVendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itens-venda")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ItemVendaController {

    private final ItemVendaRepository itemVendaRepository;

    @GetMapping
    public List<ItemVenda> listarTodos() {
        return itemVendaRepository.findAll();
    }

    @GetMapping("/venda/{vendaId}")
    public List<ItemVenda> listarPorVenda(@PathVariable Long vendaId) {
        return itemVendaRepository.findByVendaId(vendaId);
    }

    @PostMapping
    public ItemVenda criar(@RequestBody ItemVenda itemVenda) {
        return itemVendaRepository.save(itemVenda);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemVenda> atualizar(@PathVariable Long id, @RequestBody ItemVenda itemAtualizado) {
        return itemVendaRepository.findById(id)
                .map(item -> {
                    item.setQuantidade(itemAtualizado.getQuantidade());
                    item.setPrecoUnitario(itemAtualizado.getPrecoUnitario());
                    return ResponseEntity.ok(itemVendaRepository.save(item));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        return itemVendaRepository.findById(id)
                .map(item -> {
                    itemVendaRepository.delete(item);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

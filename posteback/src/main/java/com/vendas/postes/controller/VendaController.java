package com.vendas.postes.controller;

import com.vendas.postes.dto.ResumoVendasDTO;
import com.vendas.postes.dto.VendaCreateDTO;
import com.vendas.postes.dto.VendaDTO;
import com.vendas.postes.repository.VendaRepository;
import com.vendas.postes.service.VendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vendas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VendaController {

    private final VendaRepository vendaRepository;
    private final VendaService vendaService;

    @GetMapping
    public List<VendaDTO> listarTodas() {
        return vendaService.listarTodasVendas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaDTO> buscarPorId(@PathVariable Long id) {
        Optional<VendaDTO> venda = vendaService.buscarVendaPorId(id);
        return venda.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resumo")
    public ResumoVendasDTO obterResumo() {
        return vendaService.obterDadosParaCalculos();
    }

    @PostMapping
    public ResponseEntity<VendaDTO> criar(@RequestBody VendaCreateDTO vendaCreateDTO) {
        try {
            VendaDTO vendaCriada = vendaService.criarVenda(vendaCreateDTO);
            return ResponseEntity.ok(vendaCriada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<VendaDTO> atualizar(@PathVariable Long id, @RequestBody VendaDTO vendaAtualizada) {
        try {
            VendaDTO venda = vendaService.atualizarVenda(id, vendaAtualizada);
            return ResponseEntity.ok(venda);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            vendaService.deletarVenda(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
package com.vendas.postes.controller;

import com.vendas.postes.dto.ResumoVendasDTO;
import com.vendas.postes.model.Venda;
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
    public List<Venda> listarTodas() {
        return vendaRepository.findAllOrderByDataVendaDesc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venda> buscarPorId(@PathVariable Long id) {
        Optional<Venda> venda = vendaRepository.findById(id);
        return venda.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resumo")
    public ResumoVendasDTO obterResumo() {
        return vendaService.calcularResumoVendas();
    }

    @PostMapping
    public Venda criar(@RequestBody Venda venda) {
        return vendaRepository.save(venda);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venda> atualizar(@PathVariable Long id, @RequestBody Venda vendaAtualizada) {
        return vendaRepository.findById(id)
                .map(venda -> {
                    venda.setTotalFreteEletrons(vendaAtualizada.getTotalFreteEletrons());
                    venda.setTotalComissao(vendaAtualizada.getTotalComissao());
                    venda.setValorTotalInformado(vendaAtualizada.getValorTotalInformado());
                    venda.setObservacoes(vendaAtualizada.getObservacoes());
                    return ResponseEntity.ok(vendaRepository.save(venda));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        return vendaRepository.findById(id)
                .map(venda -> {
                    vendaRepository.delete(venda);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
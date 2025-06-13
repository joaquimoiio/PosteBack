package com.vendas.postes.controller;

import com.vendas.postes.model.Despesa;
import com.vendas.postes.repository.DespesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/despesas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DespesaController {

    private final DespesaRepository despesaRepository;

    @GetMapping
    public List<Despesa> listarTodas() {
        return despesaRepository.findAll();
    }

    @GetMapping("/funcionario")
    public List<Despesa> listarDespesasFuncionario() {
        return despesaRepository.findByTipo(Despesa.TipoDespesa.FUNCIONARIO);
    }

    @GetMapping("/outras")
    public List<Despesa> listarOutrasDespesas() {
        return despesaRepository.findByTipo(Despesa.TipoDespesa.OUTRAS);
    }

    @PostMapping
    public Despesa criar(@RequestBody Despesa despesa) {
        return despesaRepository.save(despesa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Despesa> atualizar(@PathVariable Long id, @RequestBody Despesa despesaAtualizada) {
        return despesaRepository.findById(id)
                .map(despesa -> {
                    despesa.setDescricao(despesaAtualizada.getDescricao());
                    despesa.setValor(despesaAtualizada.getValor());
                    despesa.setTipo(despesaAtualizada.getTipo());
                    return ResponseEntity.ok(despesaRepository.save(despesa));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        return despesaRepository.findById(id)
                .map(despesa -> {
                    despesaRepository.delete(despesa);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
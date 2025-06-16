package com.vendas.postes.controller;

import com.vendas.postes.model.Despesa;
import com.vendas.postes.repository.DespesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/despesas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DespesaController {

    private final DespesaRepository despesaRepository;

    @GetMapping
    public List<Despesa> listarTodas(
            @RequestParam(value = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

            @RequestParam(value = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        if (dataInicio != null || dataFim != null) {
            LocalDate inicio = dataInicio != null ? dataInicio : LocalDate.of(1900, 1, 1);
            LocalDate fim = dataFim != null ? dataFim : LocalDate.now();
            return despesaRepository.findByDataDespesaBetween(inicio, fim);
        }

        return despesaRepository.findAll();
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
                    despesa.setDataDespesa(despesaAtualizada.getDataDespesa());
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
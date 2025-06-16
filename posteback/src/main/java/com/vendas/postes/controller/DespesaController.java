package com.vendas.postes.controller;

import com.vendas.postes.model.Despesa;
import com.vendas.postes.repository.DespesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,

            @RequestParam(value = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {

        if (dataInicio != null || dataFim != null) {
            // Ajustar para início e fim do dia se apenas data for fornecida
            LocalDateTime inicio = dataInicio != null ? dataInicio : LocalDateTime.of(1900, 1, 1, 0, 0);
            LocalDateTime fim = dataFim != null ? dataFim : LocalDateTime.now().plusDays(1);

            return despesaRepository.findByDataDespesaBetween(inicio, fim);
        }

        return despesaRepository.findAll();
    }

    @GetMapping("/funcionario")
    public List<Despesa> listarDespesasFuncionario(
            @RequestParam(value = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,

            @RequestParam(value = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {

        List<Despesa> despesas;

        if (dataInicio != null || dataFim != null) {
            LocalDateTime inicio = dataInicio != null ? dataInicio : LocalDateTime.of(1900, 1, 1, 0, 0);
            LocalDateTime fim = dataFim != null ? dataFim : LocalDateTime.now().plusDays(1);
            despesas = despesaRepository.findByDataDespesaBetween(inicio, fim);
        } else {
            despesas = despesaRepository.findAll();
        }

        return despesas.stream()
                .filter(d -> d.getTipo() == Despesa.TipoDespesa.FUNCIONARIO)
                .toList();
    }

    @GetMapping("/outras")
    public List<Despesa> listarOutrasDespesas(
            @RequestParam(value = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,

            @RequestParam(value = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {

        List<Despesa> despesas;

        if (dataInicio != null || dataFim != null) {
            LocalDateTime inicio = dataInicio != null ? dataInicio : LocalDateTime.of(1900, 1, 1, 0, 0);
            LocalDateTime fim = dataFim != null ? dataFim : LocalDateTime.now().plusDays(1);
            despesas = despesaRepository.findByDataDespesaBetween(inicio, fim);
        } else {
            despesas = despesaRepository.findAll();
        }

        return despesas.stream()
                .filter(d -> d.getTipo() == Despesa.TipoDespesa.OUTRAS)
                .toList();
    }

    @GetMapping("/periodo")
    public List<Despesa> listarPorPeriodo(
            @RequestParam(value = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,

            @RequestParam(value = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {

        if (dataInicio == null && dataFim == null) {
            return despesaRepository.findAll();
        }

        LocalDateTime inicio = dataInicio != null ? dataInicio : LocalDateTime.of(1900, 1, 1, 0, 0);
        LocalDateTime fim = dataFim != null ? dataFim : LocalDateTime.now().plusDays(1);

        return despesaRepository.findByDataDespesaBetween(inicio, fim);
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
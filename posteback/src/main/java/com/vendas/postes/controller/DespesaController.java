package com.vendas.postes.controller;

import com.vendas.postes.config.TenantContext;
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

        String tenantId = TenantContext.getCurrentTenantValue();

        if (dataInicio != null || dataFim != null) {
            LocalDate inicio = dataInicio != null ? dataInicio : LocalDate.of(1900, 1, 1);
            LocalDate fim = dataFim != null ? dataFim : LocalDate.now();

            if (tenantId != null) {
                return despesaRepository.findByTenantIdAndDataDespesaBetween(tenantId, inicio, fim);
            }
            return despesaRepository.findByDataDespesaBetween(inicio, fim);
        }

        if (tenantId != null) {
            return despesaRepository.findByTenantId(tenantId);
        }
        return despesaRepository.findAll();
    }

    @PostMapping
    public Despesa criar(@RequestBody Despesa despesa) {
        // Definir tenant atual
        String tenantId = TenantContext.getCurrentTenantValue();
        if (tenantId != null) {
            despesa.setTenantId(tenantId);
        }
        return despesaRepository.save(despesa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Despesa> atualizar(@PathVariable Long id, @RequestBody Despesa despesaAtualizada) {
        return despesaRepository.findById(id)
                .map(despesa -> {
                    // Verificar se a despesa pertence ao tenant atual
                    String tenantAtual = TenantContext.getCurrentTenantValue();
                    if (tenantAtual != null && !tenantAtual.equals(despesa.getTenantId())) {
                        return ResponseEntity.notFound().<Despesa>build();
                    }

                    despesa.setDescricao(despesaAtualizada.getDescricao());
                    despesa.setValor(despesaAtualizada.getValor());
                    despesa.setTipo(despesaAtualizada.getTipo());
                    despesa.setDataDespesa(despesaAtualizada.getDataDespesa());
                    // Manter o tenant ID original
                    return ResponseEntity.ok(despesaRepository.save(despesa));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        return despesaRepository.findById(id)
                .map(despesa -> {
                    // Verificar se a despesa pertence ao tenant atual
                    String tenantAtual = TenantContext.getCurrentTenantValue();
                    if (tenantAtual != null && !tenantAtual.equals(despesa.getTenantId())) {
                        return ResponseEntity.notFound().build();
                    }

                    despesaRepository.delete(despesa);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
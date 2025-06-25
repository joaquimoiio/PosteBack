package com.vendas.postes.controller;

import com.vendas.postes.dto.VendaCreateDTO;
import com.vendas.postes.dto.VendaDTO;
import com.vendas.postes.dto.ResumoVendasDTO;
import com.vendas.postes.service.VendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vendas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;

    @GetMapping
    public List<VendaDTO> listarTodas(
            @RequestParam(value = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(value = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        if (dataInicio != null || dataFim != null) {
            return vendaService.listarVendasPorPeriodo(dataInicio, dataFim);
        }
        return vendaService.listarTodasVendas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaDTO> buscarPorId(@PathVariable Long id) {
        return vendaService.buscarVendaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resumo")
    public ResumoVendasDTO obterResumo(
            @RequestParam(value = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(value = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        if (dataInicio != null || dataFim != null) {
            return vendaService.obterResumoVendasPorPeriodo(dataInicio, dataFim);
        }
        return vendaService.obterResumoVendas();
    }

    @PostMapping
    public VendaDTO criar(@RequestBody VendaCreateDTO vendaCreateDTO) {
        return vendaService.criarVenda(vendaCreateDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            vendaService.deletarVenda(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
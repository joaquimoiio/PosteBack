package com.vendas.postes.controller;

import com.vendas.postes.dto.RelatorioPosteDTO;
import com.vendas.postes.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/vendas-por-poste")
    public List<RelatorioPosteDTO> relatorioVendasPorPoste(
            @RequestParam(value = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

            @RequestParam(value = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,

            @RequestParam(value = "tipoVenda", required = false) String tipoVenda) {

        return relatorioService.gerarRelatorioVendasPorPoste(dataInicio, dataFim, tipoVenda);
    }
}
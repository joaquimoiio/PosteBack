package com.vendas.postes.controller;

import com.vendas.postes.dto.VendaCreateDTO;
import com.vendas.postes.dto.VendaDTO;
import com.vendas.postes.dto.ResumoVendasDTO;
import com.vendas.postes.service.VendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vendas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VendaController {

    private final VendaService vendaService;

    @GetMapping
    public ResponseEntity<List<VendaDTO>> listarTodas(
            @RequestParam(value = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

            @RequestParam(value = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        try {
            List<VendaDTO> vendas;
            if (dataInicio != null || dataFim != null) {
                vendas = vendaService.listarVendasPorPeriodo(dataInicio, dataFim);
            } else {
                vendas = vendaService.listarTodasVendas();
            }
            return ResponseEntity.ok(vendas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaDTO> buscarPorId(@PathVariable Long id) {
        try {
            Optional<VendaDTO> venda = vendaService.buscarVendaPorId(id);
            return venda.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/resumo")
    public ResponseEntity<ResumoVendasDTO> obterResumo(
            @RequestParam(value = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

            @RequestParam(value = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        try {
            ResumoVendasDTO resumo;
            if (dataInicio != null || dataFim != null) {
                resumo = vendaService.obterResumoVendasPorPeriodo(dataInicio, dataFim);
            } else {
                resumo = vendaService.obterResumoVendas();
            }
            return ResponseEntity.ok(resumo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<VendaDTO> criar(@RequestBody VendaCreateDTO vendaCreateDTO) {
        try {
            VendaDTO vendaCriada = vendaService.criarVenda(vendaCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(vendaCriada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<VendaDTO> atualizar(@PathVariable Long id, @RequestBody VendaDTO vendaAtualizada) {
        try {
            VendaDTO venda = vendaService.atualizarVenda(id, vendaAtualizada);
            return ResponseEntity.ok(venda);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            // Verificar se a venda existe antes de tentar deletar
            Optional<VendaDTO> vendaExistente = vendaService.buscarVendaPorId(id);

            if (vendaExistente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Deletar a venda
            vendaService.deletarVenda(id);

            // Retornar 204 No Content (padrão para DELETE bem-sucedido)
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            // ID inválido ou outros problemas de validação
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            // Venda não encontrada (caso o service lance esta exceção)
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Erro interno do servidor
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
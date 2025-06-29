package com.vendas.postes.controller;

import com.vendas.postes.dto.VendaCreateDTO;
import com.vendas.postes.dto.VendaDTO;
import com.vendas.postes.dto.ResumoVendasDTO;
import com.vendas.postes.service.VendaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vendas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class VendaController {

    private final VendaService vendaService;

    /**
     * Lista todas as vendas com filtros opcionais por período
     */
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
                log.info("📋 Listando vendas do período: {} até {}", dataInicio, dataFim);
            } else {
                vendas = vendaService.listarTodasVendas();
                log.info("📋 Listando todas as vendas");
            }

            return ResponseEntity.ok(vendas);

        } catch (Exception e) {
            log.error("❌ Erro ao listar vendas: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca uma venda específica por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<VendaDTO> buscarPorId(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                log.warn("⚠️ ID inválido fornecido: {}", id);
                return ResponseEntity.badRequest().build();
            }

            return vendaService.buscarVendaPorId(id)
                    .map(venda -> {
                        log.info("✅ Venda encontrada: ID {}", id);
                        return ResponseEntity.ok(venda);
                    })
                    .orElseGet(() -> {
                        log.warn("⚠️ Venda não encontrada: ID {}", id);
                        return ResponseEntity.notFound().build();
                    });

        } catch (Exception e) {
            log.error("❌ Erro ao buscar venda por ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtém resumo das vendas com filtros opcionais
     */
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
                log.info("📊 Gerando resumo do período: {} até {}", dataInicio, dataFim);
            } else {
                resumo = vendaService.obterResumoVendas();
                log.info("📊 Gerando resumo geral das vendas");
            }

            return ResponseEntity.ok(resumo);

        } catch (Exception e) {
            log.error("❌ Erro ao gerar resumo de vendas: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cria uma nova venda - ATUALIZA ESTOQUE CONSOLIDADO
     */
    @PostMapping
    public ResponseEntity<VendaDTO> criar(@RequestBody VendaCreateDTO vendaCreateDTO) {
        try {
            if (vendaCreateDTO == null) {
                log.warn("⚠️ Dados de venda nulos recebidos");
                return ResponseEntity.badRequest().build();
            }

            // Validações básicas
            if (vendaCreateDTO.getTipoVenda() == null) {
                log.warn("⚠️ Tipo de venda não informado");
                return ResponseEntity.badRequest().build();
            }

            if (vendaCreateDTO.getDataVenda() == null) {
                log.warn("⚠️ Data de venda não informada");
                return ResponseEntity.badRequest().build();
            }

            log.info("🛒 Criando nova venda do tipo: {}", vendaCreateDTO.getTipoVenda());

            VendaDTO vendaCriada = vendaService.criarVenda(vendaCreateDTO);

            log.info("✅ Venda criada com sucesso: ID {}", vendaCriada.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(vendaCriada);

        } catch (RuntimeException e) {
            log.error("❌ Erro de negócio ao criar venda: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("❌ Erro interno ao criar venda: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Atualiza uma venda existente - ATUALIZA ESTOQUE CONSOLIDADO
     */
    @PutMapping("/{id}")
    public ResponseEntity<VendaDTO> atualizar(@PathVariable Long id, @RequestBody VendaCreateDTO vendaUpdateDTO) {
        try {
            if (id == null || id <= 0) {
                log.warn("⚠️ ID inválido para atualização: {}", id);
                return ResponseEntity.badRequest().build();
            }

            if (vendaUpdateDTO == null) {
                log.warn("⚠️ Dados de atualização nulos para venda ID: {}", id);
                return ResponseEntity.badRequest().build();
            }

            log.info("✏️ Atualizando venda ID: {}", id);

            VendaDTO vendaAtualizada = vendaService.atualizarVenda(id, vendaUpdateDTO);

            log.info("✅ Venda atualizada com sucesso: ID {}", id);
            return ResponseEntity.ok(vendaAtualizada);

        } catch (RuntimeException e) {
            log.error("❌ Erro de negócio ao atualizar venda ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("❌ Erro interno ao atualizar venda ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deleta uma venda - REVERTE ESTOQUE CONSOLIDADO
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                log.warn("⚠️ ID inválido para exclusão: {}", id);
                return ResponseEntity.badRequest().build();
            }

            log.info("🗑️ Excluindo venda ID: {}", id);

            vendaService.deletarVenda(id);

            log.info("✅ Venda excluída com sucesso: ID {}", id);
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            log.error("❌ Erro de negócio ao excluir venda ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("❌ Erro interno ao excluir venda ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint específico para análise de impacto no estoque
     */
    @GetMapping("/impacto-estoque")
    public ResponseEntity<List<VendaDTO>> analisarImpactoEstoque(
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

            // Filtrar apenas vendas que afetam estoque (V e L)
            List<VendaDTO> vendasComImpacto = vendas.stream()
                    .filter(venda -> venda.getPosteId() != null && venda.getQuantidade() != null)
                    .collect(java.util.stream.Collectors.toList());

            log.info("📊 Analisando impacto no estoque: {} vendas encontradas", vendasComImpacto.size());

            return ResponseEntity.ok(vendasComImpacto);

        } catch (Exception e) {
            log.error("❌ Erro ao analisar impacto no estoque: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
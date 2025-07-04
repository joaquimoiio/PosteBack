package com.vendas.postes.service;

import com.vendas.postes.config.TenantContext;
import com.vendas.postes.dto.*;
import com.vendas.postes.model.Poste;
import com.vendas.postes.model.Venda;
import com.vendas.postes.repository.PosteRepository;
import com.vendas.postes.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendaService {

    private final VendaRepository vendaRepository;
    private final PosteRepository posteRepository;
    private final EstoqueService estoqueService;

    public List<VendaDTO> listarTodasVendas() {
        String tenantId = TenantContext.getCurrentTenantValue();
        List<Venda> vendas = vendaRepository.findByTenantIdOrderByDataVendaDesc(tenantId);
        return vendas.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<VendaDTO> listarVendasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        String tenantId = TenantContext.getCurrentTenantValue();
        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : LocalDateTime.of(1900, 1, 1, 0, 0);
        LocalDateTime fim = dataFim != null ? dataFim.atTime(23, 59, 59) : LocalDateTime.now();

        List<Venda> vendas = vendaRepository.findByTenantIdAndDataVendaBetween(tenantId, inicio, fim);
        return vendas.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Optional<VendaDTO> buscarVendaPorId(Long id) {
        Optional<Venda> vendaOpt = vendaRepository.findById(id);
        if (vendaOpt.isPresent()) {
            Venda venda = vendaOpt.get();
            String tenantAtual = TenantContext.getCurrentTenantValue();
            if (!tenantAtual.equals(venda.getTenantId())) {
                return Optional.empty();
            }
            return Optional.of(convertToDTO(venda));
        }
        return Optional.empty();
    }

    /**
     * Cria uma venda e atualiza o estoque consolidado COM DATA
     */
    @Transactional
    public VendaDTO criarVenda(VendaCreateDTO vendaCreateDTO) {
        String tenantId = TenantContext.getCurrentTenantValue();
        log.info("🛒 Criando venda para tenant: {} - Tipo: {}", tenantId, vendaCreateDTO.getTipoVenda());

        Venda venda = new Venda();
        venda.setDataVenda(vendaCreateDTO.getDataVenda());
        venda.setTipoVenda(vendaCreateDTO.getTipoVenda());
        venda.setQuantidade(vendaCreateDTO.getQuantidade());
        venda.setFreteEletrons(vendaCreateDTO.getFreteEletrons());
        venda.setValorVenda(vendaCreateDTO.getValorVenda());
        venda.setValorExtra(vendaCreateDTO.getValorExtra());
        venda.setObservacoes(vendaCreateDTO.getObservacoes());
        venda.setTenantId(tenantId);

        if (vendaCreateDTO.getPosteId() != null) {
            Poste poste = posteRepository.findById(vendaCreateDTO.getPosteId())
                    .orElseThrow(() -> new RuntimeException("Poste não encontrado"));
            venda.setPoste(poste);
        }

        venda = vendaRepository.save(venda);

        // ⚡ ATUALIZAR ESTOQUE CONSOLIDADO COM DATA DA VENDA ⚡
        // Só atualiza estoque para vendas tipo V e L (que envolvem postes)
        if (venda.getPoste() != null && venda.getQuantidade() != null && venda.getQuantidade() > 0) {
            LocalDate dataVenda = venda.getDataVenda().toLocalDate();
            String observacao = String.format("Venda %s - ID: %d", venda.getTipoVenda().name(), venda.getId());

            log.info("📦 Reduzindo {} unidades do estoque consolidado para poste {} (código: {}) na data {}",
                    venda.getQuantidade(), venda.getPoste().getId(), venda.getPoste().getCodigo(), dataVenda);

            try {
                estoqueService.reduzirEstoqueComData(venda.getPoste().getId(), venda.getQuantidade(), dataVenda, observacao);
                log.info("✅ Estoque consolidado atualizado com sucesso");
            } catch (Exception e) {
                log.error("❌ Erro ao atualizar estoque consolidado: {}", e.getMessage());
                // Continue mesmo se o estoque falhar - não queremos perder a venda
            }
        } else {
            log.info("ℹ️ Venda tipo {} não afeta estoque", venda.getTipoVenda());
        }

        log.info("✅ Venda criada com sucesso: ID {} para tenant {}", venda.getId(), tenantId);
        return convertToDTO(venda);
    }

    /**
     * Atualiza uma venda existente COM CONTROLE DE DATA
     */
    @Transactional
    public VendaDTO atualizarVenda(Long id, VendaCreateDTO vendaUpdateDTO) {
        String tenantId = TenantContext.getCurrentTenantValue();

        Venda vendaExistente = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        if (!tenantId.equals(vendaExistente.getTenantId())) {
            throw new RuntimeException("Não é possível editar venda de outro caminhão");
        }

        // Reverter estoque da venda original se necessário COM DATA ORIGINAL
        if (vendaExistente.getPoste() != null && vendaExistente.getQuantidade() != null) {
            LocalDate dataOriginal = vendaExistente.getDataVenda().toLocalDate();
            String observacaoReversao = String.format("Reversão venda %s - ID: %d (atualização)",
                    vendaExistente.getTipoVenda().name(), vendaExistente.getId());

            estoqueService.adicionarEstoqueComData(vendaExistente.getPoste().getId(),
                    vendaExistente.getQuantidade(), dataOriginal, observacaoReversao);
        }

        // Atualizar dados da venda
        vendaExistente.setDataVenda(vendaUpdateDTO.getDataVenda());
        vendaExistente.setQuantidade(vendaUpdateDTO.getQuantidade());
        vendaExistente.setFreteEletrons(vendaUpdateDTO.getFreteEletrons());
        vendaExistente.setValorVenda(vendaUpdateDTO.getValorVenda());
        vendaExistente.setValorExtra(vendaUpdateDTO.getValorExtra());
        vendaExistente.setObservacoes(vendaUpdateDTO.getObservacoes());

        // Atualizar poste se necessário
        if (vendaUpdateDTO.getPosteId() != null) {
            Poste novoPoste = posteRepository.findById(vendaUpdateDTO.getPosteId())
                    .orElseThrow(() -> new RuntimeException("Poste não encontrado"));
            vendaExistente.setPoste(novoPoste);
        }

        vendaExistente = vendaRepository.save(vendaExistente);

        // Aplicar novo desconto de estoque COM NOVA DATA
        if (vendaExistente.getPoste() != null && vendaExistente.getQuantidade() != null) {
            LocalDate novaData = vendaExistente.getDataVenda().toLocalDate();
            String observacaoNova = String.format("Venda %s - ID: %d (atualizada)",
                    vendaExistente.getTipoVenda().name(), vendaExistente.getId());

            estoqueService.reduzirEstoqueComData(vendaExistente.getPoste().getId(),
                    vendaExistente.getQuantidade(), novaData, observacaoNova);
        }

        return convertToDTO(vendaExistente);
    }

    /**
     * Deleta uma venda e reverte o estoque COM DATA
     */
    @Transactional
    public void deletarVenda(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        String tenantAtual = TenantContext.getCurrentTenantValue();
        if (!tenantAtual.equals(venda.getTenantId())) {
            throw new RuntimeException("Não é possível excluir venda de outro caminhão");
        }

        log.info("🗑️ Excluindo venda ID: {} do tenant: {}", id, tenantAtual);

        // ⚡ REVERTER ESTOQUE CONSOLIDADO COM DATA DA VENDA ⚡
        if (venda.getPoste() != null && venda.getQuantidade() != null && venda.getQuantidade() > 0) {
            LocalDate dataVenda = venda.getDataVenda().toLocalDate();
            String observacao = String.format("Reversão venda %s - ID: %d (exclusão)",
                    venda.getTipoVenda().name(), venda.getId());

            log.info("📦 Revertendo {} unidades para o estoque consolidado do poste {} (código: {}) na data {}",
                    venda.getQuantidade(), venda.getPoste().getId(), venda.getPoste().getCodigo(), dataVenda);

            try {
                estoqueService.adicionarEstoqueComData(venda.getPoste().getId(),
                        venda.getQuantidade(), dataVenda, observacao);
                log.info("✅ Estoque consolidado revertido com sucesso");
            } catch (Exception e) {
                log.error("❌ Erro ao reverter estoque: {}", e.getMessage());
                // Continue mesmo se falhar - não queremos impedir a exclusão
            }
        }

        vendaRepository.delete(venda);
        log.info("✅ Venda excluída com sucesso");
    }

    public ResumoVendasDTO obterResumoVendas() {
        String tenantId = TenantContext.getCurrentTenantValue();
        List<Venda> vendas = vendaRepository.findByTenantId(tenantId);
        return calcularResumo(vendas);
    }

    public ResumoVendasDTO obterResumoVendasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        String tenantId = TenantContext.getCurrentTenantValue();
        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : LocalDateTime.of(1900, 1, 1, 0, 0);
        LocalDateTime fim = dataFim != null ? dataFim.atTime(23, 59, 59) : LocalDateTime.now();

        List<Venda> vendas = vendaRepository.findByTenantIdAndDataVendaBetween(tenantId, inicio, fim);
        return calcularResumo(vendas);
    }

    private ResumoVendasDTO calcularResumo(List<Venda> vendas) {
        BigDecimal totalVendaPostes = BigDecimal.ZERO;
        BigDecimal valorTotalVendas = BigDecimal.ZERO;
        BigDecimal totalFreteEletrons = BigDecimal.ZERO;
        BigDecimal valorTotalExtras = BigDecimal.ZERO;
        long totalE = 0, totalV = 0, totalL = 0;

        for (Venda venda : vendas) {
            switch (venda.getTipoVenda()) {
                case E:
                    totalE++;
                    if (venda.getValorExtra() != null) {
                        valorTotalExtras = valorTotalExtras.add(venda.getValorExtra());
                    }
                    break;
                case V:
                    totalV++;
                    if (venda.getValorVenda() != null) {
                        valorTotalVendas = valorTotalVendas.add(venda.getValorVenda());
                    }
                    if (venda.getPoste() != null && venda.getQuantidade() != null) {
                        BigDecimal custo = venda.getPoste().getPreco().multiply(BigDecimal.valueOf(venda.getQuantidade()));
                        totalVendaPostes = totalVendaPostes.add(custo);
                    }
                    break;
                case L:
                    totalL++;
                    if (venda.getFreteEletrons() != null) {
                        totalFreteEletrons = totalFreteEletrons.add(venda.getFreteEletrons());
                    }
                    break;
            }
        }

        return new ResumoVendasDTO(totalVendaPostes, valorTotalVendas, totalFreteEletrons,
                valorTotalExtras, totalE, totalV, totalL);
    }

    private VendaDTO convertToDTO(Venda venda) {
        VendaDTO dto = new VendaDTO();
        dto.setId(venda.getId());
        dto.setDataVenda(venda.getDataVenda());
        dto.setTipoVenda(venda.getTipoVenda());
        dto.setQuantidade(venda.getQuantidade());
        dto.setFreteEletrons(venda.getFreteEletrons());
        dto.setValorVenda(venda.getValorVenda());
        dto.setValorExtra(venda.getValorExtra());
        dto.setObservacoes(venda.getObservacoes());

        if (venda.getPoste() != null) {
            dto.setPosteId(venda.getPoste().getId());
            dto.setCodigoPoste(venda.getPoste().getCodigo());
            dto.setDescricaoPoste(venda.getPoste().getDescricao());
        }

        return dto;
    }
}
package com.vendas.postes.service;

import com.vendas.postes.config.TenantContext;
import com.vendas.postes.dto.EstoqueDTO;
import com.vendas.postes.model.Estoque;
import com.vendas.postes.model.Poste;
import com.vendas.postes.repository.EstoqueRepository;
import com.vendas.postes.repository.PosteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final PosteRepository posteRepository;

    /**
     * Lista todo o estoque consolidado - busca postes de ambos os caminhões
     * e consolida os estoques por código de poste
     */
    public List<EstoqueDTO> listarTodoEstoque() {
        String tenantAtual = TenantContext.getCurrentTenantValue();
        log.info("🔍 Listando estoque consolidado para tenant: {}", tenantAtual);

        // Se for Jefferson, mostrar estoque consolidado real
        if ("jefferson".equals(tenantAtual)) {
            return listarEstoqueConsolidadoCompleto();
        }

        // Para vermelho e branco, mostrar apenas seus postes mas com estoque consolidado
        List<Poste> postesDoTenant = posteRepository.findByTenantIdAndAtivoTrue(tenantAtual);

        return postesDoTenant.stream().map(poste -> {
            EstoqueDTO estoqueConsolidado = obterEstoqueConsolidadoPorCodigo(poste.getCodigo());
            if (estoqueConsolidado != null) {
                // Usar os dados do poste do tenant atual mas quantidade consolidada
                estoqueConsolidado.setPosteId(poste.getId());
                estoqueConsolidado.setCodigoPoste(poste.getCodigo());
                estoqueConsolidado.setDescricaoPoste(poste.getDescricao());
                estoqueConsolidado.setPrecoPoste(poste.getPreco());
                estoqueConsolidado.setPosteAtivo(poste.getAtivo());
                return estoqueConsolidado;
            } else {
                return criarEstoqueDTOZerado(poste);
            }
        }).collect(Collectors.toList());
    }

    /**
     * Lista estoque consolidado completo - para Jefferson
     */
    private List<EstoqueDTO> listarEstoqueConsolidadoCompleto() {
        log.info("📦 Gerando estoque consolidado completo");

        // Buscar todos os postes ativos de ambos os caminhões
        List<Poste> postesVermelho = posteRepository.findByTenantIdAndAtivoTrue("vermelho");
        List<Poste> postesBranco = posteRepository.findByTenantIdAndAtivoTrue("branco");

        // Mapa para consolidar por código
        Map<String, EstoqueConsolidado> consolidadoPorCodigo = new HashMap<>();

        // Processar postes vermelho
        for (Poste poste : postesVermelho) {
            processarPosteParaConsolidacao(poste, consolidadoPorCodigo, "vermelho");
        }

        // Processar postes branco
        for (Poste poste : postesBranco) {
            processarPosteParaConsolidacao(poste, consolidadoPorCodigo, "branco");
        }

        // Converter para DTO
        return consolidadoPorCodigo.values().stream()
                .map(this::converterConsolidadoParaDTO)
                .sorted(Comparator.comparing(EstoqueDTO::getCodigoPoste))
                .collect(Collectors.toList());
    }

    /**
     * Processa um poste para consolidação
     */
    private void processarPosteParaConsolidacao(Poste poste, Map<String, EstoqueConsolidado> consolidado, String tenant) {
        String codigoBase = extrairCodigoBase(poste.getCodigo());

        EstoqueConsolidado item = consolidado.computeIfAbsent(codigoBase, k -> new EstoqueConsolidado());

        // Configurar dados básicos se ainda não foram configurados
        if (item.codigoPoste == null) {
            item.codigoPoste = codigoBase;
            item.descricaoPoste = limparDescricaoParaConsolidacao(poste.getDescricao());
            item.precoPoste = poste.getPreco();
        }

        // Buscar estoque para este poste específico
        Optional<Estoque> estoqueOpt = estoqueRepository.findByPosteId(poste.getId());
        int quantidade = estoqueOpt.map(Estoque::getQuantidadeAtual).orElse(0);

        // Adicionar à quantidade total
        item.quantidadeTotal += quantidade;

        // Adicionar às quantidades específicas por caminhão
        if ("vermelho".equals(tenant)) {
            item.quantidadeVermelho += quantidade;
        } else if ("branco".equals(tenant)) {
            item.quantidadeBranco += quantidade;
        }

        // Atualizar data de última atualização
        if (estoqueOpt.isPresent() && estoqueOpt.get().getDataAtualizacao() != null) {
            if (item.dataUltimaAtualizacao == null ||
                    estoqueOpt.get().getDataAtualizacao().isAfter(item.dataUltimaAtualizacao)) {
                item.dataUltimaAtualizacao = estoqueOpt.get().getDataAtualizacao();
            }
        }
    }

    /**
     * Extrai o código base removendo sufixos como -B, -C, etc.
     */
    private String extrairCodigoBase(String codigo) {
        if (codigo == null) return "";

        // Remover sufixos comuns como -B, -C
        String codigoLimpo = codigo.replaceAll("-[BC]$", "");
        return codigoLimpo;
    }

    /**
     * Remove indicações de caminhão da descrição
     */
    private String limparDescricaoParaConsolidacao(String descricao) {
        if (descricao == null) return "";

        return descricao
                .replaceAll(" - Vermelho$", "")
                .replaceAll(" - Branco$", "")
                .trim();
    }

    /**
     * Obtém estoque consolidado por código de poste
     */
    private EstoqueDTO obterEstoqueConsolidadoPorCodigo(String codigoPoste) {
        String codigoBase = extrairCodigoBase(codigoPoste);

        // Buscar todos os postes com código similar
        List<Poste> postesRelacionados = buscarPostesRelacionados(codigoBase);

        int quantidadeTotal = 0;

        for (Poste poste : postesRelacionados) {
            Optional<Estoque> estoqueOpt = estoqueRepository.findByPosteId(poste.getId());
            if (estoqueOpt.isPresent()) {
                quantidadeTotal += estoqueOpt.get().getQuantidadeAtual();
            }
        }

        if (!postesRelacionados.isEmpty()) {
            EstoqueDTO dto = new EstoqueDTO();
            dto.setQuantidadeAtual(quantidadeTotal);
            return dto;
        }

        return null;
    }

    /**
     * Busca postes relacionados por código base
     */
    private List<Poste> buscarPostesRelacionados(String codigoBase) {
        List<Poste> todosPostes = posteRepository.findAll();

        return todosPostes.stream()
                .filter(poste -> {
                    String codigoPosteBase = extrairCodigoBase(poste.getCodigo());
                    return codigoBase.equals(codigoPosteBase) && poste.getAtivo();
                })
                .collect(Collectors.toList());
    }

    /**
     * Adiciona estoque - SEMPRE usa o primeiro poste encontrado com o código
     */
    @Transactional
    public EstoqueDTO adicionarEstoque(Long posteId, Integer quantidade) {
        log.info("📦 Adicionando {} unidades ao estoque do poste ID: {}", quantidade, posteId);

        Poste poste = posteRepository.findById(posteId)
                .orElseThrow(() -> new RuntimeException("Poste não encontrado"));

        // Verificar se já existe estoque para este poste específico
        Optional<Estoque> estoqueOpt = estoqueRepository.findByPosteId(posteId);

        Estoque estoque;
        if (estoqueOpt.isPresent()) {
            estoque = estoqueOpt.get();
            estoque.adicionarQuantidade(quantidade);
        } else {
            estoque = new Estoque(poste, quantidade);
        }

        estoque = estoqueRepository.save(estoque);

        log.info("✅ Estoque atualizado: {} unidades para {}", estoque.getQuantidadeAtual(), poste.getCodigo());

        return convertToDTO(estoque);
    }

    /**
     * Reduz estoque - BUSCA PRIMEIRO POSTE DISPONÍVEL COM ESTOQUE
     */
    @Transactional
    public void reduzirEstoque(Long posteId, Integer quantidade) {
        log.info("📤 Reduzindo {} unidades do estoque para poste ID: {}", quantidade, posteId);

        Poste posteOriginal = posteRepository.findById(posteId)
                .orElseThrow(() -> new RuntimeException("Poste não encontrado"));

        String codigoBase = extrairCodigoBase(posteOriginal.getCodigo());

        // Buscar todos os postes relacionados
        List<Poste> postesRelacionados = buscarPostesRelacionados(codigoBase);

        // Tentar reduzir do estoque existente primeiro
        int quantidadeRestante = quantidade;

        for (Poste poste : postesRelacionados) {
            if (quantidadeRestante <= 0) break;

            Optional<Estoque> estoqueOpt = estoqueRepository.findByPosteId(poste.getId());
            if (estoqueOpt.isPresent()) {
                Estoque estoque = estoqueOpt.get();
                int quantidadeDisponivel = estoque.getQuantidadeAtual();

                if (quantidadeDisponivel > 0) {
                    int quantidadeAReduzir = Math.min(quantidadeRestante, quantidadeDisponivel);
                    estoque.removerQuantidade(quantidadeAReduzir);
                    estoqueRepository.save(estoque);
                    quantidadeRestante -= quantidadeAReduzir;

                    log.info("📉 Reduzido {} unidades do poste {} (restam {} no estoque)",
                            quantidadeAReduzir, poste.getCodigo(), estoque.getQuantidadeAtual());
                }
            }
        }

        // Se ainda restou quantidade para reduzir, criar estoque negativo no poste original
        if (quantidadeRestante > 0) {
            Optional<Estoque> estoqueOriginalOpt = estoqueRepository.findByPosteId(posteId);

            Estoque estoqueOriginal;
            if (estoqueOriginalOpt.isPresent()) {
                estoqueOriginal = estoqueOriginalOpt.get();
                estoqueOriginal.removerQuantidade(quantidadeRestante);
            } else {
                estoqueOriginal = new Estoque(posteOriginal, -quantidadeRestante);
            }

            estoqueRepository.save(estoqueOriginal);

            log.warn("⚠️ Estoque negativo criado para {} - faltaram {} unidades",
                    posteOriginal.getCodigo(), quantidadeRestante);
        }
    }

    public List<EstoqueDTO> listarEstoquesComQuantidade() {
        String tenantId = TenantContext.getCurrentTenantValue();
        List<Estoque> estoques = estoqueRepository.findEstoquesComQuantidadePorTenant(tenantId);
        return estoques.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private EstoqueDTO convertToDTO(Estoque estoque) {
        EstoqueDTO dto = new EstoqueDTO();
        dto.setId(estoque.getId());
        dto.setPosteId(estoque.getPoste().getId());
        dto.setCodigoPoste(estoque.getPoste().getCodigo());
        dto.setDescricaoPoste(estoque.getPoste().getDescricao());
        dto.setPrecoPoste(estoque.getPoste().getPreco());
        dto.setPosteAtivo(estoque.getPoste().getAtivo());
        dto.setQuantidadeAtual(estoque.getQuantidadeAtual());
        dto.setQuantidadeMinima(estoque.getQuantidadeMinima());
        dto.setDataAtualizacao(estoque.getDataAtualizacao());
        dto.setEstoqueAbaixoMinimo(estoque.getQuantidadeAtual() <= estoque.getQuantidadeMinima());
        return dto;
    }

    private EstoqueDTO converterConsolidadoParaDTO(EstoqueConsolidado consolidado) {
        EstoqueDTO dto = new EstoqueDTO();
        dto.setCodigoPoste(consolidado.codigoPoste);
        dto.setDescricaoPoste(consolidado.descricaoPoste);
        dto.setPrecoPoste(consolidado.precoPoste);
        dto.setQuantidadeAtual(consolidado.quantidadeTotal);
        dto.setDataAtualizacao(consolidado.dataUltimaAtualizacao);
        dto.setPosteAtivo(true);
        dto.setEstoqueAbaixoMinimo(consolidado.quantidadeTotal <= 5);
        return dto;
    }

    private EstoqueDTO criarEstoqueDTOZerado(Poste poste) {
        EstoqueDTO dto = new EstoqueDTO();
        dto.setPosteId(poste.getId());
        dto.setCodigoPoste(poste.getCodigo());
        dto.setDescricaoPoste(poste.getDescricao());
        dto.setPrecoPoste(poste.getPreco());
        dto.setPosteAtivo(poste.getAtivo());
        dto.setQuantidadeAtual(0);
        dto.setQuantidadeMinima(0);
        dto.setEstoqueAbaixoMinimo(false);
        return dto;
    }

    /**
     * Classe auxiliar para consolidação de estoque
     */
    private static class EstoqueConsolidado {
        String codigoPoste;
        String descricaoPoste;
        java.math.BigDecimal precoPoste;
        int quantidadeTotal = 0;
        int quantidadeVermelho = 0;
        int quantidadeBranco = 0;
        java.time.LocalDateTime dataUltimaAtualizacao;
    }
}
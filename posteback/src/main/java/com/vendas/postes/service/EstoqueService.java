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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final PosteRepository posteRepository;

    public List<EstoqueDTO> listarTodoEstoque() {
        String tenantId = TenantContext.getCurrentTenantValue();
        List<Poste> postes = posteRepository.findByTenantIdAndAtivoTrue(tenantId);

        return postes.stream().map(poste -> {
            Optional<Estoque> estoqueOpt = estoqueRepository.findByPosteId(poste.getId());
            if (estoqueOpt.isPresent()) {
                return convertToDTO(estoqueOpt.get());
            } else {
                return criarEstoqueDTOZerado(poste);
            }
        }).collect(Collectors.toList());
    }

    public List<EstoqueDTO> listarEstoquesComQuantidade() {
        String tenantId = TenantContext.getCurrentTenantValue();
        List<Estoque> estoques = estoqueRepository.findEstoquesComQuantidadePorTenant(tenantId);
        return estoques.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public EstoqueDTO adicionarEstoque(Long posteId, Integer quantidade) {
        Poste poste = posteRepository.findById(posteId)
                .orElseThrow(() -> new RuntimeException("Poste não encontrado"));

        Optional<Estoque> estoqueOpt = estoqueRepository.findByPosteId(posteId);

        Estoque estoque;
        if (estoqueOpt.isPresent()) {
            estoque = estoqueOpt.get();
            estoque.adicionarQuantidade(quantidade);
        } else {
            estoque = new Estoque(poste, quantidade);
        }

        estoque = estoqueRepository.save(estoque);
        return convertToDTO(estoque);
    }

    @Transactional
    public void reduzirEstoque(Long posteId, Integer quantidade) {
        Optional<Estoque> estoqueOpt = estoqueRepository.findByPosteId(posteId);

        if (estoqueOpt.isPresent()) {
            Estoque estoque = estoqueOpt.get();
            estoque.removerQuantidade(quantidade);
            estoqueRepository.save(estoque);
        } else {
            // Criar estoque negativo se não existir
            Poste poste = posteRepository.findById(posteId)
                    .orElseThrow(() -> new RuntimeException("Poste não encontrado"));
            Estoque estoque = new Estoque(poste, -quantidade);
            estoqueRepository.save(estoque);
        }
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
}
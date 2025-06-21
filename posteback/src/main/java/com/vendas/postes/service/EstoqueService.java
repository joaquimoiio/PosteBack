package com.vendas.postes.service;

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
        log.debug("Listando todo o estoque");

        // Buscar todos os postes ativos
        List<Poste> postesAtivos = posteRepository.findByAtivoTrue();

        return postesAtivos.stream().map(poste -> {
            Optional<Estoque> estoqueOpt = estoqueRepository.findByPoste(poste);

            if (estoqueOpt.isPresent()) {
                return convertToDTO(estoqueOpt.get());
            } else {
                // Criar entrada de estoque zerada para postes sem estoque
                return criarEstoqueDTOZerado(poste);
            }
        }).collect(Collectors.toList());
    }

    public List<EstoqueDTO> listarEstoquesComQuantidade() {
        log.debug("Listando estoques com quantidade");
        List<Estoque> estoques = estoqueRepository.findEstoquesComQuantidade();
        return estoques.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<EstoqueDTO> listarEstoquesAbaixoMinimo() {
        log.debug("Listando estoques abaixo do mínimo");
        List<Estoque> estoques = estoqueRepository.findEstoquesAbaixoMinimo();
        return estoques.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Optional<EstoqueDTO> buscarEstoquePorPoste(Long posteId) {
        log.debug("Buscando estoque para poste ID: {}", posteId);

        Optional<Estoque> estoqueOpt = estoqueRepository.findByPosteId(posteId);
        if (estoqueOpt.isPresent()) {
            return Optional.of(convertToDTO(estoqueOpt.get()));
        }

        // Se não existe estoque, buscar o poste e criar entrada zerada
        Optional<Poste> posteOpt = posteRepository.findById(posteId);
        if (posteOpt.isPresent() && posteOpt.get().getAtivo()) {
            return Optional.of(criarEstoqueDTOZerado(posteOpt.get()));
        }

        return Optional.empty();
    }

    @Transactional
    public EstoqueDTO adicionarEstoque(Long posteId, Integer quantidade, String observacao) {
        log.debug("Adicionando {} unidades ao estoque do poste ID: {}", quantidade, posteId);

        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        Poste poste = posteRepository.findById(posteId)
                .orElseThrow(() -> new IllegalArgumentException("Poste não encontrado com ID: " + posteId));

        if (!poste.getAtivo()) {
            throw new IllegalArgumentException("Não é possível adicionar estoque para poste inativo");
        }

        Optional<Estoque> estoqueOpt = estoqueRepository.findByPoste(poste);

        Estoque estoque;
        if (estoqueOpt.isPresent()) {
            estoque = estoqueOpt.get();
            estoque.adicionarQuantidade(quantidade);
        } else {
            estoque = new Estoque(poste, quantidade);
        }

        estoque = estoqueRepository.save(estoque);
        log.info("Estoque atualizado. Poste: {}, Nova quantidade: {}", poste.getCodigo(), estoque.getQuantidadeAtual());

        return convertToDTO(estoque);
    }

    @Transactional
    public boolean removerEstoque(Long posteId, Integer quantidade) {
        log.debug("Removendo {} unidades do estoque do poste ID: {}", quantidade, posteId);

        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        Optional<Estoque> estoqueOpt = estoqueRepository.findByPosteId(posteId);

        if (estoqueOpt.isPresent()) {
            Estoque estoque = estoqueOpt.get();
            if (estoque.removerQuantidade(quantidade)) {
                estoqueRepository.save(estoque);
                log.info("Estoque reduzido. Poste: {}, Nova quantidade: {}",
                        estoque.getPoste().getCodigo(), estoque.getQuantidadeAtual());
                return true;
            } else {
                log.warn("Estoque insuficiente. Poste: {}, Disponível: {}, Solicitado: {}",
                        estoque.getPoste().getCodigo(), estoque.getQuantidadeAtual(), quantidade);
                return false;
            }
        }

        log.warn("Estoque não encontrado para poste ID: {}", posteId);
        return false;
    }

    /**
     * NOVO MÉTODO: Reduz estoque sem validação, permite negativo
     * Usado pelo VendaService para permitir vendas com estoque insuficiente
     */
    @Transactional
    public void reduzirEstoqueForcado(Long posteId, Integer quantidade) {
        log.debug("Reduzindo estoque FORÇADAMENTE (pode ficar negativo). Poste ID: {}, Quantidade: {}", posteId, quantidade);

        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        Poste poste = posteRepository.findById(posteId)
                .orElseThrow(() -> new IllegalArgumentException("Poste não encontrado com ID: " + posteId));

        Optional<Estoque> estoqueOpt = estoqueRepository.findByPoste(poste);

        Estoque estoque;
        if (estoqueOpt.isPresent()) {
            estoque = estoqueOpt.get();
        } else {
            // Criar entrada de estoque zerada se não existir
            estoque = new Estoque(poste, 0);
        }

        // Reduzir quantidade SEM validação (pode ficar negativo)
        estoque.setQuantidadeAtual(estoque.getQuantidadeAtual() - quantidade);
        estoque.setDataAtualizacao(java.time.LocalDateTime.now());

        estoqueRepository.save(estoque);

        if (estoque.getQuantidadeAtual() < 0) {
            log.warn("⚠️ ESTOQUE NEGATIVO! Poste: {}, Quantidade atual: {}",
                    poste.getCodigo(), estoque.getQuantidadeAtual());
        } else {
            log.info("Estoque reduzido com sucesso. Poste: {}, Nova quantidade: {}",
                    poste.getCodigo(), estoque.getQuantidadeAtual());
        }
    }

    public boolean verificarEstoqueSuficiente(Long posteId, Integer quantidade) {
        log.debug("Verificando estoque suficiente. Poste ID: {}, Quantidade: {}", posteId, quantidade);

        if (quantidade <= 0) {
            return true; // Quantidade zero ou negativa não requer estoque
        }

        return estoqueRepository.existeEstoqueSuficiente(posteId, quantidade);
    }

    @Transactional
    public EstoqueDTO atualizarQuantidadeMinima(Long posteId, Integer quantidadeMinima) {
        log.debug("Atualizando quantidade mínima. Poste ID: {}, Quantidade mínima: {}", posteId, quantidadeMinima);

        Poste poste = posteRepository.findById(posteId)
                .orElseThrow(() -> new IllegalArgumentException("Poste não encontrado com ID: " + posteId));

        Optional<Estoque> estoqueOpt = estoqueRepository.findByPoste(poste);

        Estoque estoque;
        if (estoqueOpt.isPresent()) {
            estoque = estoqueOpt.get();
            estoque.setQuantidadeMinima(quantidadeMinima);
        } else {
            estoque = new Estoque(poste, 0);
            estoque.setQuantidadeMinima(quantidadeMinima);
        }

        estoque = estoqueRepository.save(estoque);
        log.info("Quantidade mínima atualizada. Poste: {}, Quantidade mínima: {}",
                poste.getCodigo(), quantidadeMinima);

        return convertToDTO(estoque);
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
        dto.setEstoqueAbaixoMinimo(estoque.estoqueAbaixoDoMinimo());
        return dto;
    }

    private EstoqueDTO criarEstoqueDTOZerado(Poste poste) {
        EstoqueDTO dto = new EstoqueDTO();
        dto.setId(null);
        dto.setPosteId(poste.getId());
        dto.setCodigoPoste(poste.getCodigo());
        dto.setDescricaoPoste(poste.getDescricao());
        dto.setPrecoPoste(poste.getPreco());
        dto.setPosteAtivo(poste.getAtivo());
        dto.setQuantidadeAtual(0);
        dto.setQuantidadeMinima(0);
        dto.setDataAtualizacao(null);
        dto.setEstoqueAbaixoMinimo(false);
        return dto;
    }
}
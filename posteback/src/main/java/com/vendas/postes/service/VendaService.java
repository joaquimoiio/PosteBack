package com.vendas.postes.service;

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

/**
 * Service para gerenciamento de vendas SEM validação de estoque.
 *
 * MODIFICAÇÕES IMPLEMENTADAS:
 * - Remoção da verificação de estoque suficiente
 * - Permitir estoque negativo
 * - Atualização automática do estoque após venda
 *
 * @author Sistema de Vendas de Postes
 * @version 2.1
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VendaService {

    private final VendaRepository vendaRepository;
    private final PosteRepository posteRepository;
    private final EstoqueService estoqueService;

    // ===== OPERAÇÕES DE CONSULTA =====

    /**
     * Lista todas as vendas ordenadas por data (mais recentes primeiro)
     */
    public List<VendaDTO> listarTodasVendas() {
        log.debug("Listando todas as vendas");

        List<Venda> vendas = vendaRepository.findAllByOrderByDataVendaDesc();
        return convertToDTO(vendas);
    }

    /**
     * Lista vendas em um período específico
     */
    public List<VendaDTO> listarVendasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        log.debug("Listando vendas por período: {} a {}", dataInicio, dataFim);

        LocalDateTime inicio = determinarDataInicio(dataInicio);
        LocalDateTime fim = determinarDataFim(dataFim);

        List<Venda> vendas = vendaRepository.findByDataVendaBetween(inicio, fim);

        return vendas.stream()
                .sorted((v1, v2) -> v2.getDataVenda().compareTo(v1.getDataVenda()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca uma venda específica por ID
     */
    public Optional<VendaDTO> buscarVendaPorId(Long id) {
        log.debug("Buscando venda por ID: {}", id);

        validarId(id);
        return vendaRepository.findById(id).map(this::convertToDTO);
    }

    // ===== OPERAÇÕES DE RESUMO =====

    /**
     * Obtém resumo de todas as vendas
     */
    public ResumoVendasDTO obterResumoVendas() {
        log.debug("Calculando resumo de todas as vendas");

        List<Venda> vendas = vendaRepository.findAll();
        return calcularResumoVendas(vendas);
    }

    /**
     * Obtém resumo de vendas por período
     */
    public ResumoVendasDTO obterResumoVendasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        log.debug("Calculando resumo de vendas por período: {} a {}", dataInicio, dataFim);

        LocalDateTime inicio = determinarDataInicio(dataInicio);
        LocalDateTime fim = determinarDataFim(dataFim);

        List<Venda> vendas = vendaRepository.findByDataVendaBetween(inicio, fim);
        return calcularResumoVendas(vendas);
    }

    // ===== OPERAÇÕES DE MODIFICAÇÃO =====

    /**
     * Cria uma nova venda SEM verificação de estoque
     *
     * MODIFICAÇÃO: Removida a verificação de estoque suficiente
     * Agora permite vendas mesmo com estoque insuficiente (estoque pode ficar negativo)
     */
    @Transactional
    public VendaDTO criarVenda(VendaCreateDTO vendaCreateDTO) {
        log.info("Iniciando criação de venda tipo: {}", vendaCreateDTO.getTipoVenda());

        try {
            // 1. Validações básicas
            validarDadosVenda(vendaCreateDTO);

            // 2. Validação específica por tipo
            validarDadosPorTipo(vendaCreateDTO);

            // 3. REMOÇÃO: Não há mais verificação de estoque
            // A venda pode ser realizada mesmo com estoque insuficiente

            // 4. Criar e persistir venda
            Venda venda = construirVenda(vendaCreateDTO);
            venda = vendaRepository.save(venda);

            // 5. Atualizar estoque (pode ficar negativo)
            atualizarEstoqueSemValidacao(venda);

            // 6. Log de sucesso
            log.info("Venda criada com sucesso. ID: {}, Tipo: {}", venda.getId(), venda.getTipoVenda());

            return convertToDTO(venda);

        } catch (Exception e) {
            log.error("Erro ao criar venda: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Atualiza uma venda existente (campos editáveis apenas)
     */
    @Transactional
    public VendaDTO atualizarVenda(Long id, VendaDTO vendaDTO) {
        log.info("Iniciando atualização da venda ID: {}", id);

        try {
            validarId(id);
            validarDadosEdicao(vendaDTO);

            Venda venda = buscarVendaOuLancarExcecao(id);

            // Atualizar apenas campos editáveis (sem mexer no estoque)
            atualizarCamposEditaveis(venda, vendaDTO);

            venda = vendaRepository.save(venda);

            log.info("Venda atualizada com sucesso. ID: {}", id);
            return convertToDTO(venda);

        } catch (Exception e) {
            log.error("Erro ao atualizar venda ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Deleta uma venda e reverte o estoque automaticamente
     */
    @Transactional
    public void deletarVenda(Long id) {
        log.info("Iniciando exclusão da venda ID: {}", id);

        try {
            validarId(id);
            Venda venda = buscarVendaOuLancarExcecao(id);

            // Reverter estoque antes de deletar
            reverterEstoqueSeNecessario(venda);

            // Deletar venda
            vendaRepository.delete(venda);

            log.info("Venda deletada com sucesso. ID: {}, Estoque revertido automaticamente", id);

        } catch (Exception e) {
            log.error("Erro ao deletar venda ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Erro ao deletar venda: " + e.getMessage(), e);
        }
    }

    // ===== VALIDAÇÕES =====

    private void validarDadosVenda(VendaCreateDTO vendaDTO) {
        if (vendaDTO == null) {
            throw new IllegalArgumentException("Dados da venda não podem ser nulos");
        }

        if (vendaDTO.getDataVenda() == null) {
            throw new IllegalArgumentException("Data da venda é obrigatória");
        }

        if (vendaDTO.getTipoVenda() == null) {
            throw new IllegalArgumentException("Tipo da venda é obrigatório");
        }
    }

    private void validarDadosPorTipo(VendaCreateDTO vendaDTO) {
        switch (vendaDTO.getTipoVenda()) {
            case E:
                validarVendaExtra(vendaDTO);
                break;
            case V:
                validarVendaNormal(vendaDTO);
                break;
            case L:
                validarVendaLoja(vendaDTO);
                break;
            default:
                throw new IllegalArgumentException("Tipo de venda inválido: " + vendaDTO.getTipoVenda());
        }
    }

    private void validarVendaExtra(VendaCreateDTO vendaDTO) {
        if (vendaDTO.getValorExtra() == null || vendaDTO.getValorExtra().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor extra deve ser maior que zero para vendas tipo E");
        }
    }

    private void validarVendaNormal(VendaCreateDTO vendaDTO) {
        if (vendaDTO.getPosteId() == null) {
            throw new IllegalArgumentException("Poste é obrigatório para vendas tipo V");
        }

        if (vendaDTO.getValorVenda() == null || vendaDTO.getValorVenda().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de venda deve ser maior que zero para vendas tipo V");
        }

        if (vendaDTO.getQuantidade() == null || vendaDTO.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero para vendas tipo V");
        }
    }

    private void validarVendaLoja(VendaCreateDTO vendaDTO) {
        if (vendaDTO.getPosteId() == null) {
            throw new IllegalArgumentException("Poste de referência é obrigatório para vendas tipo L");
        }

        if (vendaDTO.getQuantidade() == null || vendaDTO.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero para vendas tipo L");
        }
    }

    private void validarDadosEdicao(VendaDTO vendaDTO) {
        if (vendaDTO == null) {
            throw new IllegalArgumentException("Dados da venda não podem ser nulos");
        }
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da venda deve ser um número positivo");
        }
    }

    // ===== OPERAÇÕES DE ESTOQUE (MODIFICADAS) =====

    /**
     * NOVA IMPLEMENTAÇÃO: Atualiza estoque sem validação
     * Permite que o estoque fique negativo
     */
    private void atualizarEstoqueSemValidacao(Venda venda) {
        if (!precisaAtualizarEstoque(venda)) {
            return;
        }

        Long posteId = venda.getPoste().getId();
        Integer quantidade = venda.getQuantidade() != null ? venda.getQuantidade() : 1;

        log.debug("Reduzindo estoque SEM validação. Poste ID: {}, Quantidade: {}", posteId, quantidade);

        try {
            // Força a redução do estoque, mesmo que fique negativo
            estoqueService.reduzirEstoqueForcado(posteId, quantidade);

            log.info("Estoque reduzido (pode estar negativo). Venda ID: {}, Poste: {}, Quantidade: {}",
                    venda.getId(), venda.getPoste().getCodigo(), quantidade);

        } catch (Exception e) {
            log.error("Erro ao reduzir estoque para venda ID {}: {}", venda.getId(), e.getMessage());
            // Não falha a venda por erro no estoque
        }
    }

    private void reverterEstoqueSeNecessario(Venda venda) {
        if (!precisaAtualizarEstoque(venda)) {
            return;
        }

        Long posteId = venda.getPoste().getId();
        Integer quantidade = venda.getQuantidade() != null ? venda.getQuantidade() : 1;

        log.debug("Revertendo estoque. Poste ID: {}, Quantidade: {}", posteId, quantidade);

        try {
            estoqueService.adicionarEstoque(
                    posteId,
                    quantidade,
                    "Devolução por exclusão de venda ID: " + venda.getId()
            );

            log.info("Estoque revertido com sucesso. Venda ID: {}, Poste: {}, Quantidade: {}",
                    venda.getId(), venda.getPoste().getCodigo(), quantidade);

        } catch (Exception e) {
            log.error("Erro ao reverter estoque para venda ID {}: {}", venda.getId(), e.getMessage());
            // Não falha a exclusão da venda por erro na reversão do estoque
            // mas registra o erro para auditoria
        }
    }

    private boolean precisaAtualizarEstoque(Venda venda) {
        return (venda.getTipoVenda() == Venda.TipoVenda.V ||
                venda.getTipoVenda() == Venda.TipoVenda.L) &&
                venda.getPoste() != null;
    }

    // ===== CONSTRUÇÃO DE OBJETOS =====

    private Venda construirVenda(VendaCreateDTO vendaDTO) {
        Venda venda = new Venda();
        venda.setDataVenda(vendaDTO.getDataVenda());
        venda.setTipoVenda(vendaDTO.getTipoVenda());
        venda.setObservacoes(vendaDTO.getObservacoes());

        // Configurar poste se necessário
        if (vendaDTO.getPosteId() != null) {
            Poste poste = buscarPosteOuLancarExcecao(vendaDTO.getPosteId());
            validarPosteAtivo(poste);
            venda.setPoste(poste);
        }

        // Configurar campos específicos por tipo
        configurarCamposPorTipo(venda, vendaDTO);

        return venda;
    }

    private void configurarCamposPorTipo(Venda venda, VendaCreateDTO vendaDTO) {
        venda.setQuantidade(vendaDTO.getQuantidade());
        venda.setFreteEletrons(vendaDTO.getFreteEletrons());
        venda.setValorVenda(vendaDTO.getValorVenda());
        venda.setValorExtra(vendaDTO.getValorExtra());
    }

    private void atualizarCamposEditaveis(Venda venda, VendaDTO vendaDTO) {
        // Apenas campos que podem ser editados sem afetar estoque
        venda.setFreteEletrons(vendaDTO.getFreteEletrons());
        venda.setValorVenda(vendaDTO.getValorVenda());
        venda.setValorExtra(vendaDTO.getValorExtra());
        venda.setObservacoes(vendaDTO.getObservacoes());
    }

    // ===== BUSCA E VALIDAÇÃO DE ENTIDADES =====

    private Venda buscarVendaOuLancarExcecao(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com ID: " + id));
    }

    private Poste buscarPosteOuLancarExcecao(Long posteId) {
        return posteRepository.findById(posteId)
                .orElseThrow(() -> new IllegalArgumentException("Poste não encontrado com ID: " + posteId));
    }

    private void validarPosteAtivo(Poste poste) {
        if (!poste.getAtivo()) {
            throw new IllegalArgumentException("Poste inativo não pode ser usado em vendas");
        }
    }

    // ===== CÁLCULOS DE RESUMO =====

    private ResumoVendasDTO calcularResumoVendas(List<Venda> vendas) {
        log.debug("Calculando resumo para {} vendas", vendas.size());

        // Separar vendas por tipo
        List<Venda> vendasE = filtrarPorTipo(vendas, Venda.TipoVenda.E);
        List<Venda> vendasV = filtrarPorTipo(vendas, Venda.TipoVenda.V);
        List<Venda> vendasL = filtrarPorTipo(vendas, Venda.TipoVenda.L);

        // Calcular totais
        BigDecimal totalVendaPostes = calcularCustoPostes(vendasV);
        BigDecimal valorTotalVendas = calcularValorVendas(vendasV);
        BigDecimal totalFreteEletrons = calcularFreteEletrons(vendasL);
        BigDecimal valorTotalExtras = calcularValorExtras(vendasE);
        BigDecimal totalContribuicoesExtras = valorTotalExtras.add(totalFreteEletrons);

        return new ResumoVendasDTO(
                totalVendaPostes,
                valorTotalVendas,
                totalFreteEletrons,
                valorTotalExtras,
                (long) vendasE.size(),
                (long) vendasV.size(),
                (long) vendasL.size(),
                BigDecimal.ZERO, // despesasFuncionario - calculado no frontend
                BigDecimal.ZERO, // outrasDespesas - calculado no frontend
                totalContribuicoesExtras
        );
    }

    private List<Venda> filtrarPorTipo(List<Venda> vendas, Venda.TipoVenda tipo) {
        return vendas.stream()
                .filter(v -> v.getTipoVenda() == tipo)
                .collect(Collectors.toList());
    }

    private BigDecimal calcularCustoPostes(List<Venda> vendasV) {
        return vendasV.stream()
                .map(this::calcularCustoPosteVenda)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularCustoPosteVenda(Venda venda) {
        if (venda.getPoste() != null && venda.getQuantidade() != null) {
            return venda.getPoste().getPreco().multiply(BigDecimal.valueOf(venda.getQuantidade()));
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calcularValorVendas(List<Venda> vendasV) {
        return vendasV.stream()
                .map(v -> v.getValorVenda() != null ? v.getValorVenda() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularFreteEletrons(List<Venda> vendasL) {
        return vendasL.stream()
                .map(v -> v.getFreteEletrons() != null ? v.getFreteEletrons() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularValorExtras(List<Venda> vendasE) {
        return vendasE.stream()
                .map(v -> v.getValorExtra() != null ? v.getValorExtra() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ===== CONVERSÕES DTO =====

    private List<VendaDTO> convertToDTO(List<Venda> vendas) {
        return vendas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private VendaDTO convertToDTO(Venda venda) {
        VendaDTO dto = new VendaDTO();

        // Campos básicos
        dto.setId(venda.getId());
        dto.setDataVenda(venda.getDataVenda());
        dto.setTipoVenda(venda.getTipoVenda());
        dto.setQuantidade(venda.getQuantidade());
        dto.setFreteEletrons(venda.getFreteEletrons());
        dto.setValorVenda(venda.getValorVenda());
        dto.setValorExtra(venda.getValorExtra());
        dto.setObservacoes(venda.getObservacoes());

        // Informações do poste (se existir)
        if (venda.getPoste() != null) {
            dto.setPosteId(venda.getPoste().getId());
            dto.setCodigoPoste(venda.getPoste().getCodigo());
            dto.setDescricaoPoste(venda.getPoste().getDescricao());
        }

        return dto;
    }

    // ===== UTILITÁRIOS =====

    private LocalDateTime determinarDataInicio(LocalDate dataInicio) {
        return dataInicio != null ?
                dataInicio.atStartOfDay() :
                LocalDateTime.of(1900, 1, 1, 0, 0);
    }

    private LocalDateTime determinarDataFim(LocalDate dataFim) {
        return dataFim != null ?
                dataFim.atTime(23, 59, 59) :
                LocalDateTime.now();
    }
}
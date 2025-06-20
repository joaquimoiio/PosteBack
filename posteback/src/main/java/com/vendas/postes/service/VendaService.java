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

@Service
@RequiredArgsConstructor
@Slf4j
public class VendaService {

    private final VendaRepository vendaRepository;
    private final PosteRepository posteRepository;

    public List<VendaDTO> listarTodasVendas() {
        log.debug("Listando todas as vendas");
        List<Venda> vendas = vendaRepository.findAllByOrderByDataVendaDesc();
        return vendas.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<VendaDTO> listarVendasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        log.debug("Listando vendas por período: {} a {}", dataInicio, dataFim);

        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : LocalDateTime.of(1900, 1, 1, 0, 0);
        LocalDateTime fim = dataFim != null ? dataFim.atTime(23, 59, 59) : LocalDateTime.now();

        List<Venda> vendas = vendaRepository.findByDataVendaBetween(inicio, fim);
        return vendas.stream()
                .sorted((v1, v2) -> v2.getDataVenda().compareTo(v1.getDataVenda()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<VendaDTO> buscarVendaPorId(Long id) {
        log.debug("Buscando venda por ID: {}", id);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da venda deve ser um número positivo");
        }

        return vendaRepository.findById(id).map(this::convertToDTO);
    }

    public ResumoVendasDTO obterResumoVendas() {
        log.debug("Calculando resumo de todas as vendas");
        List<Venda> vendas = vendaRepository.findAll();
        return calcularResumoVendas(vendas);
    }

    public ResumoVendasDTO obterResumoVendasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        log.debug("Calculando resumo de vendas por período: {} a {}", dataInicio, dataFim);

        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : LocalDateTime.of(1900, 1, 1, 0, 0);
        LocalDateTime fim = dataFim != null ? dataFim.atTime(23, 59, 59) : LocalDateTime.now();

        List<Venda> vendas = vendaRepository.findByDataVendaBetween(inicio, fim);
        return calcularResumoVendas(vendas);
    }

    @Transactional
    public VendaDTO criarVenda(VendaCreateDTO vendaCreateDTO) {
        log.debug("Criando nova venda: {}", vendaCreateDTO.getTipoVenda());

        // Validações básicas
        if (vendaCreateDTO == null) {
            throw new IllegalArgumentException("Dados da venda não podem ser nulos");
        }

        if (vendaCreateDTO.getDataVenda() == null) {
            throw new IllegalArgumentException("Data da venda é obrigatória");
        }

        if (vendaCreateDTO.getTipoVenda() == null) {
            throw new IllegalArgumentException("Tipo da venda é obrigatório");
        }

        // Validações específicas por tipo
        validarDadosPorTipo(vendaCreateDTO);

        Venda venda = new Venda();
        venda.setDataVenda(vendaCreateDTO.getDataVenda());
        venda.setTipoVenda(vendaCreateDTO.getTipoVenda());
        venda.setObservacoes(vendaCreateDTO.getObservacoes());

        // Configurar campos baseados no tipo
        if (vendaCreateDTO.getPosteId() != null) {
            Poste poste = posteRepository.findById(vendaCreateDTO.getPosteId())
                    .orElseThrow(() -> new IllegalArgumentException("Poste não encontrado com ID: " + vendaCreateDTO.getPosteId()));

            if (!poste.getAtivo()) {
                throw new IllegalArgumentException("Poste inativo não pode ser usado em vendas");
            }

            venda.setPoste(poste);
        }

        venda.setQuantidade(vendaCreateDTO.getQuantidade());
        venda.setFreteEletrons(vendaCreateDTO.getFreteEletrons());
        venda.setValorVenda(vendaCreateDTO.getValorVenda());
        venda.setValorExtra(vendaCreateDTO.getValorExtra());

        venda = vendaRepository.save(venda);
        log.info("Venda criada com sucesso. ID: {}", venda.getId());

        return convertToDTO(venda);
    }

    @Transactional
    public VendaDTO atualizarVenda(Long id, VendaDTO vendaDTO) {
        log.debug("Atualizando venda ID: {}", id);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da venda deve ser um número positivo");
        }

        if (vendaDTO == null) {
            throw new IllegalArgumentException("Dados da venda não podem ser nulos");
        }

        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com ID: " + id));

        // Atualizar apenas campos editáveis
        venda.setFreteEletrons(vendaDTO.getFreteEletrons());
        venda.setValorVenda(vendaDTO.getValorVenda());
        venda.setValorExtra(vendaDTO.getValorExtra());
        venda.setObservacoes(vendaDTO.getObservacoes());

        venda = vendaRepository.save(venda);
        log.info("Venda atualizada com sucesso. ID: {}", venda.getId());

        return convertToDTO(venda);
    }

    @Transactional
    public void deletarVenda(Long id) {
        log.debug("Deletando venda ID: {}", id);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da venda deve ser um número positivo");
        }

        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com ID: " + id));

        try {
            vendaRepository.delete(venda);
            log.info("Venda deletada com sucesso. ID: {}", id);
        } catch (Exception e) {
            log.error("Erro ao deletar venda ID: {}. Erro: {}", id, e.getMessage());
            throw new RuntimeException("Erro ao deletar venda: " + e.getMessage(), e);
        }
    }

    private void validarDadosPorTipo(VendaCreateDTO vendaCreateDTO) {
        switch (vendaCreateDTO.getTipoVenda()) {
            case E:
                if (vendaCreateDTO.getValorExtra() == null || vendaCreateDTO.getValorExtra().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Valor extra deve ser maior que zero para vendas tipo E");
                }
                break;

            case V:
                if (vendaCreateDTO.getPosteId() == null) {
                    throw new IllegalArgumentException("Poste é obrigatório para vendas tipo V");
                }
                if (vendaCreateDTO.getValorVenda() == null || vendaCreateDTO.getValorVenda().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Valor de venda deve ser maior que zero para vendas tipo V");
                }
                if (vendaCreateDTO.getQuantidade() == null || vendaCreateDTO.getQuantidade() <= 0) {
                    throw new IllegalArgumentException("Quantidade deve ser maior que zero para vendas tipo V");
                }
                break;

            case L:
                if (vendaCreateDTO.getPosteId() == null) {
                    throw new IllegalArgumentException("Poste de referência é obrigatório para vendas tipo L");
                }
                if (vendaCreateDTO.getQuantidade() == null || vendaCreateDTO.getQuantidade() <= 0) {
                    throw new IllegalArgumentException("Quantidade deve ser maior que zero para vendas tipo L");
                }
                // Frete pode ser zero para vendas tipo L
                break;

            default:
                throw new IllegalArgumentException("Tipo de venda inválido: " + vendaCreateDTO.getTipoVenda());
        }
    }

    private ResumoVendasDTO calcularResumoVendas(List<Venda> vendas) {
        // Separar por tipo
        List<Venda> vendasE = vendas.stream().filter(v -> v.getTipoVenda() == Venda.TipoVenda.E).collect(Collectors.toList());
        List<Venda> vendasV = vendas.stream().filter(v -> v.getTipoVenda() == Venda.TipoVenda.V).collect(Collectors.toList());
        List<Venda> vendasL = vendas.stream().filter(v -> v.getTipoVenda() == Venda.TipoVenda.L).collect(Collectors.toList());

        // Calcular totais
        BigDecimal totalVendaPostes = vendasV.stream()
                .map(v -> {
                    if (v.getPoste() != null && v.getQuantidade() != null) {
                        return v.getPoste().getPreco().multiply(BigDecimal.valueOf(v.getQuantidade()));
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalVendas = vendasV.stream()
                .map(v -> v.getValorVenda() != null ? v.getValorVenda() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFreteEletrons = vendasL.stream()
                .map(v -> v.getFreteEletrons() != null ? v.getFreteEletrons() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalExtras = vendasE.stream()
                .map(v -> v.getValorExtra() != null ? v.getValorExtra() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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
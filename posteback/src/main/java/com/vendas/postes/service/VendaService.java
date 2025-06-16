package com.vendas.postes.service;

import com.vendas.postes.dto.*;
import com.vendas.postes.model.Poste;
import com.vendas.postes.model.Venda;
import com.vendas.postes.repository.PosteRepository;
import com.vendas.postes.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
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
public class VendaService {

    private final VendaRepository vendaRepository;
    private final PosteRepository posteRepository;

    public List<VendaDTO> listarTodasVendas() {
        List<Venda> vendas = vendaRepository.findAllByOrderByDataVendaDesc();
        return vendas.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<VendaDTO> listarVendasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : LocalDateTime.of(1900, 1, 1, 0, 0);
        LocalDateTime fim = dataFim != null ? dataFim.atTime(23, 59, 59) : LocalDateTime.now();

        List<Venda> vendas = vendaRepository.findByDataVendaBetween(inicio, fim);
        return vendas.stream()
                .sorted((v1, v2) -> v2.getDataVenda().compareTo(v1.getDataVenda()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<VendaDTO> buscarVendaPorId(Long id) {
        return vendaRepository.findById(id).map(this::convertToDTO);
    }

    public ResumoVendasDTO obterResumoVendas() {
        List<Venda> vendas = vendaRepository.findAll();
        return calcularResumoVendas(vendas);
    }

    public ResumoVendasDTO obterResumoVendasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : LocalDateTime.of(1900, 1, 1, 0, 0);
        LocalDateTime fim = dataFim != null ? dataFim.atTime(23, 59, 59) : LocalDateTime.now();

        List<Venda> vendas = vendaRepository.findByDataVendaBetween(inicio, fim);
        return calcularResumoVendas(vendas);
    }

    @Transactional
    public VendaDTO criarVenda(VendaCreateDTO vendaCreateDTO) {
        Venda venda = new Venda();
        venda.setDataVenda(vendaCreateDTO.getDataVenda());
        venda.setTipoVenda(vendaCreateDTO.getTipoVenda());
        venda.setObservacoes(vendaCreateDTO.getObservacoes());

        // Configurar campos baseados no tipo
        if (vendaCreateDTO.getPosteId() != null) {
            Poste poste = posteRepository.findById(vendaCreateDTO.getPosteId())
                    .orElseThrow(() -> new RuntimeException("Poste não encontrado"));
            venda.setPoste(poste);
        }

        venda.setQuantidade(vendaCreateDTO.getQuantidade());
        venda.setFreteEletrons(vendaCreateDTO.getFreteEletrons());
        venda.setValorVenda(vendaCreateDTO.getValorVenda());
        venda.setValorExtra(vendaCreateDTO.getValorExtra());

        venda = vendaRepository.save(venda);
        return convertToDTO(venda);
    }

    @Transactional
    public VendaDTO atualizarVenda(Long id, VendaDTO vendaDTO) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        venda.setFreteEletrons(vendaDTO.getFreteEletrons());
        venda.setValorVenda(vendaDTO.getValorVenda());
        venda.setValorExtra(vendaDTO.getValorExtra());
        venda.setObservacoes(vendaDTO.getObservacoes());

        venda = vendaRepository.save(venda);
        return convertToDTO(venda);
    }

    @Transactional
    public void deletarVenda(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));
        vendaRepository.delete(venda);
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
package com.vendas.postes.service;

import com.vendas.postes.dto.*;
import com.vendas.postes.model.Despesa;
import com.vendas.postes.model.ItemVenda;
import com.vendas.postes.model.Poste;
import com.vendas.postes.model.Venda;
import com.vendas.postes.repository.DespesaRepository;
import com.vendas.postes.repository.ItemVendaRepository;
import com.vendas.postes.repository.PosteRepository;
import com.vendas.postes.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final DespesaRepository despesaRepository;
    private final ItemVendaRepository itemVendaRepository;
    private final PosteRepository posteRepository;

    public List<VendaDTO> listarTodasVendas() {
        List<Venda> vendas = vendaRepository.findAllOrderByDataVendaDesc();
        return vendas.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Optional<VendaDTO> buscarVendaPorId(Long id) {
        return vendaRepository.findById(id).map(this::convertToDTO);
    }

    @Transactional
    public VendaDTO criarVenda(VendaCreateDTO vendaCreateDTO) {
        // Buscar o poste
        Poste poste = posteRepository.findById(vendaCreateDTO.getPosteId())
                .orElseThrow(() -> new RuntimeException("Poste não encontrado"));

        // Criar a venda
        Venda venda = new Venda();
        venda.setDataVenda(vendaCreateDTO.getDataVenda());
        venda.setTotalFreteEletrons(vendaCreateDTO.getFreteEletrons());
        venda.setValorTotalInformado(vendaCreateDTO.getValorVenda());
        venda.setObservacoes(vendaCreateDTO.getObservacoes());

        // Salvar a venda primeiro
        venda = vendaRepository.save(venda);

        // Criar o item da venda
        ItemVenda itemVenda = new ItemVenda();
        itemVenda.setVenda(venda);
        itemVenda.setPoste(poste);
        itemVenda.setQuantidade(vendaCreateDTO.getQuantidade());
        itemVenda.setPrecoUnitario(poste.getPreco());

        itemVendaRepository.save(itemVenda);

        return convertToDTO(venda);
    }

    @Transactional
    public VendaDTO atualizarVenda(Long id, VendaDTO vendaDTO) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        venda.setTotalFreteEletrons(vendaDTO.getTotalFreteEletrons());
        venda.setTotalComissao(vendaDTO.getTotalComissao());
        venda.setValorTotalInformado(vendaDTO.getValorTotalInformado());
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

    public ResumoVendasDTO calcularResumoVendas() {
        // Calcular total de vendas dos postes
        BigDecimal totalVendaPostes = itemVendaRepository.calcularTotalVendaPostes();
        if (totalVendaPostes == null) totalVendaPostes = BigDecimal.ZERO;

        // Buscar todas as vendas para calcular outros totais
        List<Venda> vendas = vendaRepository.findAll();

        BigDecimal totalFreteEletrons = vendas.stream()
                .map(v -> v.getTotalFreteEletrons() != null ? v.getTotalFreteEletrons() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalComissao = vendas.stream()
                .map(v -> v.getTotalComissao() != null ? v.getTotalComissao() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalVendas = vendas.stream()
                .map(v -> v.getValorTotalInformado() != null ? v.getValorTotalInformado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular despesas
        BigDecimal despesasFuncionario = despesaRepository.calcularTotalPorTipo(Despesa.TipoDespesa.FUNCIONARIO);
        if (despesasFuncionario == null) despesasFuncionario = BigDecimal.ZERO;

        BigDecimal outrasDespesas = despesaRepository.calcularTotalPorTipo(Despesa.TipoDespesa.OUTRAS);
        if (outrasDespesas == null) outrasDespesas = BigDecimal.ZERO;

        BigDecimal totalDespesas = despesasFuncionario.add(outrasDespesas);

        // Calcular lucro: Total dos postes - Valor total das vendas - Despesas
        BigDecimal lucro = totalVendaPostes.subtract(valorTotalVendas).subtract(totalDespesas);

        // Distribuição de lucro
        BigDecimal parteCicero = lucro.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        BigDecimal parteGuilhermeJefferson = lucro.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP)
                .subtract(despesasFuncionario);
        BigDecimal parteGuilherme = parteGuilhermeJefferson.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        BigDecimal parteJefferson = parteGuilhermeJefferson.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        return new ResumoVendasDTO(
                totalVendaPostes,
                totalFreteEletrons,
                totalComissao,
                valorTotalVendas,
                despesasFuncionario,
                outrasDespesas,
                totalDespesas,
                lucro,
                parteCicero,
                parteGuilhermeJefferson,
                parteGuilherme,
                parteJefferson,
                BigDecimal.ZERO // totalValorExtra mantido para compatibilidade
        );
    }

    private VendaDTO convertToDTO(Venda venda) {
        List<ItemVendaDTO> itensDTO = venda.getItens() != null ?
                venda.getItens().stream().map(this::convertItemToDTO).collect(Collectors.toList()) :
                List.of();

        return new VendaDTO(
                venda.getId(),
                venda.getTotalFreteEletrons(),
                venda.getTotalComissao(),
                venda.getValorTotalInformado(),
                venda.getDataVenda(),
                venda.getObservacoes(),
                itensDTO
        );
    }

    private ItemVendaDTO convertItemToDTO(ItemVenda item) {
        return new ItemVendaDTO(
                item.getId(),
                item.getVenda().getId(),
                item.getPoste().getId(),
                item.getPoste().getCodigo(),
                item.getPoste().getDescricao(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getSubtotal()
        );
    }
}
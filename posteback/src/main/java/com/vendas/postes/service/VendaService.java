package com.vendas.postes.service;

import com.vendas.postes.dto.*;
import com.vendas.postes.model.ItemVenda;
import com.vendas.postes.model.Poste;
import com.vendas.postes.model.Venda;
import com.vendas.postes.repository.ItemVendaRepository;
import com.vendas.postes.repository.PosteRepository;
import com.vendas.postes.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
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
        // Criar a venda base
        Venda venda = new Venda();
        venda.setDataVenda(vendaCreateDTO.getDataVenda());
        venda.setTipoVenda(vendaCreateDTO.getTipoVenda());
        venda.setObservacoes(vendaCreateDTO.getObservacoes());

        // Configurar campos baseados no tipo de venda
        switch (vendaCreateDTO.getTipoVenda()) {
            case E:
                // Tipo E: apenas valor extra
                venda.setValorExtra(vendaCreateDTO.getValorExtra());
                break;

            case V:
                // Tipo V: poste, quantidade, frete, valor de venda
                venda.setTotalFreteEletrons(vendaCreateDTO.getFreteEletrons());
                venda.setValorTotalInformado(vendaCreateDTO.getValorVenda());

                // Criar item da venda
                if (vendaCreateDTO.getPosteId() != null && vendaCreateDTO.getQuantidade() != null) {
                    Poste poste = posteRepository.findById(vendaCreateDTO.getPosteId())
                            .orElseThrow(() -> new RuntimeException("Poste não encontrado"));

                    // Salvar a venda primeiro
                    venda = vendaRepository.save(venda);

                    ItemVenda itemVenda = new ItemVenda();
                    itemVenda.setVenda(venda);
                    itemVenda.setPoste(poste);
                    itemVenda.setQuantidade(vendaCreateDTO.getQuantidade());
                    itemVenda.setPrecoUnitario(poste.getPreco());

                    itemVendaRepository.save(itemVenda);
                    return convertToDTO(venda);
                }
                break;

            case L:
                // Tipo L: poste (sem considerar valor), quantidade, frete, valor de venda
                venda.setTotalFreteEletrons(vendaCreateDTO.getFreteEletrons());
                venda.setValorTotalInformado(vendaCreateDTO.getValorVenda());

                // Criar item da venda (mas não considera o preço do poste para cálculos)
                if (vendaCreateDTO.getPosteId() != null && vendaCreateDTO.getQuantidade() != null) {
                    Poste poste = posteRepository.findById(vendaCreateDTO.getPosteId())
                            .orElseThrow(() -> new RuntimeException("Poste não encontrado"));

                    // Salvar a venda primeiro
                    venda = vendaRepository.save(venda);

                    ItemVenda itemVenda = new ItemVenda();
                    itemVenda.setVenda(venda);
                    itemVenda.setPoste(poste);
                    itemVenda.setQuantidade(vendaCreateDTO.getQuantidade());
                    itemVenda.setPrecoUnitario(BigDecimal.ZERO); // Não considera preço para tipo L

                    itemVendaRepository.save(itemVenda);
                    return convertToDTO(venda);
                }
                break;

            case C:
                // Tipo C: Comissão
                venda.setTotalComissao(vendaCreateDTO.getValorComissao());
                break;

            case F:
                // Tipo F: Frete
                venda.setTotalFreteEletrons(vendaCreateDTO.getFreteEletrons());
                break;
        }

        // Salvar a venda (para tipos que não precisam de itens)
        venda = vendaRepository.save(venda);
        return convertToDTO(venda);
    }

    @Transactional
    public VendaDTO atualizarVenda(Long id, VendaDTO vendaDTO) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        venda.setTotalFreteEletrons(vendaDTO.getTotalFreteEletrons());
        venda.setTotalComissao(vendaDTO.getTotalComissao());
        venda.setValorTotalInformado(vendaDTO.getValorTotalInformado());
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

    // Retorna apenas os dados brutos para o frontend calcular
    public ResumoVendasDTO obterDadosParaCalculos() {
        List<Venda> vendas = vendaRepository.findAll();

        // Contar vendas por tipo
        Long totalVendasE = vendas.stream().filter(v -> v.getTipoVenda() == Venda.TipoVenda.E).count();
        Long totalVendasV = vendas.stream().filter(v -> v.getTipoVenda() == Venda.TipoVenda.V).count();
        Long totalVendasL = vendas.stream().filter(v -> v.getTipoVenda() == Venda.TipoVenda.L).count();
        Long totalVendasC = vendas.stream().filter(v -> v.getTipoVenda() == Venda.TipoVenda.C).count();
        Long totalVendasF = vendas.stream().filter(v -> v.getTipoVenda() == Venda.TipoVenda.F).count();

        // Calcular totais básicos (sem lógica de lucro)
        BigDecimal totalVendaPostes = vendas.stream()
                .filter(v -> v.getTipoVenda() == Venda.TipoVenda.V)
                .map(Venda::calcularTotalItens)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFreteEletrons = vendas.stream()
                .map(v -> v.getTotalFreteEletrons() != null ? v.getTotalFreteEletrons() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalComissao = vendas.stream()
                .map(v -> v.getTotalComissao() != null ? v.getTotalComissao() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalVendas = vendas.stream()
                .filter(v -> v.getTipoVenda() == Venda.TipoVenda.V)
                .map(v -> v.getValorTotalInformado() != null ? v.getValorTotalInformado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalExtras = vendas.stream()
                .filter(v -> v.getTipoVenda() == Venda.TipoVenda.E)
                .map(v -> v.getValorExtra() != null ? v.getValorExtra() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalLivres = vendas.stream()
                .filter(v -> v.getTipoVenda() == Venda.TipoVenda.L)
                .map(v -> v.getValorTotalInformado() != null ? v.getValorTotalInformado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalComissoes = vendas.stream()
                .filter(v -> v.getTipoVenda() == Venda.TipoVenda.C)
                .map(v -> v.getTotalComissao() != null ? v.getTotalComissao() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalFretes = vendas.stream()
                .filter(v -> v.getTipoVenda() == Venda.TipoVenda.F)
                .map(v -> v.getTotalFreteEletrons() != null ? v.getTotalFreteEletrons() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ResumoVendasDTO(
                totalVendaPostes,
                totalFreteEletrons,
                totalComissao,
                valorTotalVendas,
                BigDecimal.ZERO, // despesasFuncionario - será calculado no frontend
                BigDecimal.ZERO, // outrasDespesas - será calculado no frontend
                BigDecimal.ZERO, // totalDespesas - será calculado no frontend
                BigDecimal.ZERO, // lucro - será calculado no frontend
                BigDecimal.ZERO, // parteCicero - será calculado no frontend
                BigDecimal.ZERO, // parteGuilhermeJefferson - será calculado no frontend
                BigDecimal.ZERO, // parteGuilherme - será calculado no frontend
                BigDecimal.ZERO, // parteJefferson - será calculado no frontend
                BigDecimal.ZERO, // totalContribuicoesExtras - será calculado no frontend
                totalVendasE,
                totalVendasV,
                totalVendasL,
                valorTotalExtras,
                valorTotalLivres,
                totalVendasC,
                totalVendasF,
                valorTotalComissoes,
                valorTotalFretes
        );
    }

    private VendaDTO convertToDTO(Venda venda) {
        List<ItemVendaDTO> itensDTO = venda.getItens() != null ?
                venda.getItens().stream().map(this::convertItemToDTO).collect(Collectors.toList()) :
                List.of();

        return new VendaDTO(
                venda.getId(),
                venda.getDataVenda(),
                venda.getTipoVenda(),
                venda.getTotalFreteEletrons(),
                venda.getTotalComissao(),
                venda.getValorTotalInformado(),
                venda.getValorExtra(),
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
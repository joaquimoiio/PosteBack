package com.vendas.postes.service;

import com.vendas.postes.dto.RelatorioPosteDTO;
import com.vendas.postes.model.Venda;
import com.vendas.postes.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final VendaRepository vendaRepository;

    public List<RelatorioPosteDTO> gerarRelatorioVendasPorPoste(LocalDate dataInicio, LocalDate dataFim, String tipoVenda) {
        // Definir período
        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : LocalDateTime.of(1900, 1, 1, 0, 0);
        LocalDateTime fim = dataFim != null ? dataFim.atTime(23, 59, 59) : LocalDateTime.now();

        // Buscar vendas do período
        List<Venda> vendas = vendaRepository.findByDataVendaBetween(inicio, fim);

        // Filtrar por tipo de venda se especificado
        if (tipoVenda != null && !tipoVenda.isEmpty()) {
            Venda.TipoVenda tipo = Venda.TipoVenda.valueOf(tipoVenda);
            vendas = vendas.stream()
                    .filter(v -> v.getTipoVenda() == tipo)
                    .collect(Collectors.toList());
        }

        // Filtrar apenas vendas que têm postes (V e L)
        List<Venda> vendasComPostes = vendas.stream()
                .filter(v -> (v.getTipoVenda() == Venda.TipoVenda.V || v.getTipoVenda() == Venda.TipoVenda.L))
                .filter(v -> v.getPoste() != null)
                .collect(Collectors.toList());

        // Agrupar por poste
        Map<Long, RelatorioPosteDTO> relatorioPorPoste = new HashMap<>();

        for (Venda venda : vendasComPostes) {
            Long posteId = venda.getPoste().getId();
            RelatorioPosteDTO item = relatorioPorPoste.computeIfAbsent(posteId, k -> {
                RelatorioPosteDTO dto = new RelatorioPosteDTO();
                dto.setPosteId(posteId);
                dto.setCodigoPoste(venda.getPoste().getCodigo());
                dto.setDescricaoPoste(venda.getPoste().getDescricao());
                dto.setPrecoUnitario(venda.getPoste().getPreco());
                dto.setQuantidadeVendida(0L);
                dto.setValorTotal(BigDecimal.ZERO);
                dto.setNumeroVendas(0L);
                return dto;
            });

            // Somar quantidades
            Integer quantidade = venda.getQuantidade() != null ? venda.getQuantidade() : 1;
            item.setQuantidadeVendida(item.getQuantidadeVendida() + quantidade);
            item.setNumeroVendas(item.getNumeroVendas() + 1);

            // Somar valores (apenas para vendas tipo V)
            if (venda.getTipoVenda() == Venda.TipoVenda.V && venda.getValorVenda() != null) {
                item.setValorTotal(item.getValorTotal().add(venda.getValorVenda()));
            }
        }

        // Converter para lista e ordenar por quantidade vendida
        List<RelatorioPosteDTO> relatorio = new ArrayList<>(relatorioPorPoste.values());
        relatorio.sort((a, b) -> b.getQuantidadeVendida().compareTo(a.getQuantidadeVendida()));

        // Calcular percentuais e ranking
        Long quantidadeTotal = relatorio.stream()
                .mapToLong(RelatorioPosteDTO::getQuantidadeVendida)
                .sum();

        for (int i = 0; i < relatorio.size(); i++) {
            RelatorioPosteDTO item = relatorio.get(i);
            item.setRanking(i + 1);

            if (quantidadeTotal > 0) {
                double percentual = (item.getQuantidadeVendida().doubleValue() / quantidadeTotal.doubleValue()) * 100;
                item.setPercentualDoTotal(Math.round(percentual * 10.0) / 10.0); // Arredondar para 1 casa decimal
            } else {
                item.setPercentualDoTotal(0.0);
            }
        }

        return relatorio;
    }
}
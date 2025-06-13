package com.vendas.postes.service;

import com.vendas.postes.dto.ResumoVendasDTO;
import com.vendas.postes.model.Despesa;
import com.vendas.postes.model.Venda;
import com.vendas.postes.repository.DespesaRepository;
import com.vendas.postes.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final DespesaRepository despesaRepository;

    public ResumoVendasDTO calcularResumoVendas() {
        // Buscar todas as vendas
        List<Venda> vendas = vendaRepository.findAll();

        // Calcular totais das vendas
        BigDecimal totalVendaPostes = vendas.stream()
                .map(v -> v.getValorLoja().multiply(BigDecimal.valueOf(v.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFreteEletrons = vendas.stream()
                .map(v -> v.getFreteEletrons() != null ? v.getFreteEletrons() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalValorVenda = vendas.stream()
                .map(v -> v.getValorVenda() != null ? v.getValorVenda() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalValorExtra = vendas.stream()
                .map(v -> v.getValorExtra() != null ? v.getValorExtra() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular despesas
        BigDecimal despesasFuncionario = despesaRepository.calcularTotalPorTipo(Despesa.TipoDespesa.FUNCIONARIO);
        if (despesasFuncionario == null) despesasFuncionario = BigDecimal.ZERO;

        BigDecimal outrasDespesas = despesaRepository.calcularTotalPorTipo(Despesa.TipoDespesa.OUTRAS);
        if (outrasDespesas == null) outrasDespesas = BigDecimal.ZERO;

        BigDecimal totalDespesas = despesasFuncionario.add(outrasDespesas);

        // Calcular lucro: Total_Venda_Postes - Total_Valor_Venda - Total_Despesas - Total_Valor_Extra
        BigDecimal lucro = totalVendaPostes.subtract(totalValorVenda).subtract(totalDespesas).subtract(totalValorExtra);

        // Distribuição de lucro
        // CÍCERO recebe 50% do lucro
        BigDecimal parteCicero = lucro.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        // GUILHERME/JEFFERSON: 50% do lucro - despesas funcionário, dividido por 2
        BigDecimal parteGuilhermeJefferson = lucro.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP)
                .subtract(despesasFuncionario);

        BigDecimal parteGuilherme = parteGuilhermeJefferson.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        BigDecimal parteJefferson = parteGuilhermeJefferson.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        return new ResumoVendasDTO(
                totalVendaPostes,
                totalFreteEletrons,
                BigDecimal.ZERO, // totalComissao - removido
                totalValorVenda,
                despesasFuncionario,
                outrasDespesas,
                totalDespesas,
                lucro,
                parteCicero,
                parteGuilhermeJefferson,
                parteGuilherme,
                parteJefferson,
                totalValorExtra
        );
    }
}
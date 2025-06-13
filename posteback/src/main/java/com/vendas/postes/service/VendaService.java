package com.vendas.postes.service;

import com.vendas.postes.dto.ResumoVendasDTO;
import com.vendas.postes.model.Despesa;
import com.vendas.postes.repository.DespesaRepository;
import com.vendas.postes.repository.ItemVendaRepository;
import com.vendas.postes.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ItemVendaRepository itemVendaRepository;
    private final DespesaRepository despesaRepository;

    public ResumoVendasDTO calcularResumoVendas() {
        // Buscar totais
        BigDecimal totalVendaPostes = itemVendaRepository.calcularTotalVendaPostes();
        if (totalVendaPostes == null) totalVendaPostes = BigDecimal.ZERO;

        // Somar valores informados das vendas
        BigDecimal totalFreteEletrons = vendaRepository.findAll().stream()
                .map(v -> v.getTotalFreteEletrons() != null ? v.getTotalFreteEletrons() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalComissao = vendaRepository.findAll().stream()
                .map(v -> v.getTotalComissao() != null ? v.getTotalComissao() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalInformado = vendaRepository.findAll().stream()
                .map(v -> v.getValorTotalInformado() != null ? v.getValorTotalInformado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular despesas
        BigDecimal despesasFuncionario = despesaRepository.calcularTotalPorTipo(Despesa.TipoDespesa.FUNCIONARIO);
        if (despesasFuncionario == null) despesasFuncionario = BigDecimal.ZERO;

        BigDecimal outrasDespesas = despesaRepository.calcularTotalPorTipo(Despesa.TipoDespesa.OUTRAS);
        if (outrasDespesas == null) outrasDespesas = BigDecimal.ZERO;

        BigDecimal totalDespesas = despesasFuncionario.add(outrasDespesas);

        // Calcular lucro: Total_Venda_Poste - Valor_Total_Informado - Total_Despesas
        BigDecimal lucro = totalVendaPostes.subtract(valorTotalInformado).subtract(totalDespesas);

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
                totalComissao,
                valorTotalInformado,
                despesasFuncionario,
                outrasDespesas,
                totalDespesas,
                lucro,
                parteCicero,
                parteGuilhermeJefferson,
                parteGuilherme,
                parteJefferson
        );
    }
}
package com.vendas.postes.service;

import com.vendas.postes.config.TenantContext;
import com.vendas.postes.dto.ResumoVendasDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Serviço para lógica específica de cada tenant (caminhão)
 */
@Service
public class TenantService {

    /**
     * Calcula o lucro considerando a divisão específica do caminhão atual
     */
    public LucroCalculation calcularLucrosPorTenant(ResumoVendasDTO resumoVendas, BigDecimal despesasFuncionario, BigDecimal outrasDespesas) {
        TenantContext.TenantType tenant = TenantContext.getCurrentTenant();

        if (tenant == null) {
            tenant = TenantContext.TenantType.VERMELHO; // Default
        }

        switch (tenant) {
            case BRANCO:
                return calcularLucrosCaminhaoBranco(resumoVendas, despesasFuncionario, outrasDespesas);
            case VERMELHO:
            default:
                return calcularLucrosCaminhaoVermelho(resumoVendas, despesasFuncionario, outrasDespesas);
        }
    }

    /**
     * Cálculo específico para Caminhão Vermelho (3 sócios: 50% + 25% + 25%)
     */
    private LucroCalculation calcularLucrosCaminhaoVermelho(ResumoVendasDTO resumoVendas, BigDecimal despesasFuncionario, BigDecimal outrasDespesas) {
        BigDecimal totalVendaPostes = resumoVendas.getTotalVendaPostes() != null ? resumoVendas.getTotalVendaPostes() : BigDecimal.ZERO;
        BigDecimal valorTotalVendas = resumoVendas.getValorTotalVendas() != null ? resumoVendas.getValorTotalVendas() : BigDecimal.ZERO;
        BigDecimal totalFreteEletrons = resumoVendas.getTotalFreteEletrons() != null ? resumoVendas.getTotalFreteEletrons() : BigDecimal.ZERO;
        BigDecimal valorTotalExtras = resumoVendas.getValorTotalExtras() != null ? resumoVendas.getValorTotalExtras() : BigDecimal.ZERO;

        // Lucro total
        BigDecimal lucroTotal = valorTotalVendas.add(valorTotalExtras).add(totalFreteEletrons)
                .subtract(outrasDespesas).subtract(totalVendaPostes);

        // Divisão: 50% Cícero, 50% (Gilberto + Jefferson)
        BigDecimal metadeCicero = lucroTotal.divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal metadeGilbertoJefferson = lucroTotal.divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_HALF_UP);

        // Descontar despesas de funcionário apenas da parte Gilberto+Jefferson
        BigDecimal gilbertoJeffersonLiquido = metadeGilbertoJefferson.subtract(despesasFuncionario);

        // Dividir entre Gilberto e Jefferson
        BigDecimal parteGilberto = gilbertoJeffersonLiquido.divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal parteJefferson = gilbertoJeffersonLiquido.divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_HALF_UP);

        return LucroCalculation.builder()
                .totalVendaPostes(totalVendaPostes)
                .valorTotalVendas(valorTotalVendas)
                .valorTotalExtras(valorTotalExtras)
                .totalFreteEletrons(totalFreteEletrons)
                .despesasFuncionario(despesasFuncionario)
                .outrasDespesas(outrasDespesas)
                .lucroTotal(lucroTotal)
                .parteCicero(metadeCicero)
                .parteGilberto(parteGilberto)
                .parteJefferson(parteJefferson)
                .tenantType(TenantContext.TenantType.VERMELHO)
                .build();
    }

    /**
     * Cálculo específico para Caminhão Branco (2 sócios: 50% + 50%)
     */
    private LucroCalculation calcularLucrosCaminhaoBranco(ResumoVendasDTO resumoVendas, BigDecimal despesasFuncionario, BigDecimal outrasDespesas) {
        BigDecimal totalVendaPostes = resumoVendas.getTotalVendaPostes() != null ? resumoVendas.getTotalVendaPostes() : BigDecimal.ZERO;
        BigDecimal valorTotalVendas = resumoVendas.getValorTotalVendas() != null ? resumoVendas.getValorTotalVendas() : BigDecimal.ZERO;
        BigDecimal totalFreteEletrons = resumoVendas.getTotalFreteEletrons() != null ? resumoVendas.getTotalFreteEletrons() : BigDecimal.ZERO;
        BigDecimal valorTotalExtras = resumoVendas.getValorTotalExtras() != null ? resumoVendas.getValorTotalExtras() : BigDecimal.ZERO;

        // Lucro total
        BigDecimal lucroTotal = valorTotalVendas.add(valorTotalExtras).add(totalFreteEletrons)
                .subtract(outrasDespesas).subtract(totalVendaPostes);

        // Divisão: 50% Cícero, 50% Jefferson
        BigDecimal metadeCicero = lucroTotal.divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal metadeJefferson = lucroTotal.divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_HALF_UP);

        // Descontar despesas de funcionário apenas da parte Jefferson
        BigDecimal parteJeffersonLiquida = metadeJefferson.subtract(despesasFuncionario);

        return LucroCalculation.builder()
                .totalVendaPostes(totalVendaPostes)
                .valorTotalVendas(valorTotalVendas)
                .valorTotalExtras(valorTotalExtras)
                .totalFreteEletrons(totalFreteEletrons)
                .despesasFuncionario(despesasFuncionario)
                .outrasDespesas(outrasDespesas)
                .lucroTotal(lucroTotal)
                .parteCicero(metadeCicero)
                .parteGilberto(BigDecimal.ZERO) // Não há Gilberto no caminhão branco
                .parteJefferson(parteJeffersonLiquida)
                .tenantType(TenantContext.TenantType.BRANCO)
                .build();
    }

    /**
     * Classe para retornar resultado dos cálculos
     */
    public static class LucroCalculation {
        private BigDecimal totalVendaPostes;
        private BigDecimal valorTotalVendas;
        private BigDecimal valorTotalExtras;
        private BigDecimal totalFreteEletrons;
        private BigDecimal despesasFuncionario;
        private BigDecimal outrasDespesas;
        private BigDecimal lucroTotal;
        private BigDecimal parteCicero;
        private BigDecimal parteGilberto;
        private BigDecimal parteJefferson;
        private TenantContext.TenantType tenantType;

        // Construtor privado
        private LucroCalculation() {}

        // Builder pattern
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private LucroCalculation calculation = new LucroCalculation();

            public Builder totalVendaPostes(BigDecimal value) {
                calculation.totalVendaPostes = value;
                return this;
            }

            public Builder valorTotalVendas(BigDecimal value) {
                calculation.valorTotalVendas = value;
                return this;
            }

            public Builder valorTotalExtras(BigDecimal value) {
                calculation.valorTotalExtras = value;
                return this;
            }

            public Builder totalFreteEletrons(BigDecimal value) {
                calculation.totalFreteEletrons = value;
                return this;
            }

            public Builder despesasFuncionario(BigDecimal value) {
                calculation.despesasFuncionario = value;
                return this;
            }

            public Builder outrasDespesas(BigDecimal value) {
                calculation.outrasDespesas = value;
                return this;
            }

            public Builder lucroTotal(BigDecimal value) {
                calculation.lucroTotal = value;
                return this;
            }

            public Builder parteCicero(BigDecimal value) {
                calculation.parteCicero = value;
                return this;
            }

            public Builder parteGilberto(BigDecimal value) {
                calculation.parteGilberto = value;
                return this;
            }

            public Builder parteJefferson(BigDecimal value) {
                calculation.parteJefferson = value;
                return this;
            }

            public Builder tenantType(TenantContext.TenantType value) {
                calculation.tenantType = value;
                return this;
            }

            public LucroCalculation build() {
                return calculation;
            }
        }

        // Getters
        public BigDecimal getTotalVendaPostes() { return totalVendaPostes; }
        public BigDecimal getValorTotalVendas() { return valorTotalVendas; }
        public BigDecimal getValorTotalExtras() { return valorTotalExtras; }
        public BigDecimal getTotalFreteEletrons() { return totalFreteEletrons; }
        public BigDecimal getDespesasFuncionario() { return despesasFuncionario; }
        public BigDecimal getOutrasDespesas() { return outrasDespesas; }
        public BigDecimal getLucroTotal() { return lucroTotal; }
        public BigDecimal getParteCicero() { return parteCicero; }
        public BigDecimal getParteGilberto() { return parteGilberto; }
        public BigDecimal getParteJefferson() { return parteJefferson; }
        public TenantContext.TenantType getTenantType() { return tenantType; }

        public BigDecimal getCustoEletronsL() {
            return totalVendaPostes.subtract(totalFreteEletrons);
        }
    }
}
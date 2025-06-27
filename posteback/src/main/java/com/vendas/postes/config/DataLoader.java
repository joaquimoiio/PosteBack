package com.vendas.postes.config;

import com.vendas.postes.model.Poste;
import com.vendas.postes.repository.PosteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * DataLoader com debug melhorado para verificar carregamento por tenant
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final PosteRepository posteRepository;

    @Override
    public void run(String... args) throws Exception {
        try {
            log.info("🚀 Iniciando verificação de dados...");

            long totalPostes = posteRepository.count();
            long postesVermelho = posteRepository.findByTenantId("vermelho").size();
            long postesBranco = posteRepository.findByTenantId("branco").size();

            log.info("📊 Status atual do banco:");
            log.info("   Total de postes: {}", totalPostes);
            log.info("   Postes VERMELHO: {}", postesVermelho);
            log.info("   Postes BRANCO: {}", postesBranco);

            if (totalPostes == 0) {
                log.info("🔄 Banco vazio - Carregando dados iniciais...");
                carregarPostesPorTenant();

                // Verificar novamente após carregamento
                totalPostes = posteRepository.count();
                postesVermelho = posteRepository.findByTenantId("vermelho").size();
                postesBranco = posteRepository.findByTenantId("branco").size();

                log.info("✅ Dados carregados com sucesso!");
                log.info("📊 Status final:");
                log.info("   Total de postes: {}", totalPostes);
                log.info("   Postes VERMELHO: {}", postesVermelho);
                log.info("   Postes BRANCO: {}", postesBranco);

            } else {
                log.info("ℹ️ Dados já existem no banco.");

                // Verificar se há problemas de distribuição
                if (postesVermelho == 0) {
                    log.warn("⚠️ PROBLEMA: Nenhum poste encontrado para tenant VERMELHO!");
                }
                if (postesBranco == 0) {
                    log.warn("⚠️ PROBLEMA: Nenhum poste encontrado para tenant BRANCO!");
                    log.info("🔧 Tentando adicionar postes para tenant BRANCO...");
                    adicionarPostesBranco();
                    postesBranco = posteRepository.findByTenantId("branco").size();
                    log.info("✅ Postes BRANCO após correção: {}", postesBranco);
                }
            }

            // Log detalhado dos primeiros postes de cada tenant para debug
            List<Poste> amostrasVermelho = posteRepository.findByTenantId("vermelho");
            List<Poste> amostrasBranco = posteRepository.findByTenantId("branco");

            if (!amostrasVermelho.isEmpty()) {
                log.debug("🔴 Exemplo poste VERMELHO: {} - {}",
                        amostrasVermelho.get(0).getCodigo(),
                        amostrasVermelho.get(0).getDescricao());
            }

            if (!amostrasBranco.isEmpty()) {
                log.debug("⚪ Exemplo poste BRANCO: {} - {}",
                        amostrasBranco.get(0).getCodigo(),
                        amostrasBranco.get(0).getDescricao());
            }

        } catch (Exception e) {
            log.error("❌ Erro crítico ao carregar dados iniciais: ", e);
            throw e;
        }
    }

    private void carregarPostesPorTenant() {
        // Postes do Caminhão Vermelho
        List<Poste> postesVermelho = Arrays.asList(
                createPoste("4199", "Kit Poste 7m 1 Cx Mono Saída Aérea - Vermelho", new BigDecimal("865.00"), "vermelho"),
                createPoste("91450", "Kit Poste 7m 1 Cx Mono Saída Aérea (CERBRANORTE) e Segero - Vermelho", new BigDecimal("870.00"), "vermelho"),
                createPoste("4268", "Kit Poste 7m 1 Cx Mono Saída Subterrânea - Vermelho", new BigDecimal("830.00"), "vermelho"),
                createPoste("4270", "Kit Poste 7m 2 Cx Mono 2 Saídas Subterrâneas - Vermelho", new BigDecimal("1140.00"), "vermelho"),
                createPoste("4269", "Kit Poste 7m 2 Cx Mono 1 Saída Aérea + 1 Saída Subterrânea - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("4200", "Kit Poste 7m 2 Cx Mono 2 Saídas Aéreas - Vermelho", new BigDecimal("1190.00"), "vermelho"),
                createPoste("4392", "Kit Poste 7m 3 Cx Mono 3 Saídas Subterrâneas - Vermelho", new BigDecimal("1390.00"), "vermelho"),
                createPoste("4393", "Kit Poste 7m 3 Cx Mono 1 Saída Aérea + 2 Saídas Subterrâneas - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("4394", "Kit Poste 7m 3 Cx Mono 2 Saídas Aéreas + 1 Saída Subterrânea - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("4512", "Kit Poste 7m 3 Cx Mono 3 Saídas Aéreas - Vermelho", new BigDecimal("1510.00"), "vermelho"),
                createPoste("4248", "Kit Poste 7m 1 Cx Bifásica Saída Aérea - Vermelho", new BigDecimal("1120.00"), "vermelho"),
                createPoste("4248C", "Kit Poste 7m 1 Cx Bifásica Saída Aérea (Cerpalo) - Vermelho", new BigDecimal("1130.00"), "vermelho"),
                createPoste("4271", "Kit Poste 7m 1 Cx Bifásica Saída Subterrânea - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("4198", "Kit Poste 7m 1 Cx Trifásica Saída Aérea - Vermelho", new BigDecimal("1210.00"), "vermelho"),
                createPoste("4272", "Kit Poste 7m 1 Cx Trifásica Saída Subterrânea - Vermelho", new BigDecimal("1150.00"), "vermelho"),
                createPoste("91451", "Kit Poste 7m 1 Cx Trifásica Saída Aérea (CERBRANORTE) ,mesmo do Cerpalo e Segero - Vermelho", new BigDecimal("1230.00"), "vermelho"),
                createPoste("4273", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Aérea - Vermelho", new BigDecimal("1430.00"), "vermelho"),
                createPoste("4274", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Bifásica Aérea Saída Mono Subterrânea - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("4261", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Bifásica Subterrânea Saída Mono Aérea - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("4275", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica 2 Saídas Subterrâneas - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("4738", "Kit Poste 7m 1 Cx Trifásica - Padrão Mono ( Kit Post Polifásico) - Vermelho", new BigDecimal("990.00"), "vermelho"),
                createPoste("4201", "Kit Poste 8m 1 Cx Mono Saída Aérea - Vermelho", new BigDecimal("1080.00"), "vermelho"),
                createPoste("4276", "Kit Poste 8m 1 Cx Mono Saída Subterrânea - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("4203", "Kit Poste 8m 2 Cx Mono 2 Saídas Aéreas - Vermelho", new BigDecimal("1380.00"), "vermelho"),
                createPoste("0000", "Kit Poste 8m 2 Cx Mono 1 Saída Aérea 1 Saída Subterrânea - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("0000C", "Kit Poste 8m 2 Cx Mono 2 Saídas Subterrâneas - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("0000D", "Kit Poste 8m 3 Cx Mono 2 Saídas Aéreas 1 Saída Subterrânea - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("0000E", "Kit Poste 8m 3 Cx Mono 1 Saída Aérea 2 Saídas Subterrâneas - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("0000F", "Kit Poste 8m 3 Cx Mono 3 Saídas Aéreas - Vermelho", new BigDecimal("1720.00"), "vermelho"),
                createPoste("4243", "Kit Poste 8m 3 Cx Mono 3 Saídas Subterrâneas - Vermelho", new BigDecimal("1660.00"), "vermelho"),
                createPoste("4285", "Kit Poste 8m 1 Cx Mono + Bifásica Saída Aérea (Não Tem) - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("4249", "Kit Poste 8 m 1 Cx Bifásica Saída Aérea - Vermelho", new BigDecimal("1285.00"), "vermelho"),
                createPoste("91511", "Kit Poste 8 m 1 Cx Bifásica Saída Aérea CB 16mm Disj 70 - Vermelho", new BigDecimal("1560.00"), "vermelho"),
                createPoste("4277", "Kit Poste 8m 1 Cx Bifásica Saída Subterrânea - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("4202", "Kit Poste 8m 1 Cx Trifásica Saída Aérea - Vermelho", new BigDecimal("1395.00"), "vermelho"),
                createPoste("4267", "Kit Poste 8m 1 Cx Trifásica Saída Subterrânea - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("91006", "Kit Poste 8m 1 Cx Trifásica Saída Aéra Cabo 16mm - Vermelho", new BigDecimal("1905.00"), "vermelho"),
                createPoste("91326", "Kit Poste 8m 1 Cx Trifásico S/A CB 25mm Disj 100A - Vermelho", new BigDecimal("2180.00"), "vermelho"),
                createPoste("91371", "Kit Poste 8m 1 Cx Trifásica - Padrão Mono ( Kit Post Polifásico) - Vermelho", new BigDecimal("1150.00"), "vermelho"),
                createPoste("90394", "Kit Poste 8m 1 Cx Trifásica (Cerbranorte) - Vermelho", new BigDecimal("1415.00"), "vermelho"),
                createPoste("91598", "Kit Poste 8m 1 Cx Trifásica S/A C/8 Pino Retrátil Derivação 10mm - Vermelho", new BigDecimal("1665.00"), "vermelho"),
                createPoste("90259", "Kit Poste 7m 1 Cx Mono Saída Aérea (Cerpalo) - Vermelho", new BigDecimal("850.00"), "vermelho"),
                createPoste("1309", "Poste DT 7/100 - Vermelho", new BigDecimal("350.00"), "vermelho"),
                createPoste("4462", "Kit Poste 7m Trifásica Saida Sub Cabo 16 Disjuntor 70 - Vermelho", new BigDecimal("1580.00"), "vermelho"),
                createPoste("4391", "Kit poste trifasico 7m saida aerea cabo 25mm - disj 70 - Vermelho", new BigDecimal("1900.00"), "vermelho"),
                createPoste("4517", "Kit poste trifásico 7m saída sub cabo 25mm - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("90591", "Kit Poste 7m Bifásico Saída Sub Cabo 16mm - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("91372", "Kit Poste 7m Bifásico S/A Cabo 16mm - Vermelho", new BigDecimal("1400.00"), "vermelho"),
                createPoste("4395", "Kit Poste 7m Trifásico Saída Aérea Cabo 16mm - Vermelho", new BigDecimal("1640.00"), "vermelho"),
                createPoste("4395C", "Kit Poste 7m Trifásico Saída Aérea Cabo 16mm DJ 70AP - Vermelho", new BigDecimal("1670.00"), "vermelho"),
                createPoste("91270", "kit poste trifasico 7m saida aerea cabo 35mm - disj 100 - Vermelho", new BigDecimal("2200.00"), "vermelho"),
                createPoste("90882", "Kit Poste 3 CX 7m Saída Aérea Cabo 16mm - Vermelho", new BigDecimal("2078.00"), "vermelho"),
                createPoste("90881", "Kit Poste Bi+Mono 7m Saída Aérea Cabo 16 mm - Vermelho", new BigDecimal("1920.00"), "vermelho"),
                createPoste("3588", "Poste DT 8/300 - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("1310", "Poste DT8/100 - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("4122", "Poste DT 8/200 - Vermelho", new BigDecimal("0"), "vermelho"),
                createPoste("91132", "Mureta Trifásica CB 16mm disjuntor 70A - Vermelho", new BigDecimal("820.00"), "vermelho"),
                createPoste("389", "Mureta Água (Hidrômetro) - Vermelho", new BigDecimal("200.00"), "vermelho"),
                createPoste("91241", "Mureta Mono - Vermelho", new BigDecimal("600.00"), "vermelho"),
                createPoste("91242", "Mureta Trifásica - Vermelho", new BigDecimal("710.00"), "vermelho"),

        );

        // Salvar postes VERMELHO
        log.info("💾 Salvando {} postes para tenant VERMELHO...", postesVermelho.size());
        List<Poste> savedVermelho = posteRepository.saveAll(postesVermelho);
        log.info("✅ Salvos {} postes VERMELHO", savedVermelho.size());

        // Carregar postes BRANCO
        adicionarPostesBranco();
    }

    private void adicionarPostesBranco() {
        // Postes do Caminhão Branco
        List<Poste> postesBranco = Arrays.asList(
                createPoste("4199-B", "Kit Poste 7m 1 Cx Mono Saída Aérea - Branco", new BigDecimal("865.00"), "branco"),
                createPoste("91450-B", "Kit Poste 7m 1 Cx Mono Saída Aérea (CERBRANORTE) e Segero - Branco", new BigDecimal("870.00"), "branco"),
                createPoste("4268-B", "Kit Poste 7m 1 Cx Mono Saída Subterrânea - Branco", new BigDecimal("830.00"), "branco"),
                createPoste("4270-B", "Kit Poste 7m 2 Cx Mono 2 Saídas Subterrâneas - Branco", new BigDecimal("1140.00"), "branco"),
                createPoste("4269-B", "Kit Poste 7m 2 Cx Mono 1 Saída Aérea + 1 Saída Subterrânea - Branco", new BigDecimal("0"), "branco"),
                createPoste("4200-B", "Kit Poste 7m 2 Cx Mono 2 Saídas Aéreas - Branco", new BigDecimal("1190.00"), "branco"),
                createPoste("4392-B", "Kit Poste 7m 3 Cx Mono 3 Saídas Subterrâneas - Branco", new BigDecimal("1390.00"), "branco"),
                createPoste("4393-B", "Kit Poste 7m 3 Cx Mono 1 Saída Aérea + 2 Saídas Subterrâneas - Branco", new BigDecimal("0"), "branco"),
                createPoste("4394-B", "Kit Poste 7m 3 Cx Mono 2 Saídas Aéreas + 1 Saída Subterrânea - Branco", new BigDecimal("0"), "branco"),
                createPoste("4512-B", "Kit Poste 7m 3 Cx Mono 3 Saídas Aéreas - Branco", new BigDecimal("1510.00"), "branco"),
                createPoste("4248-B", "Kit Poste 7m 1 Cx Bifásica Saída Aérea - Branco", new BigDecimal("1120.00"), "branco"),
                createPoste("4248-C", "Kit Poste 7m 1 Cx Bifásica Saída Aérea (Cerpalo) - Branco", new BigDecimal("1130.00"), "branco"),
                createPoste("4271-B", "Kit Poste 7m 1 Cx Bifásica Saída Subterrânea - Branco", new BigDecimal("0"), "branco"),
                createPoste("4198-B", "Kit Poste 7m 1 Cx Trifásica Saída Aérea - Branco", new BigDecimal("1210.00"), "branco"),
                createPoste("4272-B", "Kit Poste 7m 1 Cx Trifásica Saída Subterrânea - Branco", new BigDecimal("1150.00"), "branco"),
                createPoste("91451-B", "Kit Poste 7m 1 Cx Trifásica Saída Aérea (CERBRANORTE) ,mesmo do Cerpalo e Segero - Branco", new BigDecimal("1230.00"), "branco"),
                createPoste("4273-B", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Aérea - Branco", new BigDecimal("1430.00"), "branco"),
                createPoste("4274-B", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Bifásica Aérea Saída Mono Subterrânea - Branco", new BigDecimal("0"), "branco"),
                createPoste("4261-B", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Bifásica Subterrânea Saída Mono Aérea - Branco", new BigDecimal("0"), "branco"),
                createPoste("4275-B", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica 2 Saídas Subterrâneas - Branco", new BigDecimal("0"), "branco"),
                createPoste("4738-B", "Kit Poste 7m 1 Cx Trifásica - Padrão Mono ( Kit Post Polifásico) - Branco", new BigDecimal("990.00"), "branco"),
                createPoste("4201-B", "Kit Poste 8m 1 Cx Mono Saída Aérea - Branco", new BigDecimal("1080.00"), "branco"),
                createPoste("4276-B", "Kit Poste 8m 1 Cx Mono Saída Subterrânea - Branco", new BigDecimal("0"), "branco"),
                createPoste("4203-B", "Kit Poste 8m 2 Cx Mono 2 Saídas Aéreas - Branco", new BigDecimal("1380.00"), "branco"),
                createPoste("0000-B", "Kit Poste 8m 2 Cx Mono 1 Saída Aérea 1 Saída Subterrânea - Branco", new BigDecimal("0"), "branco"),
                createPoste("0000-C", "Kit Poste 8m 2 Cx Mono 2 Saídas Subterrâneas - Branco", new BigDecimal("0"), "branco"),
                createPoste("0000-D", "Kit Poste 8m 3 Cx Mono 2 Saídas Aéreas 1 Saída Subterrânea - Branco", new BigDecimal("0"), "branco"),
                createPoste("0000-E", "Kit Poste 8m 3 Cx Mono 1 Saída Aérea 2 Saídas Subterrâneas - Branco", new BigDecimal("0"), "branco"),
                createPoste("0000-F", "Kit Poste 8m 3 Cx Mono 3 Saídas Aéreas - Branco", new BigDecimal("1720.00"), "branco"),
                createPoste("4243-B", "Kit Poste 8m 3 Cx Mono 3 Saídas Subterrâneas - Branco", new BigDecimal("1660.00"), "branco"),
                createPoste("4285-B", "Kit Poste 8m 1 Cx Mono + Bifásica Saída Aérea (Não Tem) - Branco", new BigDecimal("0"), "branco"),
                createPoste("4249-B", "Kit Poste 8 m 1 Cx Bifásica Saída Aérea - Branco", new BigDecimal("1285.00"), "branco"),
                createPoste("91511-B", "Kit Poste 8 m 1 Cx Bifásica Saída Aérea CB 16mm Disj 70 - Branco", new BigDecimal("1560.00"), "branco"),
                createPoste("4277-B", "Kit Poste 8m 1 Cx Bifásica Saída Subterrânea - Branco", new BigDecimal("0"), "branco"),
                createPoste("4202-B", "Kit Poste 8m 1 Cx Trifásica Saída Aérea - Branco", new BigDecimal("1395.00"), "branco"),
                createPoste("4267-B", "Kit Poste 8m 1 Cx Trifásica Saída Subterrânea - Branco", new BigDecimal("0"), "branco"),
                createPoste("91006-B", "Kit Poste 8m 1 Cx Trifásica Saída Aéra Cabo 16mm - Branco", new BigDecimal("1905.00"), "branco"),
                createPoste("91326-B", "Kit Poste 8m 1 Cx Trifásico S/A CB 25mm Disj 100A - Branco", new BigDecimal("2180.00"), "branco"),
                createPoste("91371-B", "Kit Poste 8m 1 Cx Trifásica - Padrão Mono ( Kit Post Polifásico) - Branco", new BigDecimal("1150.00"), "branco"),
                createPoste("90394-B", "Kit Poste 8m 1 Cx Trifásica (Cerbranorte) - Branco", new BigDecimal("1415.00"), "branco"),
                createPoste("91598-B", "Kit Poste 8m 1 Cx Trifásica S/A C/8 Pino Retrátil Derivação 10mm - Branco", new BigDecimal("1665.00"), "branco"),
                createPoste("90259-B", "Kit Poste 7m 1 Cx Mono Saída Aérea (Cerpalo) - Branco", new BigDecimal("850.00"), "branco"),
                createPoste("1309-B", "Poste DT 7/100 - Branco", new BigDecimal("350.00"), "branco"),
                createPoste("4462-B", "Kit Poste 7m Trifásica Saida Sub Cabo 16 Disjuntor 70 - Branco", new BigDecimal("1580.00"), "branco"),
                createPoste("4391-B", "Kit poste trifasico 7m saida aerea cabo 25mm - disj 70 - Branco", new BigDecimal("1900.00"), "branco"),
                createPoste("4517-B", "Kit poste trifásico 7m saída sub cabo 25mm - Branco", new BigDecimal("0"), "branco"),
                createPoste("90591-B", "Kit Poste 7m Bifásico Saída Sub Cabo 16mm - Branco", new BigDecimal("0"), "branco"),
                createPoste("91372-B", "Kit Poste 7m Bifásico S/A Cabo 16mm - Branco", new BigDecimal("1400.00"), "branco"),
                createPoste("4395-B", "Kit Poste 7m Trifásico Saída Aérea Cabo 16mm - Branco", new BigDecimal("1640.00"), "branco"),
                createPoste("4395-C", "Kit Poste 7m Trifásico Saída Aérea Cabo 16mm DJ 70AP - Branco", new BigDecimal("1670.00"), "branco"),
                createPoste("91270-B", "kit poste trifasico 7m saida aerea cabo 35mm - disj 100 - Branco", new BigDecimal("2200.00"), "branco"),
                createPoste("90882-B", "Kit Poste 3 CX 7m Saída Aérea Cabo 16mm - Branco", new BigDecimal("2078.00"), "branco"),
                createPoste("90881-B", "Kit Poste Bi+Mono 7m Saída Aérea Cabo 16 mm - Branco", new BigDecimal("1920.00"), "branco"),
                createPoste("3588-B", "Poste DT 8/300 - Branco", new BigDecimal("0"), "branco"),
                createPoste("1310-B", "Poste DT8/100 - Branco", new BigDecimal("0"), "branco"),
                createPoste("4122-B", "Poste DT 8/200 - Branco", new BigDecimal("0"), "branco"),
                createPoste("91132-B", "Mureta Trifásica CB 16mm disjuntor 70A - Branco", new BigDecimal("820.00"), "branco"),
                createPoste("389-B", "Mureta Água (Hidrômetro) - Branco", new BigDecimal("200.00"), "branco"),
                createPoste("91241-B", "Mureta Mono - Branco", new BigDecimal("600.00"), "branco"),
                createPoste("91242-B", "Mureta Trifásica - Branco", new BigDecimal("710.00"), "branco"),

                );

        log.info("💾 Salvando {} postes para tenant BRANCO...", postesBranco.size());
        List<Poste> savedBranco = posteRepository.saveAll(postesBranco);
        log.info("✅ Salvos {} postes BRANCO", savedBranco.size());
    }

    private Poste createPoste(String codigo, String descricao, BigDecimal preco, String tenantId) {
        Poste poste = new Poste();
        poste.setCodigo(codigo);
        poste.setDescricao(descricao);
        poste.setPreco(preco);
        poste.setAtivo(true);
        poste.setTenantId(tenantId);

        log.debug("📦 Criando poste: {} - {} (tenant: {})", codigo, descricao, tenantId);

        return poste;
    }
}
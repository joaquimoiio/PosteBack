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
            long postesBranco = posteRepository.findByTenantId("vermelho").size();

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
                postesBranco = posteRepository.findByTenantId("vermelho").size();

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
                    postesBranco = posteRepository.findByTenantId("vermelho").size();
                    log.info("✅ Postes BRANCO após correção: {}", postesBranco);
                }
            }

            // Log detalhado dos primeiros postes de cada tenant para debug
            List<Poste> amostrasVermelho = posteRepository.findByTenantId("vermelho");
            List<Poste> amostrasBranco = posteRepository.findByTenantId("vermelho");

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
                // Kit Poste 7m - Monofásico
                createPoste( "4199", "Kit Poste 7m 1 Cx Mono Saída Aérea", new BigDecimal("865.00"), "vermelho"),
                createPoste( "91450", "Kit Poste 7m 1 Cx Mono Saída Aérea cebranorte", new BigDecimal("870.00"), "vermelho"),
                createPoste( "4268", "Kit Poste 7m 1 Cx Mono Saída Subterrânea", new BigDecimal("830.00"), "vermelho"),
                createPoste( "4270", "Kit Poste 7m 2 Cx Mono 2 Saídas Subterrâneas", new BigDecimal("1150.00"), "vermelho"),
                createPoste( "4269", "Kit Poste 7m 2 Cx Mono 1 Saída Aérea + 1 Saída Subterrânea", new BigDecimal("0.00"), "vermelho"),
                createPoste( "4200", "Kit Poste 7m 2 Cx Mono 2 Saídas Aéreas", new BigDecimal("1190.00"), "vermelho"),
                createPoste( "4392", "Kit Poste 7m 3 Cx Mono 3 Saídas Subterrâneas", new BigDecimal("1390.00"), "vermelho"),
                createPoste( "4393", "Kit Poste 7m 3 Cx Mono 1 Saída Aérea + 2 Saídas Subterrâneas", new BigDecimal("0.00"), "vermelho"),
                createPoste( "4394", "Kit Poste 7m 3 Cx Mono 2 Saídas Aéreas + 1 Saída Subterrânea", new BigDecimal("0.00"), "vermelho"),
                createPoste( "4512", "Kit Poste 7m 3 Cx Mono 3 Saídas Aéreas", new BigDecimal("1510.00"), "vermelho"),

                // Kit Poste 7m - Bifásico
                createPoste( "4248", "Kit Poste 7m 1 Cx Bifásica Saída Aérea", new BigDecimal("1120.00"), "vermelho"),
                createPoste( "4248-CERPAULO", "Kit Poste 7m 1 Cx Bifásica Saída Aérea cerpaulo", new BigDecimal("1130.00"), "vermelho"),
                createPoste( "4271", "Kit Poste 7m 1 Cx Bifásica Saída Subterrânea", new BigDecimal("1090.00"), "vermelho"),

                // Kit Poste 7m - Trifásico
                createPoste( "4198", "Kit Poste 7m 1 Cx Trifásica Saída Aérea", new BigDecimal("1210.00"), "vermelho"),
                createPoste( "91451", "Kit Poste 7m 1 Cx Trifásica Saída Aérea (cerbranorte) mesmo cerpaulo", new BigDecimal("1245.00"), "vermelho"),
                createPoste( "4272", "Kit Poste 7m 1 Cx Trifásica Saída Subterrânea", new BigDecimal("1150.00"), "vermelho"),

                // Kit Poste 7m - Combinados
                createPoste( "4273", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Aérea", new BigDecimal("1430.00"), "vermelho"),
                createPoste( "4274", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Bifásica Aérea Saída Mono Subterrânea", new BigDecimal("0.00"), "vermelho"),
                createPoste( "4261", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Bifásica Subterrânea Saída Mono Aérea", new BigDecimal("0.00"), "vermelho"),
                createPoste( "4275", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica 2 Saídas Subterrâneas", new BigDecimal("0.00"), "vermelho"),

                // Kit Poste 7m - Especiais
                createPoste( "4738", "Kit Poste 7m 1 Cx Trifásica - Padrão Mono ( Kit Post Polifásico)", new BigDecimal("950.00"), "vermelho"),

                // Kit Poste 8m - Monofásico
                createPoste( "KIT8M-MONO", "Kit Poste 8m 1 Cx Mono Saída aerias", new BigDecimal("1080.00"), "vermelho"),
                createPoste( "4276", "Kit Poste 8m 1 Cx Mono Saída Subterrânea", new BigDecimal("0.00"), "vermelho"),

                // Kit Poste 8m - 2 Caixas
                createPoste("4203", "Kit Poste 8m 2 Cx Mono 2 Saídas Aéreas", new BigDecimal("1360.00"), "vermelho"),
                createPoste("0000-1", "Kit Poste 8m 2 Cx Mono 1 Saída Aérea 1 Saída Subterrânea", new BigDecimal("0.00"), "vermelho"),
                createPoste("0000-2", "Kit Poste 8m 2 Cx Mono 2 Saídas Subterrâneas", new BigDecimal("0.00"), "vermelho"),

                // Kit Poste 8m - 3 Caixas
                createPoste("0000-3", "Kit Poste 8m 3 Cx Mono 2 Saídas Aéreas 1 Saída Subterrânea", new BigDecimal("0.00"), "vermelho"),
                createPoste("0000-4", "Kit Poste 8m 3 Cx Mono 1 Saída Aérea 2 Saídas Subterrâneas", new BigDecimal("0.00"), "vermelho"),
                createPoste("0000-5", "Kit Poste 8m 3 Cx Mono 3 Saídas Aéreas", new BigDecimal("1710.00"), "vermelho"),
                createPoste("4243", "Kit Poste 8m 3 Cx Mono 3 Saídas Subterrâneas", new BigDecimal("1550.00"), "vermelho"),

                // Kit Poste 8m - Bifásico
                createPoste("4285", "Kit Poste 8m 1 Cx Bifásica Saída Aérea", new BigDecimal("1250.00"), "vermelho"),
                createPoste("4277", "Kit Poste 8m 1 Cx Bifásica Saída Subterrânea", new BigDecimal("1210.00"), "vermelho"),
                createPoste("KIT8M-TRI-CEBRA", "Kit Poste 8m 1 Cx Trifásica Saída Aérea (cebranorte)", new BigDecimal("1430.00"), "vermelho"),

                // Kit Poste 8m - Trifásico
                createPoste("4202", "Kit Poste 8m 1 Cx Trifásica Saída Aérea", new BigDecimal("1395.00"), "vermelho"),
                createPoste("4267", "Kit Poste 8m 1 Cx Trifásica Saída Subterrânea", new BigDecimal("1355.00"), "vermelho"),
                createPoste("91598", "Kit Poste 8m 1 Cx Trifásica Saída Aérea C\\8 PINO RETRATIL", new BigDecimal("1665.00"), "vermelho"),
                createPoste("KIT8M-TRI-16MM", "Kit Poste 8m 1 Cx Trifásica Saída Aérea cobo 16mm", new BigDecimal("1905.00"), "vermelho"),
                createPoste("KIT8M-BI-16MM", "Kit Poste 8m Bifásico Saída aeria Cabo 16mm", new BigDecimal("1665.00"), "vermelho"),
                createPoste("KIT8M-POLI", "Kit Poste 8mt 1 Cx Trifásica - Padrão Mono ( Kit Post Polifásico)", new BigDecimal("1150.00"), "vermelho"),

                // Kit Poste 7m - Cabos especiais
                createPoste("KIT7M-BI-16MM", "Kit Poste 7m Bifásico Saída aeria Cabo 16mm", new BigDecimal("1340.00"), "vermelho"),
                createPoste("KIT-TRI-35MM", "kit trifasico cabo 35 mm", new BigDecimal("2200.00"), "vermelho"),
                createPoste("4462", "Kit Poste 7m Trifásica Saida Sub Cabo 16 Disjuntor 70", new BigDecimal("1540.00"), "vermelho"),
                createPoste("4391", "Kit poste trifasico 7m saida aerea cabo 25mm - disj 70", new BigDecimal("1810.00"), "vermelho"),
                createPoste("4517", "Kit poste trifásico 7m saída sub cabo 25mm", new BigDecimal("0.00"), "vermelho"),
                createPoste("90591", "Kit Poste 7m Bifásico Saída Sub Cabo 16mm", new BigDecimal("0.00"), "vermelho"),
                createPoste("KIT7M-TRI-16MM-DJ70", "Kit Poste 7m Trifásico Saída Aérea Cabo 16mm DJ 70AP", new BigDecimal("1670.00"), "vermelho"),
                createPoste("4395", "Kit Poste 7m Trifásico Saída Aérea Cabo 16mm", new BigDecimal("1640.00"), "vermelho"),

                // Outros produtos
                createPoste("90259", "Kit Poste 7m 1 Cx Mono Saída Aérea (Cerpalo)", new BigDecimal("930.00"), "vermelho"),
                createPoste("1309", "Poste DT 7/100", new BigDecimal("400.00"), "vermelho"),
                createPoste("3588", "Poste DT 8/300", new BigDecimal("0.00"), "vermelho"),


                createPoste("MURETA-TRI", "Mureta sub trifasica", new BigDecimal("700.00"), "vermelho"),
                createPoste("389", "Padrão de agua", new BigDecimal("200.00"), "vermelho"),
                createPoste("MURETA-3CX", "Mureta sub 3cx monofasica", new BigDecimal("810.00"), "vermelho")
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
                // Kit Poste 7m - Monofásico
                createPoste( "4199", "Kit Poste 7m 1 Cx Mono Saída Aérea", new BigDecimal("865.00"), "branco"),
                createPoste( "91450", "Kit Poste 7m 1 Cx Mono Saída Aérea cebranorte", new BigDecimal("870.00"), "branco"),
                createPoste( "4268", "Kit Poste 7m 1 Cx Mono Saída Subterrânea", new BigDecimal("830.00"), "branco"),
                createPoste( "4270", "Kit Poste 7m 2 Cx Mono 2 Saídas Subterrâneas", new BigDecimal("1150.00"), "branco"),
                createPoste( "4269", "Kit Poste 7m 2 Cx Mono 1 Saída Aérea + 1 Saída Subterrânea", new BigDecimal("0.00"), "branco"),
                createPoste( "4200", "Kit Poste 7m 2 Cx Mono 2 Saídas Aéreas", new BigDecimal("1190.00"), "branco"),
                createPoste( "4392", "Kit Poste 7m 3 Cx Mono 3 Saídas Subterrâneas", new BigDecimal("1390.00"), "branco"),
                createPoste( "4393", "Kit Poste 7m 3 Cx Mono 1 Saída Aérea + 2 Saídas Subterrâneas", new BigDecimal("0.00"), "branco"),
                createPoste( "4394", "Kit Poste 7m 3 Cx Mono 2 Saídas Aéreas + 1 Saída Subterrânea", new BigDecimal("0.00"), "branco"),
                createPoste( "4512", "Kit Poste 7m 3 Cx Mono 3 Saídas Aéreas", new BigDecimal("1510.00"), "branco"),

                // Kit Poste 7m - Bifásico
                createPoste( "4248", "Kit Poste 7m 1 Cx Bifásica Saída Aérea", new BigDecimal("1120.00"), "branco"),
                createPoste( "4248-CERPAULO", "Kit Poste 7m 1 Cx Bifásica Saída Aérea cerpaulo", new BigDecimal("1130.00"), "branco"),
                createPoste( "4271", "Kit Poste 7m 1 Cx Bifásica Saída Subterrânea", new BigDecimal("1090.00"), "branco"),

                // Kit Poste 7m - Trifásico
                createPoste( "4198", "Kit Poste 7m 1 Cx Trifásica Saída Aérea", new BigDecimal("1210.00"), "branco"),
                createPoste( "91451", "Kit Poste 7m 1 Cx Trifásica Saída Aérea (cerbranorte) mesmo cerpaulo", new BigDecimal("1245.00"), "branco"),
                createPoste( "4272", "Kit Poste 7m 1 Cx Trifásica Saída Subterrânea", new BigDecimal("1150.00"), "branco"),

                // Kit Poste 7m - Combinados
                createPoste( "4273", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Aérea", new BigDecimal("1430.00"), "branco"),
                createPoste( "4274", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Bifásica Aérea Saída Mono Subterrânea", new BigDecimal("0.00"), "branco"),
                createPoste( "4261", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Bifásica Subterrânea Saída Mono Aérea", new BigDecimal("0.00"), "branco"),
                createPoste( "4275", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica 2 Saídas Subterrâneas", new BigDecimal("0.00"), "branco"),

                // Kit Poste 7m - Especiais
                createPoste( "4738", "Kit Poste 7m 1 Cx Trifásica - Padrão Mono ( Kit Post Polifásico)", new BigDecimal("950.00"), "branco"),

                // Kit Poste 8m - Monofásico
                createPoste( "KIT8M-MONO", "Kit Poste 8m 1 Cx Mono Saída aerias", new BigDecimal("1080.00"), "branco"),
                createPoste( "4276", "Kit Poste 8m 1 Cx Mono Saída Subterrânea", new BigDecimal("0.00"), "branco"),

                // Kit Poste 8m - 2 Caixas
                createPoste("4203", "Kit Poste 8m 2 Cx Mono 2 Saídas Aéreas", new BigDecimal("1360.00"), "branco"),
                createPoste("0000-1", "Kit Poste 8m 2 Cx Mono 1 Saída Aérea 1 Saída Subterrânea", new BigDecimal("0.00"), "branco"),
                createPoste("0000-2", "Kit Poste 8m 2 Cx Mono 2 Saídas Subterrâneas", new BigDecimal("0.00"), "branco"),

                // Kit Poste 8m - 3 Caixas
                createPoste("0000-3", "Kit Poste 8m 3 Cx Mono 2 Saídas Aéreas 1 Saída Subterrânea", new BigDecimal("0.00"), "branco"),
                createPoste("0000-4", "Kit Poste 8m 3 Cx Mono 1 Saída Aérea 2 Saídas Subterrâneas", new BigDecimal("0.00"), "branco"),
                createPoste("0000-5", "Kit Poste 8m 3 Cx Mono 3 Saídas Aéreas", new BigDecimal("1710.00"), "branco"),
                createPoste("4243", "Kit Poste 8m 3 Cx Mono 3 Saídas Subterrâneas", new BigDecimal("1550.00"), "branco"),

                // Kit Poste 8m - Bifásico
                createPoste("4285", "Kit Poste 8m 1 Cx Bifásica Saída Aérea", new BigDecimal("1250.00"), "branco"),
                createPoste("4277", "Kit Poste 8m 1 Cx Bifásica Saída Subterrânea", new BigDecimal("1210.00"), "branco"),
                createPoste("KIT8M-TRI-CEBRA", "Kit Poste 8m 1 Cx Trifásica Saída Aérea (cebranorte)", new BigDecimal("1430.00"), "branco"),

                // Kit Poste 8m - Trifásico
                createPoste("4202", "Kit Poste 8m 1 Cx Trifásica Saída Aérea", new BigDecimal("1395.00"), "branco"),
                createPoste("4267", "Kit Poste 8m 1 Cx Trifásica Saída Subterrânea", new BigDecimal("1355.00"), "branco"),
                createPoste("91598", "Kit Poste 8m 1 Cx Trifásica Saída Aérea C\\8 PINO RETRATIL", new BigDecimal("1665.00"), "branco"),
                createPoste("KIT8M-TRI-16MM", "Kit Poste 8m 1 Cx Trifásica Saída Aérea cobo 16mm", new BigDecimal("1905.00"), "branco"),
                createPoste("KIT8M-BI-16MM", "Kit Poste 8m Bifásico Saída aeria Cabo 16mm", new BigDecimal("1665.00"), "branco"),
                createPoste("KIT8M-POLI", "Kit Poste 8mt 1 Cx Trifásica - Padrão Mono ( Kit Post Polifásico)", new BigDecimal("1150.00"), "branco"),

                // Kit Poste 7m - Cabos especiais
                createPoste("KIT7M-BI-16MM", "Kit Poste 7m Bifásico Saída aeria Cabo 16mm", new BigDecimal("1340.00"), "branco"),
                createPoste("KIT-TRI-35MM", "kit trifasico cabo 35 mm", new BigDecimal("2200.00"), "branco"),
                createPoste("4462", "Kit Poste 7m Trifásica Saida Sub Cabo 16 Disjuntor 70", new BigDecimal("1540.00"), "branco"),
                createPoste("4391", "Kit poste trifasico 7m saida aerea cabo 25mm - disj 70", new BigDecimal("1810.00"), "branco"),
                createPoste("4517", "Kit poste trifásico 7m saída sub cabo 25mm", new BigDecimal("0.00"), "branco"),
                createPoste("90591", "Kit Poste 7m Bifásico Saída Sub Cabo 16mm", new BigDecimal("0.00"), "branco"),
                createPoste("KIT7M-TRI-16MM-DJ70", "Kit Poste 7m Trifásico Saída Aérea Cabo 16mm DJ 70AP", new BigDecimal("1670.00"), "branco"),
                createPoste("4395", "Kit Poste 7m Trifásico Saída Aérea Cabo 16mm", new BigDecimal("1640.00"), "branco"),

                // Outros produtos
                createPoste("90259", "Kit Poste 7m 1 Cx Mono Saída Aérea (Cerpalo)", new BigDecimal("930.00"), "branco"),
                createPoste("1309", "Poste DT 7/100", new BigDecimal("400.00"), "branco"),
                createPoste("3588", "Poste DT 8/300", new BigDecimal("0.00"), "branco"),


                createPoste("MURETA-TRI", "Mureta sub trifasica", new BigDecimal("700.00"), "branco"),
                createPoste("389", "Padrão de agua", new BigDecimal("200.00"), "branco"),
                createPoste("MURETA-3CX", "Mureta sub 3cx monofasica", new BigDecimal("810.00"), "branco")
        );

        log.info("💾 Salvando {} postes para tenant BRANCO...", postesBranco.size());
        List<Poste> savedBranco = posteRepository.saveAll(postesBranco);
        log.info("✅ Salvos {} postes BRANCO", savedBranco.size());
    }
    
    private Poste createPoste(String codigo, String descricao, BigDecimal preco, String tenantId) {
        Poste poste = createPoste();
        poste.setCodigo(codigo);
        poste.setDescricao(descricao);
        poste.setPreco(preco);
        poste.setAtivo(true);
        poste.setTenantId(tenantId);

        log.debug("📦 Criando poste: {} - {} (tenant: {})", codigo, descricao, tenantId);

        return poste;
    }
}
package com.vendas.postes.config;

import com.vendas.postes.model.Poste;
import com.vendas.postes.repository.PosteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PosteRepository posteRepository;

    @Override
    public void run(String... args) throws Exception {
        if (posteRepository.count() == 0) {
            List<Poste> postes = Arrays.asList(
                    // Kit Poste 7m - Monofásico
                    new Poste(null, "4199", "Kit Poste 7m 1 Cx Mono Saída Aérea", new BigDecimal("865.00"), true),
                    new Poste(null, "91450", "Kit Poste 7m 1 Cx Mono Saída Aérea cebranorte", new BigDecimal("870.00"), true),
                    new Poste(null, "4268", "Kit Poste 7m 1 Cx Mono Saída Subterrânea", new BigDecimal("830.00"), true),
                    new Poste(null, "4270", "Kit Poste 7m 2 Cx Mono 2 Saídas Subterrâneas", new BigDecimal("1150.00"), true),
                    new Poste(null, "4269", "Kit Poste 7m 2 Cx Mono 1 Saída Aérea + 1 Saída Subterrânea", new BigDecimal("0.00"), false),
                    new Poste(null, "4200", "Kit Poste 7m 2 Cx Mono 2 Saídas Aéreas", new BigDecimal("1190.00"), true),
                    new Poste(null, "4392", "Kit Poste 7m 3 Cx Mono 3 Saídas Subterrâneas", new BigDecimal("1390.00"), true),
                    new Poste(null, "4393", "Kit Poste 7m 3 Cx Mono 1 Saída Aérea + 2 Saídas Subterrâneas", new BigDecimal("0.00"), false),
                    new Poste(null, "4394", "Kit Poste 7m 3 Cx Mono 2 Saídas Aéreas + 1 Saída Subterrânea", new BigDecimal("0.00"), false),
                    new Poste(null, "4512", "Kit Poste 7m 3 Cx Mono 3 Saídas Aéreas", new BigDecimal("1510.00"), true),

                    // Kit Poste 7m - Bifásico
                    new Poste(null, "4248", "Kit Poste 7m 1 Cx Bifásica Saída Aérea", new BigDecimal("1120.00"), true),
                    new Poste(null, "4248-CERPAULO", "Kit Poste 7m 1 Cx Bifásica Saída Aérea cerpaulo", new BigDecimal("1130.00"), true),
                    new Poste(null, "4271", "Kit Poste 7m 1 Cx Bifásica Saída Subterrânea", new BigDecimal("1090.00"), true),

                    // Kit Poste 7m - Trifásico
                    new Poste(null, "4198", "Kit Poste 7m 1 Cx Trifásica Saída Aérea", new BigDecimal("1210.00"), true),
                    new Poste(null, "91451", "Kit Poste 7m 1 Cx Trifásica Saída Aérea (cerbranorte) mesmo cerpaulo", new BigDecimal("1245.00"), true),
                    new Poste(null, "4272", "Kit Poste 7m 1 Cx Trifásica Saída Subterrânea", new BigDecimal("1150.00"), true),

                    // Kit Poste 7m - Combinados
                    new Poste(null, "4273", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Aérea", new BigDecimal("1430.00"), true),
                    new Poste(null, "4274", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Bifásica Aérea Saída Mono Subterrânea", new BigDecimal("0.00"), false),
                    new Poste(null, "4261", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Bifásica Subterrânea Saída Mono Aérea", new BigDecimal("0.00"), false),
                    new Poste(null, "4275", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica 2 Saídas Subterrâneas", new BigDecimal("0.00"), false),

                    // Kit Poste 7m - Especiais
                    new Poste(null, "4738", "Kit Poste 7m 1 Cx Trifásica - Padrão Mono ( Kit Post Polifásico)", new BigDecimal("950.00"), true),

                    // Kit Poste 8m - Monofásico
                    new Poste(null, "KIT8M-MONO", "Kit Poste 8m 1 Cx Mono Saída aerias", new BigDecimal("1080.00"), true),
                    new Poste(null, "4276", "Kit Poste 8m 1 Cx Mono Saída Subterrânea", new BigDecimal("0.00"), false),

                    // Kit Poste 8m - 2 Caixas
                    new Poste(null, "4203", "Kit Poste 8m 2 Cx Mono 2 Saídas Aéreas", new BigDecimal("1360.00"), true),
                    new Poste(null, "0000-1", "Kit Poste 8m 2 Cx Mono 1 Saída Aérea 1 Saída Subterrânea", new BigDecimal("0.00"), false),
                    new Poste(null, "0000-2", "Kit Poste 8m 2 Cx Mono 2 Saídas Subterrâneas", new BigDecimal("0.00"), false),

                    // Kit Poste 8m - 3 Caixas
                    new Poste(null, "0000-3", "Kit Poste 8m 3 Cx Mono 2 Saídas Aéreas 1 Saída Subterrânea", new BigDecimal("0.00"), false),
                    new Poste(null, "0000-4", "Kit Poste 8m 3 Cx Mono 1 Saída Aérea 2 Saídas Subterrâneas", new BigDecimal("0.00"), false),
                    new Poste(null, "0000-5", "Kit Poste 8m 3 Cx Mono 3 Saídas Aéreas", new BigDecimal("1710.00"), true),
                    new Poste(null, "4243", "Kit Poste 8m 3 Cx Mono 3 Saídas Subterrâneas", new BigDecimal("1550.00"), true),

                    // Kit Poste 8m - Bifásico
                    new Poste(null, "4285", "Kit Poste 8m 1 Cx Bifásica Saída Aérea", new BigDecimal("1250.00"), true),
                    new Poste(null, "4277", "Kit Poste 8m 1 Cx Bifásica Saída Subterrânea", new BigDecimal("1210.00"), true),
                    new Poste(null, "KIT8M-TRI-CEBRA", "Kit Poste 8m 1 Cx Trifásica Saída Aérea (cebranorte)", new BigDecimal("1430.00"), true),

                    // Kit Poste 8m - Trifásico
                    new Poste(null, "4202", "Kit Poste 8m 1 Cx Trifásica Saída Aérea", new BigDecimal("1395.00"), true),
                    new Poste(null, "4267", "Kit Poste 8m 1 Cx Trifásica Saída Subterrânea", new BigDecimal("1355.00"), true),
                    new Poste(null, "91598", "Kit Poste 8m 1 Cx Trifásica Saída Aérea C\\8 PINO RETRATIL", new BigDecimal("1665.00"), true),
                    new Poste(null, "KIT8M-TRI-16MM", "Kit Poste 8m 1 Cx Trifásica Saída Aérea cobo 16mm", new BigDecimal("1905.00"), true),
                    new Poste(null, "KIT8M-BI-16MM", "Kit Poste 8m Bifásico Saída aeria Cabo 16mm", new BigDecimal("1665.00"), true),
                    new Poste(null, "KIT8M-POLI", "Kit Poste 8mt 1 Cx Trifásica - Padrão Mono ( Kit Post Polifásico)", new BigDecimal("1150.00"), true),

                    // Kit Poste 7m - Cabos especiais
                    new Poste(null, "KIT7M-BI-16MM", "Kit Poste 7m Bifásico Saída aeria Cabo 16mm", new BigDecimal("1340.00"), true),
                    new Poste(null, "KIT-TRI-35MM", "kit trifasico cabo 35 mm", new BigDecimal("2200.00"), true),
                    new Poste(null, "4462", "Kit Poste 7m Trifásica Saida Sub Cabo 16 Disjuntor 70", new BigDecimal("1540.00"), true),
                    new Poste(null, "4391", "Kit poste trifasico 7m saida aerea cabo 25mm - disj 70", new BigDecimal("1810.00"), true),
                    new Poste(null, "4517", "Kit poste trifásico 7m saída sub cabo 25mm", new BigDecimal("0.00"), false),
                    new Poste(null, "90591", "Kit Poste 7m Bifásico Saída Sub Cabo 16mm", new BigDecimal("0.00"), false),
                    new Poste(null, "KIT7M-TRI-16MM-DJ70", "Kit Poste 7m Trifásico Saída Aérea Cabo 16mm DJ 70AP", new BigDecimal("1670.00"), true),
                    new Poste(null, "4395", "Kit Poste 7m Trifásico Saída Aérea Cabo 16mm", new BigDecimal("1640.00"), true),

                    // Outros produtos
                    new Poste(null, "90259", "Kit Poste 7m 1 Cx Mono Saída Aérea (Cerpalo)", new BigDecimal("930.00"), true),
                    new Poste(null, "1309", "Poste DT 7/100", new BigDecimal("400.00"), true),
                    new Poste(null, "3588", "Poste DT 8/300", new BigDecimal("0.00"), false),

                    // Muretas e Padrões
                    new Poste(null, "MURETA-TRI", "Mureta sub trifasica", new BigDecimal("700.00"), true),
                    new Poste(null, "389", "Padrão de agua", new BigDecimal("200.00"), true),
                    new Poste(null, "MURETA-3CX", "Mureta sub 3cx monofasica", new BigDecimal("810.00"), true)
            );

            posteRepository.saveAll(postes);
            System.out.println("Postes carregados com sucesso! Total: " + postes.size() + " itens");

            long ativos = postes.stream().filter(Poste::getAtivo).count();
            long inativos = postes.stream().filter(p -> !p.getAtivo()).count();

            System.out.println("Postes ativos: " + ativos);
            System.out.println("Postes inativos: " + inativos);
        }
    }
}
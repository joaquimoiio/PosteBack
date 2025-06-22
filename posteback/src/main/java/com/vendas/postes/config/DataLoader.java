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
                    new Poste(null, "4200", "Kit Poste 7m 1 Cx Mono Saída Aérea", new BigDecimal("870.00"), true),
                    new Poste(null, "4201", "Kit Poste 7m 1 Cx Mono Saída Aérea", new BigDecimal("830.00"), true),
                    new Poste(null, "4202", "Kit Poste 7m 1 Cx Mono Saída Aérea", new BigDecimal("1140.00"), true),
                    new Poste(null, "4203", "Kit Poste 7m 1 Cx Mono Saída Aérea", new BigDecimal("0.00"), false),
                    new Poste(null, "90259", "Kit Poste 7m 1 Cx Mono Saída Aérea (Cerpalo)", new BigDecimal("850.00"), true),

                    // Kit Poste 7m - 2 Caixas Mono
                    new Poste(null, "4200-2CX", "Kit Poste 7m 2 Cx Mono 2 Saídas Aéreas", new BigDecimal("1190.00"), true),

                    // Kit Poste 7m - 3 Caixas Mono
                    new Poste(null, "4392", "Kit Poste 7m 3 Cx Mono 3 Saídas Subterrâneas", new BigDecimal("1390.00"), true),
                    new Poste(null, "4393", "Kit Poste 7m 3 Cx Mono 1 Saída Aérea + 2 Saídas Subterrâneas", new BigDecimal("0.00"), false),
                    new Poste(null, "4394", "Kit Poste 7m 3 Cx Mono 2 Saídas Aéreas + 1 Saída Subterrânea", new BigDecimal("0.00"), false),
                    new Poste(null, "4512", "Kit Poste 7m 3 Cx Mono 3 Saídas Aéreas", new BigDecimal("1510.00"), true),

                    // Kit Poste 7m - Bifásico
                    new Poste(null, "4248", "Kit Poste 7m 1 Cx Bifásica Saída Aérea", new BigDecimal("1120.00"), true),
                    new Poste(null, "4248-CERPALO", "Kit Poste 7m 1 Cx Bifásica Saída Aérea (Cerpalo)", new BigDecimal("1130.00"), true),
                    new Poste(null, "4271", "Kit Poste 7m 1 Cx Bifásica Saída Subterrânea", new BigDecimal("0.00"), false),
                    new Poste(null, "91372", "Kit Poste 7m Bifásico S/A Cabo 16mm", new BigDecimal("1400.00"), true),
                    new Poste(null, "90591", "Kit Poste 7m Bifásico Saída Sub Cabo 16mm", new BigDecimal("0.00"), false),

                    // Kit Poste 7m - Trifásico
                    new Poste(null, "4198", "Kit Poste 7m 1 Cx Trifásica Saída Aérea", new BigDecimal("1210.00"), true),
                    new Poste(null, "4272", "Kit Poste 7m 1 Cx Trifásica Saída Subterrânea", new BigDecimal("1150.00"), true),
                    new Poste(null, "91451", "Kit Poste 7m 1 Cx Trifásica Saída Aérea (CERBRANORTE)", new BigDecimal("1230.00"), true),
                    new Poste(null, "4395", "Kit Poste 7m Trifásico Saída Aérea Cabo 16mm", new BigDecimal("1640.00"), true),
                    new Poste(null, "4395-DJ70", "Kit Poste 7m Trifásico Saída Aérea Cabo 16mm DJ 70AP", new BigDecimal("1670.00"), true),
                    new Poste(null, "4462", "Kit Poste 7m Trifásica Saída Sub Cabo 16 Disjuntor 70", new BigDecimal("1580.00"), true),
                    new Poste(null, "4391", "Kit poste trifásico 7m saída aérea cabo 25mm - disj 70", new BigDecimal("1900.00"), true),
                    new Poste(null, "4391-DJ100", "Kit poste trifásico 7m saída aérea cabo 25mm - disj 100", new BigDecimal("0.00"), false),
                    new Poste(null, "4517", "Kit poste trifásico 7m saída sub cabo 25mm", new BigDecimal("0.00"), false),
                    new Poste(null, "91270", "kit poste trifásico 7m saída aérea cabo 35mm - disj 100", new BigDecimal("2200.00"), true),

                    // Kit Poste 7m - Combinados
                    new Poste(null, "4273", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Aérea", new BigDecimal("1430.00"), true),
                    new Poste(null, "4274", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Bifásica Aérea Saída Mono Subterrânea", new BigDecimal("0.00"), false),
                    new Poste(null, "4261", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Bifásica Subterrânea Saída Mono Aérea", new BigDecimal("0.00"), false),
                    new Poste(null, "4275", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica 2 Saídas Subterrâneas", new BigDecimal("0.00"), false),
                    new Poste(null, "90882", "Kit Poste 3 CX 7m Saída Aérea Cabo 16mm", new BigDecimal("2078.00"), true),
                    new Poste(null, "90881", "Kit Poste Bi+Mono 7m Saída Aérea Cabo 16 mm", new BigDecimal("1920.00"), true),

                    // Kit Poste 7m - Especiais
                    new Poste(null, "4738", "Kit Poste 7m 1 Cx Trifásica - Padrão Mono (Kit Post Polifásico)", new BigDecimal("990.00"), true),

                    // Kit Poste 8m - Monofásico
                    new Poste(null, "4201-8M", "Kit Poste 8m 1 Cx Mono Saída Aérea", new BigDecimal("1080.00"), true),
                    new Poste(null, "4276", "Kit Poste 8m 1 Cx Mono Saída Subterrânea", new BigDecimal("0.00"), false),

                    // Kit Poste 8m - 2 Caixas
                    new Poste(null, "4203-8M", "Kit Poste 8m 2 Cx Mono 2 Saídas Aéreas", new BigDecimal("1380.00"), true),
                    new Poste(null, "0001", "Kit Poste 8m 2 Cx Mono 1 Saída Aérea 1 Saída Subterrânea", new BigDecimal("0.00"), false),
                    new Poste(null, "0002", "Kit Poste 8m 2 Cx Mono 2 Saídas Subterrâneas", new BigDecimal("0.00"), false),

                    // Kit Poste 8m - 3 Caixas
                    new Poste(null, "0003", "Kit Poste 8m 3 Cx Mono 2 Saídas Aéreas 1 Saída Subterrânea", new BigDecimal("0.00"), false),
                    new Poste(null, "0004", "Kit Poste 8m 3 Cx Mono 1 Saída Aérea 2 Saídas Subterrâneas", new BigDecimal("0.00"), false),
                    new Poste(null, "0005", "Kit Poste 8m 3 Cx Mono 3 Saídas Aéreas", new BigDecimal("1720.00"), true),
                    new Poste(null, "4243", "Kit Poste 8m 3 Cx Mono 3 Saídas Subterrâneas", new BigDecimal("1660.00"), true),

                    // Kit Poste 8m - Bifásico
                    new Poste(null, "4285", "Kit Poste 8m 1 Cx Mono + Bifásica Saída Aérea", new BigDecimal("0.00"), false),
                    new Poste(null, "4249", "Kit Poste 8 m 1 Cx Bifásica Saída Aérea", new BigDecimal("1285.00"), true),
                    new Poste(null, "91005", "Kit Poste 8 m 1 Cx Bifásica Saída Aérea", new BigDecimal("1285.00"), true),
                    new Poste(null, "91511", "Kit Poste 8 m 1 Cx Bifásica Saída Aérea CB 16mm Disj 70", new BigDecimal("1560.00"), true),
                    new Poste(null, "4277", "Kit Poste 8m 1 Cx Bifásica Saída Subterrânea", new BigDecimal("0.00"), false),

                    // Kit Poste 8m - Trifásico
                    new Poste(null, "4202-8M", "Kit Poste 8m 1 Cx Trifásica Saída Aérea", new BigDecimal("1395.00"), true),
                    new Poste(null, "4267", "Kit Poste 8m 1 Cx Trifásica Saída Subterrânea", new BigDecimal("0.00"), false),
                    new Poste(null, "91006", "Kit Poste 8m 1 Cx Trifásica Saída Aérea Cabo 16mm", new BigDecimal("1905.00"), true),
                    new Poste(null, "91326", "Kit Poste 8m 1 Cx Trifásico S/A CB 25mm Disj 100A", new BigDecimal("2180.00"), true),
                    new Poste(null, "91371", "Kit Poste 8m 1 Cx Trifásica - Padrão Mono (Kit Post Polifásico)", new BigDecimal("1150.00"), true),
                    new Poste(null, "90394", "Kit Poste 8m 1 Cx Trifásica (Cerbranorte)", new BigDecimal("1415.00"), true),
                    new Poste(null, "91598", "Kit Poste 8m 1 Cx Trifásica S/A C/8 Pino Retrátil Derivação 10mm", new BigDecimal("1665.00"), true),

                    // Postes DT
                    new Poste(null, "1309", "Poste DT 7/100", new BigDecimal("350.00"), true),
                    new Poste(null, "3588", "Poste DT 8/300", new BigDecimal("0.00"), false),
                    new Poste(null, "1310", "Poste DT8/100", new BigDecimal("0.00"), false),
                    new Poste(null, "4122", "Poste DT 8/200", new BigDecimal("0.00"), false),

                    // Muretas
                    new Poste(null, "91132", "Mureta Trifásica CB 16mm disjuntor 70A", new BigDecimal("820.00"), true),
                    new Poste(null, "389", "Mureta Água (Hidrômetro)", new BigDecimal("200.00"), true),
                    new Poste(null, "91241", "Mureta Mono", new BigDecimal("600.00"), true),
                    new Poste(null, "91242", "Mureta Trifásica", new BigDecimal("710.00"), true),

                    new Poste(null, "CARGA-TRI", "Carga Trifásica - devolveu 04/01", new BigDecimal("0.00"), false),
                    new Poste(null, "CARGA-BI-MONO", "Carga Bi+Mono - devolveu 04/01", new BigDecimal("0.00"), false),
                    new Poste(null, "FRETE", "Somente Frete", new BigDecimal("0.00"), false)
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
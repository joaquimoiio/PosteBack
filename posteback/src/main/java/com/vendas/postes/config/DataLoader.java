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
                    new Poste(null, "4199", "Kit Poste 7m 1 Cx Mono Saída Aérea", new BigDecimal("450.00"), true),
                    new Poste(null, "91450", "Kit Poste 7m 1 Cx Mono Saída Aérea (cebranorte)", new BigDecimal("460.00"), true),
                    new Poste(null, "4268", "Kit Poste 7m 1 Cx Mono Saída Subterrânea", new BigDecimal("470.00"), true),
                    new Poste(null, "4270", "Kit Poste 7m 2 Cx Mono 2 Saídas Subterrâneas", new BigDecimal("620.00"), true),
                    new Poste(null, "4269", "Kit Poste 7m 2 Cx Mono 1 Saída Aérea + 1 Saída Subterrânea", new BigDecimal("610.00"), true),
                    new Poste(null, "4200", "Kit Poste 7m 2 Cx Mono 2 Saídas Aéreas", new BigDecimal("600.00"), true),
                    new Poste(null, "4392", "Kit Poste 7m 3 Cx Mono 3 Saídas Subterrâneas", new BigDecimal("780.00"), true),
                    new Poste(null, "4393", "Kit Poste 7m 3 Cx Mono 1 Saída Aérea + 2 Saídas Subterrâneas", new BigDecimal("770.00"), true),
                    new Poste(null, "4394", "Kit Poste 7m 3 Cx Mono 2 Saídas Aéreas + 1 Saída Subterrânea", new BigDecimal("760.00"), true),
                    new Poste(null, "4512", "Kit Poste 7m 3 Cx Mono 3 Saídas Aéreas", new BigDecimal("750.00"), true),
                    new Poste(null, "4248", "Kit Poste 7m 1 Cx Bifásica Saída Aérea", new BigDecimal("520.00"), true),
                    new Poste(null, "4271", "Kit Poste 7m 1 Cx Bifásica Saída Subterrânea", new BigDecimal("530.00"), true),
                    new Poste(null, "4198", "Kit Poste 7m 1 Cx Trifásica Saída Aérea", new BigDecimal("580.00"), true),
                    new Poste(null, "91451", "Kit Poste 7m 1 Cx Trifásica Saída Aérea (Cebranorte)", new BigDecimal("590.00"), true),
                    new Poste(null, "4272", "Kit Poste 7m 1 Cx Trifásica Saída Subterrânea", new BigDecimal("600.00"), true),
                    new Poste(null, "4273", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Aérea", new BigDecimal("650.00"), true),
                    new Poste(null, "4274", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Bifásica Aérea Saída Mono Subterrânea", new BigDecimal("670.00"), true),
                    new Poste(null, "4261", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica Saída Bifásica Subterrânea Saída Mono Aérea", new BigDecimal("660.00"), true),
                    new Poste(null, "4275", "Kit Poste 7m 1 Cx Mono 1 Cx Bifásica 2 Saídas Subterrâneas", new BigDecimal("680.00"), true),
                    new Poste(null, "4738", "Kit Poste 7m 1 Cx Trifásica - Padrão Mono (Kit Post Polifásico)", new BigDecimal("620.00"), true),
                    new Poste(null, "4201", "Kit Poste 8m 1 Cx Mono Saída Aérea", new BigDecimal("520.00"), true),
                    new Poste(null, "4276", "Kit Poste 8m 1 Cx Mono Saída Subterrânea", new BigDecimal("530.00"), true),
                    new Poste(null, "4203", "Kit Poste 8m 2 Cx Mono 2 Saídas Aéreas", new BigDecimal("680.00"), true),
                    new Poste(null, "0001", "Kit Poste 8m 2 Cx Mono 1 Saída Aérea 1 Saída Subterrânea", new BigDecimal("690.00"), true),
                    new Poste(null, "0002", "Kit Poste 8m 2 Cx Mono 2 Saídas Subterrâneas", new BigDecimal("700.00"), true),
                    new Poste(null, "0003", "Kit Poste 8m 3 Cx Mono 2 Saídas Aéreas 1 Saída Subterrânea", new BigDecimal("820.00"), true),
                    new Poste(null, "0004", "Kit Poste 8m 3 Cx Mono 1 Saída Aérea 2 Saídas Subterrâneas", new BigDecimal("830.00"), true),
                    new Poste(null, "0005", "Kit Poste 8m 3 Cx Mono 3 Saídas Aéreas", new BigDecimal("810.00"), true),
                    new Poste(null, "4243", "Kit Poste 8m 3 Cx Mono 3 Saídas Subterrâneas", new BigDecimal("840.00"), true),
                    new Poste(null, "4285", "Kit Poste 8m 1 Cx Mono + Bifásica Saída Aérea", new BigDecimal("720.00"), true),
                    new Poste(null, "4249", "Kit Poste 8m 1 Cx Bifásica Saída Aérea", new BigDecimal("590.00"), true),
                    new Poste(null, "91005", "Kit Poste 8m 1 Cx Bifásica Saída Aérea (Alternativo)", new BigDecimal("595.00"), true),
                    new Poste(null, "4277", "Kit Poste 8m 1 Cx Bifásica Saída Subterrânea", new BigDecimal("600.00"), true),
                    new Poste(null, "4202", "Kit Poste 8m 1 Cx Trifásica Saída Aérea", new BigDecimal("650.00"), true),
                    new Poste(null, "0006", "Kit Poste 8m 1 Cx Trifásica Saída Aérea (Cebranorte)", new BigDecimal("660.00"), true),
                    new Poste(null, "4267", "Kit Poste 8m 1 Cx Trifásica Saída Subterrânea", new BigDecimal("670.00"), true),
                    new Poste(null, "91006", "Kit Poste 8m 1 Cx Trifásica Saída Aérea Cabo 16mm", new BigDecimal("680.00"), true),
                    new Poste(null, "90259", "Kit Poste 7m 1 Cx Mono Saída Aérea (Cerpalo)", new BigDecimal("470.00"), true),
                    new Poste(null, "1309", "Poste DT 7/100", new BigDecimal("280.00"), true),
                    new Poste(null, "0007", "Carga Trifásica - devolveu 04/01", new BigDecimal("0.00"), true),
                    new Poste(null, "0008", "Deslocamento", new BigDecimal("50.00"), true),
                    new Poste(null, "0009", "Mureta água (hidrômetro)", new BigDecimal("180.00"), true),
                    new Poste(null, "4462", "Kit Poste 7m Trifásica Saída Sub Cabo 16 Disjuntor 70", new BigDecimal("720.00"), true),
                    new Poste(null, "4391", "Kit poste trifásico 7m saída aérea cabo 25mm - disj 70", new BigDecimal("750.00"), true),
                    new Poste(null, "0010", "Kit poste trifásico 7m saída aérea cabo 16 mm disjuntor 70", new BigDecimal("700.00"), true),
                    new Poste(null, "4517", "Kit poste trifásico 7m saída sub cabo 25mm", new BigDecimal("780.00"), true),
                    new Poste(null, "90591", "Kit Poste 7m Bifásico Saída Sub Cabo 16mm", new BigDecimal("620.00"), true),
                    new Poste(null, "4395", "Kit Poste 7m Trifásico Saída Aérea Cabo 16mm", new BigDecimal("680.00"), true),
                    new Poste(null, "91270", "Kit poste trifásico 7m saída aérea cabo 35mm - disj 100", new BigDecimal("820.00"), true),
                    new Poste(null, "90882", "Kit Poste 3 CX 7m Saída Aérea Cabo 16mm", new BigDecimal("850.00"), true),
                    new Poste(null, "90881", "Kit Poste Bi+Mono 7m Saída Aérea Cabo 16 mm", new BigDecimal("720.00"), true),
                    new Poste(null, "3588", "Poste DT 8/300", new BigDecimal("450.00"), true),
                    new Poste(null, "1310", "Poste DT8/100", new BigDecimal("320.00"), true),
                    new Poste(null, "0011", "Mureta 3cx monofásica", new BigDecimal("250.00"), true),
                    new Poste(null, "91132", "Mureta Trifásica CB 16mm disjuntor 70A", new BigDecimal("280.00"), true),
                    new Poste(null, "389", "Mureta Água (Hidrômetro)", new BigDecimal("180.00"), true),
                    new Poste(null, "91241", "Mureta Mono", new BigDecimal("200.00"), true),
                    new Poste(null, "91242", "Mureta Trifásica", new BigDecimal("300.00"), true)
            );

            posteRepository.saveAll(postes);
            System.out.println("Postes iniciais carregados com sucesso!");
        }
    }
}
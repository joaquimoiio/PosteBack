package com.vendas.postes.config;

import com.vendas.postes.model.Poste;
import com.vendas.postes.repository.PosteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * DataLoader atualizado com separação por tenant
 * Cada poste agora tem um campo tenant_id para identificar qual caminhão
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PosteRepository posteRepository;

    @Override
    public void run(String... args) throws Exception {
        if (posteRepository.count() == 0) {
            carregarPostesPorTenant();
            System.out.println("✅ Postes carregados com separação por tenant");
        }
    }

    private void carregarPostesPorTenant() {
        // Postes do Caminhão Vermelho
        List<Poste> postesVermelho = Arrays.asList(
                // Kit Poste 7m - Monofásico - VERMELHO
                createPoste("4199", "Kit Poste 7m 1 Cx Mono Saída Aérea", new BigDecimal("865.00"), "vermelho"),
                createPoste("91450", "Kit Poste 7m 1 Cx Mono Saída Aérea cebranorte", new BigDecimal("870.00"), "vermelho"),
                createPoste("4268", "Kit Poste 7m 1 Cx Mono Saída Subterrânea", new BigDecimal("830.00"), "vermelho"),
                createPoste("4270", "Kit Poste 7m 2 Cx Mono 2 Saídas Subterrâneas", new BigDecimal("1150.00"), "vermelho"),
                createPoste("4200", "Kit Poste 7m 2 Cx Mono 2 Saídas Aéreas", new BigDecimal("1190.00"), "vermelho"),
                createPoste("4392", "Kit Poste 7m 3 Cx Mono 3 Saídas Subterrâneas", new BigDecimal("1390.00"), "vermelho"),
                createPoste("4512", "Kit Poste 7m 3 Cx Mono 3 Saídas Aéreas", new BigDecimal("1510.00"), "vermelho"),

                // Kit Poste 7m - Bifásico - VERMELHO
                createPoste("4248", "Kit Poste 7m 1 Cx Bifásica Saída Aérea", new BigDecimal("1120.00"), "vermelho"),
                createPoste("4248-CERPAULO", "Kit Poste 7m 1 Cx Bifásica Saída Aérea cerpaulo", new BigDecimal("1130.00"), "vermelho"),
                createPoste("4271", "Kit Poste 7m 1 Cx Bifásica Saída Subterrânea", new BigDecimal("1090.00"), "vermelho"),

                // Kit Poste 7m - Trifásico - VERMELHO
                createPoste("4198", "Kit Poste 7m 1 Cx Trifásica Saída Aérea", new BigDecimal("1210.00"), "vermelho"),
                createPoste("91451", "Kit Poste 7m 1 Cx Trifásica Saída Aérea (cerbranorte)", new BigDecimal("1245.00"), "vermelho"),
                createPoste("4272", "Kit Poste 7m 1 Cx Trifásica Saída Subterrânea", new BigDecimal("1150.00"), "vermelho"),

                // Kit Poste 8m - VERMELHO
                createPoste("KIT8M-MONO", "Kit Poste 8m 1 Cx Mono Saída aerias", new BigDecimal("1080.00"), "vermelho"),
                createPoste("4203", "Kit Poste 8m 2 Cx Mono 2 Saídas Aéreas", new BigDecimal("1360.00"), "vermelho"),
                createPoste("4243", "Kit Poste 8m 3 Cx Mono 3 Saídas Subterrâneas", new BigDecimal("1550.00"), "vermelho"),
                createPoste("4285", "Kit Poste 8m 1 Cx Bifásica Saída Aérea", new BigDecimal("1250.00"), "vermelho"),
                createPoste("4202", "Kit Poste 8m 1 Cx Trifásica Saída Aérea", new BigDecimal("1395.00"), "vermelho"),
                createPoste("91598", "Kit Poste 8m 1 Cx Trifásica Saída Aérea C\\8 PINO RETRATIL", new BigDecimal("1665.00"), "vermelho"),

                // Outros - VERMELHO
                createPoste("90259", "Kit Poste 7m 1 Cx Mono Saída Aérea (Cerpalo)", new BigDecimal("930.00"), "vermelho"),
                createPoste("1309", "Poste DT 7/100", new BigDecimal("400.00"), "vermelho"),
                createPoste("MURETA-TRI", "Mureta sub trifasica", new BigDecimal("700.00"), "vermelho"),
                createPoste("389", "Padrão de agua", new BigDecimal("200.00"), "vermelho")
        );

        // Postes do Caminhão Branco
        List<Poste> postesBranco = Arrays.asList(
                // Kit Poste 7m - Monofásico - BRANCO
                createPoste("4199-B", "Kit Poste 7m 1 Cx Mono Saída Aérea - Branco", new BigDecimal("865.00"), "branco"),
                createPoste("91450-B", "Kit Poste 7m 1 Cx Mono Saída Aérea cebranorte - Branco", new BigDecimal("870.00"), "branco"),
                createPoste("4268-B", "Kit Poste 7m 1 Cx Mono Saída Subterrânea - Branco", new BigDecimal("830.00"), "branco"),
                createPoste("4270-B", "Kit Poste 7m 2 Cx Mono 2 Saídas Subterrâneas - Branco", new BigDecimal("1150.00"), "branco"),
                createPoste("4200-B", "Kit Poste 7m 2 Cx Mono 2 Saídas Aéreas - Branco", new BigDecimal("1190.00"), "branco"),

                // Kit Poste 7m - Bifásico - BRANCO
                createPoste("4248-B", "Kit Poste 7m 1 Cx Bifásica Saída Aérea - Branco", new BigDecimal("1120.00"), "branco"),
                createPoste("4271-B", "Kit Poste 7m 1 Cx Bifásica Saída Subterrânea - Branco", new BigDecimal("1090.00"), "branco"),

                // Kit Poste 7m - Trifásico - BRANCO
                createPoste("4198-B", "Kit Poste 7m 1 Cx Trifásica Saída Aérea - Branco", new BigDecimal("1210.00"), "branco"),
                createPoste("4272-B", "Kit Poste 7m 1 Cx Trifásica Saída Subterrânea - Branco", new BigDecimal("1150.00"), "branco"),

                // Kit Poste 8m - BRANCO
                createPoste("KIT8M-MONO-B", "Kit Poste 8m 1 Cx Mono Saída aerias - Branco", new BigDecimal("1080.00"), "branco"),
                createPoste("4203-B", "Kit Poste 8m 2 Cx Mono 2 Saídas Aéreas - Branco", new BigDecimal("1360.00"), "branco"),
                createPoste("4285-B", "Kit Poste 8m 1 Cx Bifásica Saída Aérea - Branco", new BigDecimal("1250.00"), "branco"),
                createPoste("4202-B", "Kit Poste 8m 1 Cx Trifásica Saída Aérea - Branco", new BigDecimal("1395.00"), "branco"),

                // Outros - BRANCO
                createPoste("90259-B", "Kit Poste 7m 1 Cx Mono Saída Aérea (Cerpalo) - Branco", new BigDecimal("930.00"), "branco"),
                createPoste("1309-B", "Poste DT 7/100 - Branco", new BigDecimal("400.00"), "branco"),
                createPoste("MURETA-TRI-B", "Mureta sub trifasica - Branco", new BigDecimal("700.00"), "branco"),
                createPoste("389-B", "Padrão de agua - Branco", new BigDecimal("200.00"), "branco")
        );

        // Salvar todos os postes
        posteRepository.saveAll(postesVermelho);
        posteRepository.saveAll(postesBranco);

        System.out.println("📊 Postes carregados:");
        System.out.println("🚛 Caminhão Vermelho: " + postesVermelho.size() + " postes");
        System.out.println("🚚 Caminhão Branco: " + postesBranco.size() + " postes");
        System.out.println("📦 Total: " + (postesVermelho.size() + postesBranco.size()) + " postes");
    }

    private Poste createPoste(String codigo, String descricao, BigDecimal preco, String tenantId) {
        Poste poste = new Poste();
        poste.setCodigo(codigo);
        poste.setDescricao(descricao);
        poste.setPreco(preco);
        poste.setAtivo(true);
        poste.setTenantId(tenantId);
        return poste;
    }
}
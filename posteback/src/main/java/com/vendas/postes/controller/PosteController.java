package com.vendas.postes.controller;

import com.vendas.postes.config.TenantContext;
import com.vendas.postes.model.Poste;
import com.vendas.postes.repository.PosteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/postes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class PosteController {

    private final PosteRepository posteRepository;

    @GetMapping
    public ResponseEntity<List<Poste>> listarTodos() {
        try {
            String tenantId = TenantContext.getCurrentTenantValue();
            log.info("Buscando postes para tenant: {}", tenantId);

            List<Poste> postes = posteRepository.findByTenantId(tenantId);
            log.info("Encontrados {} postes para o tenant {}", postes.size(), tenantId);

            return ResponseEntity.ok(postes);
        } catch (Exception e) {
            log.error("Erro ao buscar postes: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Poste> buscarPorId(@PathVariable Long id) {
        try {
            String tenantId = TenantContext.getCurrentTenantValue();
            Optional<Poste> poste = posteRepository.findById(id);

            if (poste.isPresent() && poste.get().getTenantId().equals(tenantId)) {
                return ResponseEntity.ok(poste.get());
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erro ao buscar poste por ID: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Poste> criar(@RequestBody Poste poste) {
        try {
            String tenantId = TenantContext.getCurrentTenantValue();
            poste.setTenantId(tenantId);
            poste.setAtivo(true);

            Poste savedPoste = posteRepository.save(poste);
            log.info("Poste criado com sucesso para tenant {}: {}", tenantId, savedPoste.getCodigo());

            return ResponseEntity.ok(savedPoste);
        } catch (Exception e) {
            log.error("Erro ao criar poste: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Poste> atualizar(@PathVariable Long id, @RequestBody Poste posteAtualizado) {
        try {
            String tenantId = TenantContext.getCurrentTenantValue();

            return posteRepository.findById(id)
                    .filter(poste -> poste.getTenantId().equals(tenantId))
                    .map(poste -> {
                        poste.setCodigo(posteAtualizado.getCodigo());
                        poste.setDescricao(posteAtualizado.getDescricao());
                        poste.setPreco(posteAtualizado.getPreco());
                        poste.setAtivo(posteAtualizado.getAtivo());
                        return ResponseEntity.ok(posteRepository.save(poste));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao atualizar poste: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            String tenantId = TenantContext.getCurrentTenantValue();

            return posteRepository.findById(id)
                    .filter(poste -> poste.getTenantId().equals(tenantId))
                    .map(poste -> {
                        poste.setAtivo(false);
                        posteRepository.save(poste);
                        return ResponseEntity.ok().build();
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao deletar poste: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
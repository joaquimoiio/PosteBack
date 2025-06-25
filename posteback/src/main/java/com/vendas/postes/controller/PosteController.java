package com.vendas.postes.controller;

import com.vendas.postes.config.TenantContext;
import com.vendas.postes.model.Poste;
import com.vendas.postes.repository.PosteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/postes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PosteController {

    private final PosteRepository posteRepository;

    @GetMapping
    public List<Poste> listarTodos() {
        String tenantId = TenantContext.getCurrentTenantValue();
        if (tenantId != null) {
            return posteRepository.findByTenantId(tenantId);
        }
        return posteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Poste> buscarPorId(@PathVariable Long id) {
        Optional<Poste> poste = posteRepository.findById(id);

        if (poste.isPresent()) {
            // Verificar se o poste pertence ao tenant atual
            String tenantAtual = TenantContext.getCurrentTenantValue();
            if (tenantAtual != null && !tenantAtual.equals(poste.get().getTenantId())) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(poste.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public Poste criar(@RequestBody Poste poste) {
        // Definir tenant atual
        String tenantId = TenantContext.getCurrentTenantValue();
        if (tenantId != null) {
            poste.setTenantId(tenantId);
        }
        return posteRepository.save(poste);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Poste> atualizar(@PathVariable Long id, @RequestBody Poste posteAtualizado) {
        return posteRepository.findById(id)
                .map(poste -> {
                    // Verificar se o poste pertence ao tenant atual
                    String tenantAtual = TenantContext.getCurrentTenantValue();
                    if (tenantAtual != null && !tenantAtual.equals(poste.getTenantId())) {
                        return ResponseEntity.notFound().<Poste>build();
                    }

                    poste.setCodigo(posteAtualizado.getCodigo());
                    poste.setDescricao(posteAtualizado.getDescricao());
                    poste.setPreco(posteAtualizado.getPreco());
                    poste.setAtivo(posteAtualizado.getAtivo());
                    // Manter o tenant ID original
                    return ResponseEntity.ok(posteRepository.save(poste));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        return posteRepository.findById(id)
                .map(poste -> {
                    // Verificar se o poste pertence ao tenant atual
                    String tenantAtual = TenantContext.getCurrentTenantValue();
                    if (tenantAtual != null && !tenantAtual.equals(poste.getTenantId())) {
                        return ResponseEntity.notFound().build();
                    }

                    poste.setAtivo(false);
                    posteRepository.save(poste);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
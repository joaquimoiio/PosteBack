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
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PosteController {

    private final PosteRepository posteRepository;

    @GetMapping
    public List<Poste> listarTodos() {
        String tenantId = TenantContext.getCurrentTenantValue();
        return posteRepository.findByTenantId(tenantId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Poste> buscarPorId(@PathVariable Long id) {
        Optional<Poste> poste = posteRepository.findById(id);
        if (poste.isPresent() && poste.get().getTenantId().equals(TenantContext.getCurrentTenantValue())) {
            return ResponseEntity.ok(poste.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public Poste criar(@RequestBody Poste poste) {
        poste.setTenantId(TenantContext.getCurrentTenantValue());
        return posteRepository.save(poste);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Poste> atualizar(@PathVariable Long id, @RequestBody Poste posteAtualizado) {
        return posteRepository.findById(id)
                .filter(poste -> poste.getTenantId().equals(TenantContext.getCurrentTenantValue()))
                .map(poste -> {
                    poste.setCodigo(posteAtualizado.getCodigo());
                    poste.setDescricao(posteAtualizado.getDescricao());
                    poste.setPreco(posteAtualizado.getPreco());
                    poste.setAtivo(posteAtualizado.getAtivo());
                    return ResponseEntity.ok(posteRepository.save(poste));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        return posteRepository.findById(id)
                .filter(poste -> poste.getTenantId().equals(TenantContext.getCurrentTenantValue()))
                .map(poste -> {
                    poste.setAtivo(false);
                    posteRepository.save(poste);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
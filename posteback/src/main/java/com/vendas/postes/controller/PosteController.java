package com.vendas.postes.controller;

import com.vendas.postes.config.TenantContext;
import com.vendas.postes.model.Poste;
import com.vendas.postes.repository.PosteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

            // Verificar se o tenant é válido
            if (tenantId == null || tenantId.trim().isEmpty()) {
                log.error("TenantId inválido: {}", tenantId);
                return ResponseEntity.badRequest().build();
            }

            List<Poste> postes = posteRepository.findByTenantId(tenantId);

            if (postes == null) {
                log.warn("Repository retornou null para tenant: {}", tenantId);
                postes = List.of(); // Lista vazia ao invés de null
            }

            log.info("Encontrados {} postes para o tenant {}", postes.size(), tenantId);

            return ResponseEntity.ok(postes);

        } catch (Exception e) {
            log.error("Erro ao buscar postes: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Poste> buscarPorId(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                log.warn("ID inválido fornecido: {}", id);
                return ResponseEntity.badRequest().build();
            }

            String tenantId = TenantContext.getCurrentTenantValue();
            log.debug("Buscando poste ID {} para tenant: {}", id, tenantId);

            Optional<Poste> poste = posteRepository.findByIdAndTenantId(id, tenantId);

            if (poste.isPresent()) {
                return ResponseEntity.ok(poste.get());
            } else {
                log.warn("Poste não encontrado: ID={}, tenant={}", id, tenantId);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            log.error("Erro ao buscar poste por ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Poste> criar(@RequestBody Poste poste) {
        try {
            if (poste == null) {
                log.warn("Tentativa de criar poste com dados nulos");
                return ResponseEntity.badRequest().build();
            }

            // Validações básicas
            if (poste.getCodigo() == null || poste.getCodigo().trim().isEmpty()) {
                log.warn("Tentativa de criar poste sem código");
                return ResponseEntity.badRequest().build();
            }

            if (poste.getDescricao() == null || poste.getDescricao().trim().isEmpty()) {
                log.warn("Tentativa de criar poste sem descrição");
                return ResponseEntity.badRequest().build();
            }

            if (poste.getPreco() == null || poste.getPreco().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                log.warn("Tentativa de criar poste com preço inválido: {}", poste.getPreco());
                return ResponseEntity.badRequest().build();
            }

            String tenantId = TenantContext.getCurrentTenantValue();
            poste.setTenantId(tenantId);
            poste.setAtivo(true);

            log.debug("Criando poste para tenant {}: {}", tenantId, poste.getCodigo());

            Poste savedPoste = posteRepository.save(poste);
            log.info("Poste criado com sucesso para tenant {}: ID={}, codigo={}",
                    tenantId, savedPoste.getId(), savedPoste.getCodigo());

            return ResponseEntity.status(HttpStatus.CREATED).body(savedPoste);

        } catch (Exception e) {
            log.error("Erro ao criar poste: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Poste> atualizar(@PathVariable Long id, @RequestBody Poste posteAtualizado) {
        try {
            if (id == null || id <= 0) {
                log.warn("ID inválido para atualização: {}", id);
                return ResponseEntity.badRequest().build();
            }

            if (posteAtualizado == null) {
                log.warn("Dados nulos para atualização do poste ID: {}", id);
                return ResponseEntity.badRequest().build();
            }

            String tenantId = TenantContext.getCurrentTenantValue();
            log.debug("Atualizando poste ID {} para tenant: {}", id, tenantId);

            Optional<Poste> posteOpt = posteRepository.findByIdAndTenantId(id, tenantId);

            if (posteOpt.isEmpty()) {
                log.warn("Poste não encontrado para atualização: ID={}, tenant={}", id, tenantId);
                return ResponseEntity.notFound().build();
            }

            Poste poste = posteOpt.get();

            // Atualizar apenas campos não nulos
            if (posteAtualizado.getCodigo() != null) {
                poste.setCodigo(posteAtualizado.getCodigo());
            }
            if (posteAtualizado.getDescricao() != null) {
                poste.setDescricao(posteAtualizado.getDescricao());
            }
            if (posteAtualizado.getPreco() != null) {
                poste.setPreco(posteAtualizado.getPreco());
            }
            if (posteAtualizado.getAtivo() != null) {
                poste.setAtivo(posteAtualizado.getAtivo());
            }

            Poste savedPoste = posteRepository.save(poste);
            log.info("Poste atualizado com sucesso: ID={}, tenant={}", id, tenantId);

            return ResponseEntity.ok(savedPoste);

        } catch (Exception e) {
            log.error("Erro ao atualizar poste ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                log.warn("ID inválido para exclusão: {}", id);
                return ResponseEntity.badRequest().build();
            }

            String tenantId = TenantContext.getCurrentTenantValue();
            log.debug("Inativando poste ID {} para tenant: {}", id, tenantId);

            Optional<Poste> posteOpt = posteRepository.findByIdAndTenantId(id, tenantId);

            if (posteOpt.isEmpty()) {
                log.warn("Poste não encontrado para exclusão: ID={}, tenant={}", id, tenantId);
                return ResponseEntity.notFound().build();
            }

            Poste poste = posteOpt.get();
            poste.setAtivo(false);
            posteRepository.save(poste);

            log.info("Poste inativado com sucesso: ID={}, tenant={}", id, tenantId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            log.error("Erro ao deletar poste ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
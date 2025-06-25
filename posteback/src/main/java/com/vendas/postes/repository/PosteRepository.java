package com.vendas.postes.repository;

import com.vendas.postes.model.Poste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PosteRepository extends JpaRepository<Poste, Long> {
    List<Poste> findByTenantIdAndAtivoTrue(String tenantId);
    List<Poste> findByTenantId(String tenantId);
}
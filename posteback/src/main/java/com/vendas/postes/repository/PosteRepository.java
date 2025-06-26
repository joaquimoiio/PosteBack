package com.vendas.postes.repository;

import com.vendas.postes.model.Poste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PosteRepository extends JpaRepository<Poste, Long> {

    @Query("SELECT p FROM Poste p WHERE p.tenantId = :tenantId AND p.ativo = true")
    List<Poste> findByTenantIdAndAtivoTrue(@Param("tenantId") String tenantId);

    @Query("SELECT p FROM Poste p WHERE p.tenantId = :tenantId")
    List<Poste> findByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT p FROM Poste p WHERE p.id = :id AND p.tenantId = :tenantId")
    Optional<Poste> findByIdAndTenantId(@Param("id") Long id, @Param("tenantId") String tenantId);
}
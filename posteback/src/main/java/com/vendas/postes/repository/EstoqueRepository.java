package com.vendas.postes.repository;

import com.vendas.postes.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    Optional<Estoque> findByPosteId(Long posteId);
    List<Estoque> findByTenantId(String tenantId);

    @Query("SELECT e FROM Estoque e WHERE e.tenantId = :tenantId AND e.quantidadeAtual > 0")
    List<Estoque> findEstoquesComQuantidadePorTenant(String tenantId);
}
package com.vendas.postes.repository;

import com.vendas.postes.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findByTenantIdOrderByDataVendaDesc(String tenantId);
    List<Venda> findByTenantIdAndDataVendaBetween(String tenantId, LocalDateTime inicio, LocalDateTime fim);
    List<Venda> findByTenantId(String tenantId);
}
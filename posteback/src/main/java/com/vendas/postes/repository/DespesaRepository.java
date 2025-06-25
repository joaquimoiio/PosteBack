package com.vendas.postes.repository;

import com.vendas.postes.model.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DespesaRepository extends JpaRepository<Despesa, Long> {
    List<Despesa> findByTipo(Despesa.TipoDespesa tipo);
    List<Despesa> findByDataDespesaBetween(LocalDate inicio, LocalDate fim);

    // Métodos específicos por tenant
    List<Despesa> findByTenantId(String tenantId);
    List<Despesa> findByTenantIdAndTipo(String tenantId, Despesa.TipoDespesa tipo);
    List<Despesa> findByTenantIdAndDataDespesaBetween(String tenantId, LocalDate inicio, LocalDate fim);
    List<Despesa> findByTenantIdAndTipoAndDataDespesaBetween(String tenantId, Despesa.TipoDespesa tipo, LocalDate inicio, LocalDate fim);
}
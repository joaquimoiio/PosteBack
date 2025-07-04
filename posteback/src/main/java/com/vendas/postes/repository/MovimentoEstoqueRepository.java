package com.vendas.postes.repository;

import com.vendas.postes.model.MovimentoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimentoEstoqueRepository extends JpaRepository<MovimentoEstoque, Long> {

    /**
     * Busca movimentos por tenant
     */
    List<MovimentoEstoque> findByTenantIdOrderByDataRegistroDesc(String tenantId);

    /**
     * Busca movimentos por período
     */
    List<MovimentoEstoque> findByTenantIdAndDataMovimentoBetweenOrderByDataRegistroDesc(
            String tenantId, LocalDate dataInicio, LocalDate dataFim);

    /**
     * Busca movimentos por poste
     */
    List<MovimentoEstoque> findByPosteIdAndTenantIdOrderByDataRegistroDesc(Long posteId, String tenantId);

    /**
     * Busca movimentos por tipo
     */
    List<MovimentoEstoque> findByTenantIdAndTipoMovimentoOrderByDataRegistroDesc(
            String tenantId, MovimentoEstoque.TipoMovimento tipoMovimento);

    /**
     * Busca últimos movimentos
     */
    @Query("SELECT m FROM MovimentoEstoque m WHERE m.tenantId = :tenantId ORDER BY m.dataRegistro DESC")
    List<MovimentoEstoque> findUltimosMovimentos(@Param("tenantId") String tenantId);

    /**
     * Busca movimentos consolidados (para Jefferson)
     */
    @Query("SELECT m FROM MovimentoEstoque m ORDER BY m.dataRegistro DESC")
    List<MovimentoEstoque> findAllMovimentosConsolidados();

    /**
     * Conta movimentos por período e tipo
     */
    @Query("SELECT COUNT(m) FROM MovimentoEstoque m WHERE m.tenantId = :tenantId " +
            "AND m.dataMovimento BETWEEN :dataInicio AND :dataFim " +
            "AND m.tipoMovimento = :tipoMovimento")
    Long countMovimentosByPeriodoAndTipo(
            @Param("tenantId") String tenantId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("tipoMovimento") MovimentoEstoque.TipoMovimento tipoMovimento
    );
}
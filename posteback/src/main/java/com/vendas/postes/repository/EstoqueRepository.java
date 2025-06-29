package com.vendas.postes.repository;

import com.vendas.postes.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    /**
     * Busca estoque por ID do poste específico
     */
    Optional<Estoque> findByPosteId(Long posteId);

    /**
     * Busca estoque por tenant (para compatibilidade)
     */
    List<Estoque> findByTenantId(String tenantId);

    /**
     * Busca estoques com quantidade > 0 por tenant
     */
    @Query("SELECT e FROM Estoque e WHERE e.tenantId = :tenantId AND e.quantidadeAtual > 0")
    List<Estoque> findEstoquesComQuantidadePorTenant(@Param("tenantId") String tenantId);

    /**
     * Busca estoque por código de poste (aproximado)
     * Usado para encontrar postes relacionados (ex: 4199 e 4199-B)
     */
    @Query("SELECT e FROM Estoque e JOIN e.poste p WHERE " +
            "(p.codigo = :codigo OR p.codigo = :codigoVariante1 OR p.codigo = :codigoVariante2) " +
            "AND p.ativo = true")
    List<Estoque> findByCodigoPosteVariants(
            @Param("codigo") String codigo,
            @Param("codigoVariante1") String codigoVariante1,
            @Param("codigoVariante2") String codigoVariante2
    );

    /**
     * Busca todo o estoque consolidado (ignorando tenant)
     */
    @Query("SELECT e FROM Estoque e JOIN e.poste p WHERE p.ativo = true ORDER BY p.codigo")
    List<Estoque> findAllEstoqueConsolidado();

    /**
     * Busca estoques por padrão de código (para consolidação)
     */
    @Query("SELECT e FROM Estoque e JOIN e.poste p WHERE " +
            "p.codigo LIKE :codigoPattern AND p.ativo = true ORDER BY p.codigo")
    List<Estoque> findByCodigoPattern(@Param("codigoPattern") String codigoPattern);

    /**
     * Soma quantidade total por código base
     */
    @Query("SELECT SUM(e.quantidadeAtual) FROM Estoque e JOIN e.poste p WHERE " +
            "(p.codigo = :codigoBase OR p.codigo LIKE :codigoBasePattern) AND p.ativo = true")
    Integer sumQuantidadeByCodigoBase(
            @Param("codigoBase") String codigoBase,
            @Param("codigoBasePattern") String codigoBasePattern
    );

    /**
     * Busca estoques negativos (para alertas)
     */
    @Query("SELECT e FROM Estoque e WHERE e.quantidadeAtual < 0 ORDER BY e.quantidadeAtual")
    List<Estoque> findEstoquesNegativos();

    /**
     * Busca estoque baixo (quantidade entre 1 e 5)
     */
    @Query("SELECT e FROM Estoque e WHERE e.quantidadeAtual > 0 AND e.quantidadeAtual <= 5 ORDER BY e.quantidadeAtual")
    List<Estoque> findEstoqueBaixo();

    /**
     * Busca primeiro estoque disponível para um código base
     * Usado para encontrar onde reduzir estoque prioritariamente
     */
    @Query("SELECT e FROM Estoque e JOIN e.poste p WHERE " +
            "(p.codigo = :codigoBase OR p.codigo LIKE :codigoPattern) " +
            "AND e.quantidadeAtual > 0 AND p.ativo = true " +
            "ORDER BY e.quantidadeAtual DESC")
    List<Estoque> findPrimeiroEstoqueDisponivelPorCodigo(
            @Param("codigoBase") String codigoBase,
            @Param("codigoPattern") String codigoPattern
    );

    /**
     * Conta total de tipos de postes únicos
     */
    @Query("SELECT COUNT(DISTINCT " +
            "CASE " +
            "  WHEN p.codigo LIKE '%-B' THEN SUBSTRING(p.codigo, 1, LENGTH(p.codigo) - 2) " +
            "  WHEN p.codigo LIKE '%-C' THEN SUBSTRING(p.codigo, 1, LENGTH(p.codigo) - 2) " +
            "  ELSE p.codigo " +
            "END) " +
            "FROM Estoque e JOIN e.poste p WHERE p.ativo = true")
    Long countTiposPostesUnicos();

    /**
     * Busca último estoque atualizado
     */
    @Query("SELECT e FROM Estoque e ORDER BY e.dataAtualizacao DESC")
    List<Estoque> findUltimosEstoquesAtualizados();
}
package com.vendas.postes.repository;

import com.vendas.postes.model.Estoque;
import com.vendas.postes.model.Poste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    Optional<Estoque> findByPoste(Poste poste);
    Optional<Estoque> findByPosteId(Long posteId);
    List<Estoque> findAllByOrderByPosteCodigoAsc();

    @Query("SELECT e FROM Estoque e WHERE e.quantidadeAtual <= e.quantidadeMinima")
    List<Estoque> findEstoquesAbaixoMinimo();

    @Query("SELECT e FROM Estoque e WHERE e.quantidadeAtual > 0")
    List<Estoque> findEstoquesComQuantidade();

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Estoque e WHERE e.poste.id = :posteId AND e.quantidadeAtual >= :quantidade")
    boolean existeEstoqueSuficiente(Long posteId, Integer quantidade);

    // Métodos específicos por tenant
    List<Estoque> findByTenantId(String tenantId);
    List<Estoque> findByTenantIdOrderByPosteCodigoAsc(String tenantId);

    @Query("SELECT e FROM Estoque e WHERE e.tenantId = :tenantId AND e.quantidadeAtual <= e.quantidadeMinima")
    List<Estoque> findEstoquesAbaixoMinimoPorTenant(String tenantId);

    @Query("SELECT e FROM Estoque e WHERE e.tenantId = :tenantId AND e.quantidadeAtual > 0")
    List<Estoque> findEstoquesComQuantidadePorTenant(String tenantId);

    @Query("SELECT e FROM Estoque e WHERE e.tenantId = :tenantId AND e.quantidadeAtual <> 0")
    List<Estoque> findEstoquesComQuantidadeDiferenteZeroPorTenant(String tenantId);
}
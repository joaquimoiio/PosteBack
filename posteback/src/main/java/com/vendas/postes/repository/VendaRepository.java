package com.vendas.postes.repository;

import com.vendas.postes.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    @Query("SELECT v FROM Venda v LEFT JOIN FETCH v.itens ORDER BY v.dataVenda DESC")
    List<Venda> findAllOrderByDataVendaDesc();

    List<Venda> findByDataVendaBetween(LocalDateTime inicio, LocalDateTime fim);

    List<Venda> findByTipoVenda(Venda.TipoVenda tipoVenda);

    @Query("SELECT SUM(v.totalFreteEletrons) FROM Venda v WHERE v.totalFreteEletrons IS NOT NULL")
    BigDecimal calcularTotalFreteEletrons();

    @Query("SELECT SUM(v.totalComissao) FROM Venda v WHERE v.totalComissao IS NOT NULL")
    BigDecimal calcularTotalComissao();

    @Query("SELECT SUM(v.valorTotalInformado) FROM Venda v WHERE v.valorTotalInformado IS NOT NULL AND v.tipoVenda = :tipoVenda")
    BigDecimal calcularTotalValorInformadoPorTipo(@Param("tipoVenda") Venda.TipoVenda tipoVenda);

    @Query("SELECT SUM(v.valorExtra) FROM Venda v WHERE v.valorExtra IS NOT NULL AND v.tipoVenda = 'E'")
    BigDecimal calcularTotalValorExtra();

    @Query("SELECT COUNT(v) FROM Venda v WHERE v.tipoVenda = :tipoVenda")
    Long contarVendasPorTipo(@Param("tipoVenda") Venda.TipoVenda tipoVenda);

    // Consulta para calcular contribuições para o lucro por tipo
    @Query("SELECT SUM(CASE " +
            "WHEN v.tipoVenda = 'E' THEN COALESCE(v.valorExtra, 0) " +
            "WHEN v.tipoVenda = 'L' THEN COALESCE(v.valorTotalInformado, 0) " +
            "ELSE 0 END) " +
            "FROM Venda v WHERE v.tipoVenda IN ('E', 'L')")
    BigDecimal calcularContribuicoesExtras();
}
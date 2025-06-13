package com.vendas.postes.repository;

import com.vendas.postes.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    @Query("SELECT v FROM Venda v LEFT JOIN FETCH v.itens ORDER BY v.dataVenda DESC")
    List<Venda> findAllOrderByDataVendaDesc();

    List<Venda> findByDataVendaBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT SUM(v.totalFreteEletrons) FROM Venda v WHERE v.totalFreteEletrons IS NOT NULL")
    BigDecimal calcularTotalFreteEletrons();

    @Query("SELECT SUM(v.totalComissao) FROM Venda v WHERE v.totalComissao IS NOT NULL")
    BigDecimal calcularTotalComissao();

    @Query("SELECT SUM(v.valorTotalInformado) FROM Venda v WHERE v.valorTotalInformado IS NOT NULL")
    BigDecimal calcularTotalValorInformado();
}
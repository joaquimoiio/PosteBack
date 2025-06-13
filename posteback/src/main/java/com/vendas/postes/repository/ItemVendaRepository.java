package com.vendas.postes.repository;

import com.vendas.postes.model.ItemVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {
    List<ItemVenda> findByVendaId(Long vendaId);

    @Query("SELECT SUM(iv.subtotal) FROM ItemVenda iv")
    BigDecimal calcularTotalVendaPostes();
}
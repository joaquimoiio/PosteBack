package com.vendas.postes.repository;

import com.vendas.postes.model.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DespesaRepository extends JpaRepository<Despesa, Long> {
    List<Despesa> findByTipo(Despesa.TipoDespesa tipo);
    List<Despesa> findByDataDespesaBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT SUM(d.valor) FROM Despesa d WHERE d.tipo = :tipo")
    BigDecimal calcularTotalPorTipo(Despesa.TipoDespesa tipo);

    @Query("SELECT SUM(d.valor) FROM Despesa d")
    BigDecimal calcularTotalDespesas();
}
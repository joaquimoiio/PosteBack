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
}
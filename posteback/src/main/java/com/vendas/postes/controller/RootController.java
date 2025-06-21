package com.vendas.postes.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class RootController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "online");
        response.put("message", "API Sistema de Vendas de Postes");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now());

        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("Postes", "/api/postes");
        endpoints.put("Vendas", "/api/vendas");
        endpoints.put("Estoque", "/api/estoque");
        endpoints.put("Despesas", "/api/despesas");
        endpoints.put("Relatórios", "/api/relatorios");

        response.put("endpoints", endpoints);

        Map<String, String> examples = new HashMap<>();
        examples.put("Listar postes", "GET /api/postes");
        examples.put("Criar venda", "POST /api/vendas");
        examples.put("Ver estoque", "GET /api/estoque");
        examples.put("Resumo vendas", "GET /api/vendas/resumo");

        response.put("examples", examples);

        return response;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "vendas-postes-api");
        return response;
    }
}
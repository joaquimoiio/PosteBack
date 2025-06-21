package com.vendas.postes.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiDocsController {

    @GetMapping("/")
    public Map<String, Object> apiDocs() {
        Map<String, Object> response = new HashMap<>();
        response.put("title", "API Sistema de Vendas de Postes");
        response.put("version", "1.0.0");
        response.put("description", "API para gerenciamento de vendas, estoque e relatórios de postes");

        List<Map<String, Object>> endpoints = new ArrayList<>();

        // Postes
        endpoints.add(createEndpoint("GET", "/api/postes", "Listar todos os postes"));
        endpoints.add(createEndpoint("POST", "/api/postes", "Criar novo poste"));
        endpoints.add(createEndpoint("PUT", "/api/postes/{id}", "Atualizar poste"));
        endpoints.add(createEndpoint("DELETE", "/api/postes/{id}", "Excluir poste"));

        // Vendas
        endpoints.add(createEndpoint("GET", "/api/vendas", "Listar todas as vendas"));
        endpoints.add(createEndpoint("GET", "/api/vendas/resumo", "Resumo das vendas"));
        endpoints.add(createEndpoint("POST", "/api/vendas", "Criar nova venda"));
        endpoints.add(createEndpoint("PUT", "/api/vendas/{id}", "Atualizar venda"));
        endpoints.add(createEndpoint("DELETE", "/api/vendas/{id}", "Excluir venda"));

        // Estoque
        endpoints.add(createEndpoint("GET", "/api/estoque", "Listar todo o estoque"));
        endpoints.add(createEndpoint("GET", "/api/estoque/com-quantidade", "Estoque com quantidade"));
        endpoints.add(createEndpoint("POST", "/api/estoque/adicionar", "Adicionar estoque"));

        // Despesas
        endpoints.add(createEndpoint("GET", "/api/despesas", "Listar despesas"));
        endpoints.add(createEndpoint("POST", "/api/despesas", "Criar despesa"));
        endpoints.add(createEndpoint("PUT", "/api/despesas/{id}", "Atualizar despesa"));
        endpoints.add(createEndpoint("DELETE", "/api/despesas/{id}", "Excluir despesa"));

        // Relatórios
        endpoints.add(createEndpoint("GET", "/api/relatorios/vendas-por-poste", "Relatório de vendas por poste"));

        response.put("endpoints", endpoints);

        return response;
    }

    private Map<String, Object> createEndpoint(String method, String path, String description) {
        Map<String, Object> endpoint = new HashMap<>();
        endpoint.put("method", method);
        endpoint.put("path", path);
        endpoint.put("description", description);
        return endpoint;
    }
}
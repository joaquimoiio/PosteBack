package com.vendas.postes.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
@Slf4j
public class TenantFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Adicionar headers CORS
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Tenant-ID");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");

        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        try {
            String tenantId = extractTenant(httpRequest);
            TenantContext.TenantType tenant = TenantContext.TenantType.fromValue(tenantId);
            TenantContext.setCurrentTenant(tenant);

            log.debug("Tenant definido para requisição {}: {}", httpRequest.getRequestURI(), tenant.getValue());

            chain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Erro no TenantFilter para URI {}: ", httpRequest.getRequestURI(), e);

            // Se for uma requisição de API, retornar JSON
            if (httpRequest.getRequestURI().startsWith("/api/")) {
                httpResponse.setContentType("application/json");
                httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                httpResponse.getWriter().write("{\"error\":\"Erro interno do servidor\",\"message\":\"" + e.getMessage() + "\"}");
                return;
            }

            throw e;
        } finally {
            TenantContext.clear();
        }
    }

    private String extractTenant(HttpServletRequest request) {
        try {
            // Prioridade: Header > Query Param > Default
            String tenant = request.getHeader("X-Tenant-ID");
            if (isValidTenant(tenant)) {
                log.debug("Tenant extraído do header: {}", tenant);
                return tenant;
            }

            tenant = request.getParameter("caminhao");
            if (isValidTenant(tenant)) {
                log.debug("Tenant extraído do parâmetro: {}", tenant);
                return tenant;
            }

            // Verificar URL para detectar automaticamente
            String path = request.getRequestURI();
            if (path.contains("branco") || path.contains("-branco")) {
                log.debug("Tenant extraído do path (branco): {}", path);
                return "branco";
            }

            log.debug("Usando tenant padrão: vermelho");
            return "vermelho"; // Default

        } catch (Exception e) {
            log.warn("Erro ao extrair tenant, usando padrão: {}", e.getMessage());
            return "vermelho";
        }
    }

    private boolean isValidTenant(String tenant) {
        return tenant != null && !tenant.trim().isEmpty() &&
                (tenant.equals("vermelho") || tenant.equals("branco"));
    }
}
package com.vendas.postes.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class TenantFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        try {
            String tenantId = extractTenant(httpRequest);
            TenantContext.TenantType tenant = TenantContext.TenantType.fromValue(tenantId);
            TenantContext.setCurrentTenant(tenant);

            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private String extractTenant(HttpServletRequest request) {
        // Prioridade: Header > Query Param > URL Path
        String tenant = request.getHeader("X-Tenant-ID");
        if (tenant != null) return tenant;

        tenant = request.getParameter("caminhao");
        if (tenant != null) return tenant;

        String path = request.getRequestURI();
        if (path.contains("branco")) return "branco";

        return "vermelho"; // Default
    }
}
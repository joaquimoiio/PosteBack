package com.vendas.postes.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
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

        try {
            String tenantId = extractTenant(httpRequest);
            TenantContext.TenantType tenant = TenantContext.TenantType.fromValue(tenantId);
            TenantContext.setCurrentTenant(tenant);

            log.debug("Tenant definido para requisição {}: {}", httpRequest.getRequestURI(), tenant.getValue());

            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Erro no TenantFilter: ", e);
            throw e;
        } finally {
            TenantContext.clear();
        }
    }

    private String extractTenant(HttpServletRequest request) {
        // Prioridade: Header > Query Param > URL Path
        String tenant = request.getHeader("X-Tenant-ID");
        if (tenant != null && !tenant.trim().isEmpty()) {
            log.debug("Tenant extraído do header: {}", tenant);
            return tenant;
        }

        tenant = request.getParameter("caminhao");
        if (tenant != null && !tenant.trim().isEmpty()) {
            log.debug("Tenant extraído do parâmetro: {}", tenant);
            return tenant;
        }

        String path = request.getRequestURI();
        if (path.contains("branco")) {
            log.debug("Tenant extraído do path (branco): {}", path);
            return "branco";
        }

        log.debug("Usando tenant padrão: vermelho");
        return "vermelho"; // Default
    }
}
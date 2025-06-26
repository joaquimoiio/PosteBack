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

            // Log mais detalhado para debug
            log.info("🚛 Requisição: {} | Tenant extraído: {} | Headers: X-Tenant-ID={} | Param caminhao={}",
                    httpRequest.getRequestURI(),
                    tenant.getValue(),
                    httpRequest.getHeader("X-Tenant-ID"),
                    httpRequest.getParameter("caminhao"));

            chain.doFilter(request, response);

        } catch (Exception e) {
            log.error("❌ Erro no TenantFilter para URI {}: ", httpRequest.getRequestURI(), e);

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
            // 1. Header X-Tenant-ID (prioridade máxima)
            String tenant = request.getHeader("X-Tenant-ID");
            if (isValidTenant(tenant)) {
                log.debug("✅ Tenant extraído do header X-Tenant-ID: {}", tenant);
                return tenant;
            }

            // 2. Query parameter 'caminhao'
            tenant = request.getParameter("caminhao");
            if (isValidTenant(tenant)) {
                log.debug("✅ Tenant extraído do parâmetro caminhao: {}", tenant);
                return tenant;
            }

            // 3. Query parameter 'tenant'
            tenant = request.getParameter("tenant");
            if (isValidTenant(tenant)) {
                log.debug("✅ Tenant extraído do parâmetro tenant: {}", tenant);
                return tenant;
            }

            // 4. Verificar URL path
            String path = request.getRequestURI().toLowerCase();
            if (path.contains("branco") || path.contains("-branco") || path.contains("caminhao-branco")) {
                log.debug("✅ Tenant extraído do path (detectado 'branco'): {}", path);
                return "branco";
            }
            if (path.contains("vermelho") || path.contains("-vermelho") || path.contains("caminhao-vermelho")) {
                log.debug("✅ Tenant extraído do path (detectado 'vermelho'): {}", path);
                return "vermelho";
            }

            // 5. Default
            log.debug("⚠️ Nenhum tenant específico encontrado, usando padrão: vermelho");
            return "vermelho";

        } catch (Exception e) {
            log.warn("❌ Erro ao extrair tenant, usando padrão vermelho: {}", e.getMessage());
            return "vermelho";
        }
    }

    private boolean isValidTenant(String tenant) {
        if (tenant == null || tenant.trim().isEmpty()) {
            return false;
        }

        String cleanTenant = tenant.trim().toLowerCase();
        boolean valid = "vermelho".equals(cleanTenant) || "branco".equals(cleanTenant);

        if (!valid && !tenant.trim().isEmpty()) {
            log.warn("⚠️ Valor de tenant inválido recebido: '{}'", tenant);
        }

        return valid;
    }
}
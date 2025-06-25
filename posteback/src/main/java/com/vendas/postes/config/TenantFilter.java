package com.vendas.postes.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro para identificar e definir o tenant (caminhão) atual
 * baseado no header X-Tenant-ID da requisição
 */
@Component
@Order(1)
public class TenantFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TenantFilter.class);
    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String TENANT_PARAM = "caminhao";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        try {
            // Tentar obter tenant do header primeiro
            String tenantId = httpRequest.getHeader(TENANT_HEADER);

            // Se não encontrar no header, tentar no parâmetro da query string
            if (tenantId == null || tenantId.trim().isEmpty()) {
                tenantId = httpRequest.getParameter(TENANT_PARAM);
            }

            // Se ainda não encontrar, tentar extrair da URL
            if (tenantId == null || tenantId.trim().isEmpty()) {
                tenantId = extractTenantFromPath(httpRequest.getRequestURI());
            }

            // Definir tenant no contexto se válido
            TenantContext.TenantType tenant = TenantContext.TenantType.fromValue(tenantId);
            if (tenant != null) {
                TenantContext.setCurrentTenant(tenant);
                logger.debug("Tenant definido para requisição: {}", tenant.getValue());
            } else {
                // Se não conseguir identificar, usar vermelho como padrão
                TenantContext.setCurrentTenant(TenantContext.TenantType.VERMELHO);
                logger.debug("Tenant não identificado, usando padrão: vermelho");
            }

            chain.doFilter(request, response);

        } finally {
            // Sempre limpar o contexto após a requisição
            TenantContext.clear();
        }
    }

    /**
     * Tenta extrair o tenant da URL
     * Procura por padrões como /api/vermelho/* ou /api/branco/*
     */
    private String extractTenantFromPath(String path) {
        if (path == null) return null;

        // Verificar se a URL contém indicação do caminhão
        if (path.contains("/vermelho/") || path.contains("vermelho")) {
            return "vermelho";
        }
        if (path.contains("/branco/") || path.contains("branco")) {
            return "branco";
        }

        return null;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("TenantFilter inicializado");
    }

    @Override
    public void destroy() {
        logger.info("TenantFilter destruído");
    }
}
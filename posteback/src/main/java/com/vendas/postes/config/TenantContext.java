package com.vendas.postes.config;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TenantContext {

    public enum TenantType {
        VERMELHO("vermelho"),
        BRANCO("branco");

        private final String value;

        TenantType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static TenantType fromValue(String value) {
            if (value == null || value.trim().isEmpty()) {
                log.debug("Valor de tenant nulo ou vazio, usando VERMELHO como padrão");
                return VERMELHO;
            }

            String cleanValue = value.trim().toLowerCase();

            switch (cleanValue) {
                case "branco":
                    return BRANCO;
                case "vermelho":
                    return VERMELHO;
                default:
                    log.warn("Valor de tenant desconhecido '{}', usando VERMELHO como padrão", value);
                    return VERMELHO;
            }
        }

        public static boolean isValid(String value) {
            if (value == null || value.trim().isEmpty()) {
                return false;
            }

            String cleanValue = value.trim().toLowerCase();
            return "vermelho".equals(cleanValue) || "branco".equals(cleanValue);
        }
    }

    private static final ThreadLocal<TenantType> currentTenant = new ThreadLocal<>();
    private static final ThreadLocal<String> originalValue = new ThreadLocal<>();

    public static void setCurrentTenant(TenantType tenant) {
        if (tenant == null) {
            log.warn("Tentativa de definir tenant nulo, usando VERMELHO como padrão");
            tenant = TenantType.VERMELHO;
        }

        TenantType previous = currentTenant.get();
        currentTenant.set(tenant);

        if (previous != null && previous != tenant) {
            log.debug("Tenant alterado de {} para {}", previous.getValue(), tenant.getValue());
        }
    }

    public static void setCurrentTenant(String tenantValue) {
        originalValue.set(tenantValue);
        setCurrentTenant(TenantType.fromValue(tenantValue));
    }

    public static TenantType getCurrentTenant() {
        TenantType tenant = currentTenant.get();
        if (tenant == null) {
            log.debug("Nenhum tenant definido no contexto, usando VERMELHO como padrão");
            tenant = TenantType.VERMELHO;
            currentTenant.set(tenant);
        }
        return tenant;
    }

    public static String getCurrentTenantValue() {
        return getCurrentTenant().getValue();
    }

    public static String getOriginalValue() {
        String original = originalValue.get();
        return original != null ? original : getCurrentTenantValue();
    }

    public static void clear() {
        TenantType current = currentTenant.get();
        if (current != null) {
            log.debug("Limpando contexto do tenant: {}", current.getValue());
        }

        currentTenant.remove();
        originalValue.remove();
    }

    public static boolean isCurrentTenant(String tenantValue) {
        return getCurrentTenantValue().equals(tenantValue);
    }

    public static boolean hasValidTenant() {
        try {
            TenantType tenant = currentTenant.get();
            return tenant != null;
        } catch (Exception e) {
            log.error("Erro ao verificar tenant válido: ", e);
            return false;
        }
    }
}
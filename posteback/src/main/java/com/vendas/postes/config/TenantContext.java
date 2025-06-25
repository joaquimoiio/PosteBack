package com.vendas.postes.config;

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
            if (value == null) return VERMELHO;
            return "branco".equalsIgnoreCase(value) ? BRANCO : VERMELHO;
        }
    }

    private static final ThreadLocal<TenantType> currentTenant = new ThreadLocal<>();

    public static void setCurrentTenant(TenantType tenant) {
        currentTenant.set(tenant);
    }

    public static TenantType getCurrentTenant() {
        TenantType tenant = currentTenant.get();
        return tenant != null ? tenant : TenantType.VERMELHO;
    }

    public static String getCurrentTenantValue() {
        return getCurrentTenant().getValue();
    }

    public static void clear() {
        currentTenant.remove();
    }
}
package com.vendas.postes.config;

/**
 * Contexto para armazenar informações do tenant (caminhão) atual
 * Usado para separar dados entre Caminhão Vermelho e Caminhão Branco
 */
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
            if (value == null) return null;

            for (TenantType type : TenantType.values()) {
                if (type.getValue().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            return null;
        }
    }

    private static final ThreadLocal<TenantType> currentTenant = new ThreadLocal<>();

    public static void setCurrentTenant(TenantType tenant) {
        currentTenant.set(tenant);
    }

    public static TenantType getCurrentTenant() {
        return currentTenant.get();
    }

    public static String getCurrentTenantValue() {
        TenantType tenant = getCurrentTenant();
        return tenant != null ? tenant.getValue() : null;
    }

    public static void clear() {
        currentTenant.remove();
    }

    public static boolean isVermelho() {
        return TenantType.VERMELHO.equals(getCurrentTenant());
    }

    public static boolean isBranco() {
        return TenantType.BRANCO.equals(getCurrentTenant());
    }
}
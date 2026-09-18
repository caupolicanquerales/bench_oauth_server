package com.capo.bench_oauth_server.enums;

public enum RolesEnum {
    ROLE_COACH("ROLE_COACH"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_ATHLETE("ROLE_ATHLETE");

    private final String roleName;

    RolesEnum(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public static RolesEnum fromString(String role) {
        if (role == null) {
            return ROLE_ATHLETE;
        }
        String clean = role.trim().toUpperCase();
        if (!clean.startsWith("ROLE_")) {
            clean = "ROLE_" + clean;
        }
        for (RolesEnum r : values()) {
            if (r.name().equalsIgnoreCase(clean) || r.roleName.equalsIgnoreCase(clean)) {
                return r;
            }
        }
        return ROLE_ATHLETE;
    }
}


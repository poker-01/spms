package com.example.spms.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Set;

/**
 * 登录入口类型
 */
@Getter
public enum LoginType {

    OWNER(Set.of("ROLE_OWNER")),
    PROPERTY(Set.of("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_REPAIR"));

    private final Set<String> allowedRoleCodes;

    LoginType(Set<String> allowedRoleCodes) {
        this.allowedRoleCodes = allowedRoleCodes;
    }

    @JsonCreator
    public static LoginType fromValue(String value) {
        return LoginType.valueOf(value.toUpperCase());
    }

    public boolean matchesAnyRole(Set<String> roleCodes) {
        return roleCodes.stream().anyMatch(allowedRoleCodes::contains);
    }
}

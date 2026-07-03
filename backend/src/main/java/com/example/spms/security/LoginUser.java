package com.example.spms.security;

import com.example.spms.model.po.SysUserInfo;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Getter
public class LoginUser implements UserDetails {

    private final SysUserInfo user;
    private final List<String> roleCodes;
    private final List<String> permissionStrs;

    public LoginUser(SysUserInfo user, List<String> roleCodes, List<String> permissionStrs) {
        this.user = user;
        this.roleCodes = roleCodes;
        this.permissionStrs = permissionStrs;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Stream<SimpleGrantedAuthority> roles = roleCodes.stream().map(SimpleGrantedAuthority::new);
        Stream<SimpleGrantedAuthority> permissions = permissionStrs.stream().map(SimpleGrantedAuthority::new);
        return Stream.concat(roles, permissions).toList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() != null && user.getStatus() == 1;
    }

    public Long getUserId() {
        return user.getId();
    }
}

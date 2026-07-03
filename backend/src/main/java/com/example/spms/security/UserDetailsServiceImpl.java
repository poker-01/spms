package com.example.spms.security;

import com.example.spms.mapper.SysUserInfoMapper;
import com.example.spms.model.po.SysUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserInfoMapper sysUserInfoMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUserInfo user = sysUserInfoMapper.selectByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        List<String> roleCodes = sysUserInfoMapper.selectRoleCodesByUserId(user.getId());
        List<String> permissionStrs = sysUserInfoMapper.selectPermissionStrsByUserId(user.getId());
        return new LoginUser(user, roleCodes, permissionStrs);
    }

    public LoginUser loadUserById(Long userId) {
        SysUserInfo user = sysUserInfoMapper.selectById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + userId);
        }
        List<String> roleCodes = sysUserInfoMapper.selectRoleCodesByUserId(userId);
        List<String> permissionStrs = sysUserInfoMapper.selectPermissionStrsByUserId(userId);
        return new LoginUser(user, roleCodes, permissionStrs);
    }
}

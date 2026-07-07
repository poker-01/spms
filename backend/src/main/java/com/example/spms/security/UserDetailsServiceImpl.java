package com.example.spms.security;

import com.example.spms.mapper.CommunityInfoMapper;
import com.example.spms.mapper.SysUserInfoMapper;
import com.example.spms.model.po.CommunityInfo;
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
    private final CommunityInfoMapper communityInfoMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUserInfo user = sysUserInfoMapper.selectByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        return buildLoginUser(user);
    }

    public LoginUser loadUserById(Long userId) {
        SysUserInfo user = sysUserInfoMapper.selectById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + userId);
        }
        return buildLoginUser(user);
    }

    private LoginUser buildLoginUser(SysUserInfo user) {
        List<String> roleCodes = sysUserInfoMapper.selectRoleCodesByUserId(user.getId());
        List<String> permissionStrs = sysUserInfoMapper.selectPermissionStrsByUserId(user.getId());

        Long communityId = null;
        String communityName = null;
        if (user.getCommunityId() != null) {
            CommunityInfo community = communityInfoMapper.selectById(user.getCommunityId());
            if (community != null && community.getIsDeleted() == 0) {
                communityId = community.getId();
                communityName = community.getCommunityName();
            }
        }

        return new LoginUser(user, roleCodes, permissionStrs, communityId, communityName);
    }
}

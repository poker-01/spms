package com.example.spms.service.impl;

import com.example.spms.enums.ResultCode;
import com.example.spms.exception.CustomException;
import com.example.spms.model.bo.LoginRequest;
import com.example.spms.model.po.SysUserInfo;
import com.example.spms.model.vo.LoginVO;
import com.example.spms.model.vo.MenuVO;
import com.example.spms.model.vo.UserInfoVO;
import com.example.spms.security.JwtUtils;
import com.example.spms.security.LoginUser;
import com.example.spms.security.UserDetailsServiceImpl;
import com.example.spms.service.AuthService;
import com.example.spms.service.SysPermissionInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final SysPermissionInfoService sysPermissionInfoService;

    @Override
    public LoginVO login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
        );
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        if (!request.getLoginType().matchesAnyRole(new HashSet<>(loginUser.getRoleCodes()))) {
            throw new CustomException(ResultCode.ROLE_NOT_MATCH);
        }
        String token = jwtUtils.generateToken(loginUser.getUserId(), loginUser.getUsername());
        return LoginVO.builder()
                .token(token)
                .userInfo(buildUserInfoVO(loginUser))
                .build();
    }

    @Override
    public UserInfoVO getCurrentUserInfo(Long userId) {
        LoginUser loginUser = userDetailsService.loadUserById(userId);
        return buildUserInfoVO(loginUser);
    }

    @Override
    public List<MenuVO> getCurrentUserMenus(Long userId) {
        return sysPermissionInfoService.listCurrentUserMenus(userId);
    }

    private UserInfoVO buildUserInfoVO(LoginUser loginUser) {
        SysUserInfo user = loginUser.getUser();
        return UserInfoVO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .avatarAddress(user.getAvatarAddress())
                .roles(loginUser.getRoleCodes())
                .permissions(loginUser.getPermissionStrs())
                .build();
    }
}

package com.example.spms.service;

import com.example.spms.model.bo.LoginRequest;
import com.example.spms.model.vo.LoginVO;
import com.example.spms.model.vo.MenuVO;
import com.example.spms.model.vo.UserInfoVO;

import java.util.List;

public interface AuthService {

    LoginVO login(LoginRequest request);

    UserInfoVO getCurrentUserInfo(Long userId);

    List<MenuVO> getCurrentUserMenus(Long userId);
}

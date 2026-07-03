package com.example.spms.controller;

import com.example.spms.common.Result;
import com.example.spms.model.bo.LoginRequest;
import com.example.spms.model.vo.LoginVO;
import com.example.spms.model.vo.MenuVO;
import com.example.spms.model.vo.UserInfoVO;
import com.example.spms.security.LoginUser;
import com.example.spms.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 认证控制器
 *
 * @Author SPMS
 * @Date 2026/07/03
 */
@Tag(name = "认证管理", description = "用户登录、登出等接口")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     *
     * @param request 登录请求参数
     * @return 登录结果（含 token 和用户信息）
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    /**
     * 获取当前登录用户信息
     *
     * @param loginUser 当前登录用户
     * @return 用户信息（含角色和权限）
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo(@AuthenticationPrincipal LoginUser loginUser) {
        return Result.success(authService.getCurrentUserInfo(loginUser.getUserId()));
    }

    /**
     * 获取当前登录用户菜单
     *
     * @param loginUser 当前登录用户
     * @return 菜单树
     */
    @Operation(summary = "获取当前登录用户菜单")
    @GetMapping("/menus")
    public Result<List<MenuVO>> getMenus(@AuthenticationPrincipal LoginUser loginUser) {
        return Result.success(authService.getCurrentUserMenus(loginUser.getUserId()));
    }

    /**
     * 退出登录
     *
     * @return 操作结果
     */
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
}

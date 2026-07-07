package com.example.spms.common;

import com.example.spms.security.LoginUser;

import java.util.List;

/**
 * 小区数据隔离工具类
 * <p>
 * 非超级管理员用户只能查看/操作自己所属小区的数据。
 * 超级管理员可以查看所有小区数据。
 */
public final class CommunityFilterHelper {

    private static final String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";

    private CommunityFilterHelper() {
    }

    /**
     * 获取当前用户的小区ID（用于数据过滤）。
     * 如果是超级管理员，返回 null（不过滤）；否则返回用户绑定的小区ID。
     *
     * @param loginUser 当前登录用户
     * @return 小区ID，超级管理员返回 null
     */
    public static Long getCommunityId(LoginUser loginUser) {
        if (loginUser == null) {
            return null;
        }
        List<String> roles = loginUser.getRoleCodes();
        if (roles != null && roles.contains(ROLE_SUPER_ADMIN)) {
            return null; // 超级管理员，不过滤
        }
        return loginUser.getCommunityId();
    }

    /**
     * 判断当前用户是否为超级管理员
     */
    public static boolean isSuperAdmin(LoginUser loginUser) {
        if (loginUser == null) {
            return false;
        }
        List<String> roles = loginUser.getRoleCodes();
        return roles != null && roles.contains(ROLE_SUPER_ADMIN);
    }
}

package com.example.spms.enums;

import lombok.Getter;

/**
 * 统一响应状态码
 *
 * @Author SPMS
 * @Date 2026/07/03
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "success"),
    FAIL(500, "操作失败"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有访问权限"),
    NOT_FOUND(404, "资源不存在"),
    BAD_REQUEST(400, "请求参数错误"),
    USERNAME_EXISTS(1001, "用户名已存在"),
    USER_NOT_FOUND(1002, "用户不存在"),
    PASSWORD_ERROR(1003, "用户名或密码错误"),
    USER_DISABLED(1004, "账号已被禁用"),
    ROLE_NOT_MATCH(1005, "当前账号无权从此入口登录"),
    OWNER_NOT_FOUND(1006, "关联的业主不存在");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

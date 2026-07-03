package com.example.spms.model.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增用户请求
 */
@Data
public class UserSaveRequest {

    @NotBlank(message = "用户名不能为空")
    private String userName;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String fullName;

    private String phoneNumber;

    private String email;

    @NotNull(message = "状态不能为空")
    private Integer status;
}

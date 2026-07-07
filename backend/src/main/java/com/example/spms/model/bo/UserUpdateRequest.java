package com.example.spms.model.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改用户请求
 */
@Data
public class UserUpdateRequest {

    @NotNull(message = "用户ID不能为空")
    private Long id;

    private String fullName;

    private String phoneNumber;

    private String email;

    private Integer status;

    /** 所属小区ID（管理员绑定小区，超级管理员不需要） */
    private Long communityId;
}

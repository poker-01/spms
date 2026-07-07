package com.example.spms.model.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

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

    /** 关联业主ID（非维修人员角色必填） */
    private Long ownerId;

    /** 角色ID列表（创建时直接分配角色） */
    private List<Long> roleIds;

    /** 所属小区ID（管理员绑定小区，超级管理员不需要） */
    private Long communityId;
}

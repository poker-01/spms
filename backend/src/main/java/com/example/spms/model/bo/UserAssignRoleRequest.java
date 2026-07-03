package com.example.spms.model.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 用户角色分配请求
 */
@Data
public class UserAssignRoleRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "角色ID列表不能为空")
    private List<Long> roleIds;
}

package com.example.spms.model.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OwnerSaveRequest {

    @NotBlank(message = "业主姓名不能为空")
    private String ownerName;

    @NotBlank(message = "业主电话不能为空")
    private String ownerPhone;

    private String idCard;
    private Integer gender;
    private String email;
    private Integer status;

    @NotNull(message = "关联房屋不能为空")
    private Long houseId;

    private Integer hasParking;
}
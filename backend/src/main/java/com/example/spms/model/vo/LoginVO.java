package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginVO {

    private String token;
    private UserInfoVO userInfo;
}

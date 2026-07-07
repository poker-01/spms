package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserInfoVO {

    private Long id;
    private String userName;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String avatarAddress;
    private List<String> roles;
    private List<String> permissions;
    private Long communityId;
    private String communityName;
}

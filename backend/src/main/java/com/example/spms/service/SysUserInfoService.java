package com.example.spms.service;

import com.example.spms.common.Page;
import com.example.spms.model.bo.UserAssignRoleRequest;
import com.example.spms.model.bo.UserQueryRequest;
import com.example.spms.model.bo.UserSaveRequest;
import com.example.spms.model.bo.UserStatusRequest;
import com.example.spms.model.bo.UserUpdateRequest;
import com.example.spms.model.po.SysUserInfo;
import com.example.spms.model.vo.UserDetailVO;
import com.example.spms.model.vo.UserPageVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author poker
* @description 针对表【sys_user_info(用户信息表)】的数据库操作Service
* @createDate 2026-07-03 15:19:38
*/
public interface SysUserInfoService extends IService<SysUserInfo> {

    Page<UserPageVO> pageUsers(UserQueryRequest request);

    void saveUser(UserSaveRequest request);

    void updateUser(UserUpdateRequest request);

    void toggleStatus(UserStatusRequest request);

    void resetPassword(Long userId);

    void assignRoles(UserAssignRoleRequest request);

    List<Long> getUserRoleIds(Long userId);

    /**
     * 根据角色ID查询用户列表（不分页，用于下拉选择）
     */
    List<UserPageVO> listByRoleId(Long roleId);

    /**
     * 根据角色ID和小区ID查询用户列表（不分页，用于下拉选择，支持小区数据隔离）
     */
    List<UserPageVO> listByRoleIdAndCommunityId(Long roleId, Long communityId);

    /**
     * 获取用户详情
     */
    UserDetailVO getUserDetail(Long userId);
}

package com.example.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spms.common.Page;
import com.example.spms.enums.ResultCode;
import com.example.spms.exception.CustomException;
import com.example.spms.mapper.SysRoleInfoMapper;
import com.example.spms.mapper.SysUserRoleMapper;
import com.example.spms.model.bo.UserAssignRoleRequest;
import com.example.spms.model.bo.UserQueryRequest;
import com.example.spms.model.bo.UserSaveRequest;
import com.example.spms.model.bo.UserStatusRequest;
import com.example.spms.model.bo.UserUpdateRequest;
import com.example.spms.model.po.SysRoleInfo;
import com.example.spms.model.po.SysUserInfo;
import com.example.spms.model.po.SysUserRole;
import com.example.spms.model.po.OwnerInfo;
import com.example.spms.model.vo.UserDetailVO;
import com.example.spms.model.vo.UserPageVO;
import com.example.spms.service.SysUserInfoService;
import com.example.spms.mapper.SysUserInfoMapper;
import com.example.spms.mapper.OwnerInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author poker
* @description 针对表【sys_user_info(用户信息表)】的数据库操作Service实现
* @createDate 2026-07-03 15:19:38
*/
@Service
@RequiredArgsConstructor
public class SysUserInfoServiceImpl extends ServiceImpl<SysUserInfoMapper, SysUserInfo>
    implements SysUserInfoService {

    private static final String DEFAULT_PASSWORD = "123456";
    private static final Long REPAIR_ROLE_ID = 4L;

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleInfoMapper sysRoleInfoMapper;
    private final OwnerInfoMapper ownerInfoMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UserPageVO> pageUsers(UserQueryRequest request) {
        // 如果指定了roleId，先查出该角色下的用户ID
        List<Long> roleUserIds = getUserIdsByRoleId(request.getRoleId());
        if (request.getRoleId() != null && roleUserIds.isEmpty()) {
            return Page.empty(request.getPageNum(), request.getPageSize());
        }

        LambdaQueryWrapper<SysUserInfo> wrapper = buildQueryWrapper(request, roleUserIds);
        long total = baseMapper.selectCount(wrapper);
        long size = request.getPageSize();
        long current = request.getPageNum();
        long offset = (current - 1) * size;

        List<SysUserInfo> records = List.of();
        if (total > 0) {
            LambdaQueryWrapper<SysUserInfo> pageWrapper = buildQueryWrapper(request, roleUserIds);
            pageWrapper.last("LIMIT " + offset + ", " + size);
            records = baseMapper.selectList(pageWrapper);
        }

        List<Long> userIds = records.stream().map(SysUserInfo::getId).toList();
        Map<Long, List<String>> roleNameMap = queryRoleNamesByUserIds(userIds);

        List<UserPageVO> list = records.stream().map(user -> UserPageVO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .status(user.getStatus())
                .createTime(user.getCreateTime())
                .roleNames(roleNameMap.getOrDefault(user.getId(), Collections.emptyList()))
                .build()).toList();

        Page<UserPageVO> result = new Page<>();
        result.setTotal(total);
        result.setPages(total == 0 ? 0 : (total + size - 1) / size);
        result.setCurrent(current);
        result.setSize(size);
        result.setRecords(list);
        return result;
    }

    private LambdaQueryWrapper<SysUserInfo> buildQueryWrapper(UserQueryRequest request, List<Long> roleUserIds) {
        LambdaQueryWrapper<SysUserInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysUserInfo::getIsDeleted, 0);
        if (StringUtils.hasText(request.getUserName())) {
            wrapper.like(SysUserInfo::getUserName, request.getUserName());
        }
        if (StringUtils.hasText(request.getFullName())) {
            wrapper.like(SysUserInfo::getFullName, request.getFullName());
        }
        if (request.getStatus() != null) {
            wrapper.eq(SysUserInfo::getStatus, request.getStatus());
        }
        if (roleUserIds != null && !roleUserIds.isEmpty()) {
            wrapper.in(SysUserInfo::getId, roleUserIds);
        }
        wrapper.orderByDesc(SysUserInfo::getCreateTime);
        return wrapper;
    }

    /**
     * 根据角色ID获取用户ID列表
     */
    private List<Long> getUserIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return null;
        }
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                Wrappers.<SysUserRole>lambdaQuery()
                        .eq(SysUserRole::getRoleInfoId, roleId)
                        .eq(SysUserRole::getIsDeleted, 0));
        return userRoles.stream().map(SysUserRole::getUserInfoId).toList();
    }

    private Map<Long, List<String>> queryRoleNamesByUserIds(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                Wrappers.<SysUserRole>lambdaQuery()
                        .in(SysUserRole::getUserInfoId, userIds)
                        .eq(SysUserRole::getIsDeleted, 0));
        Set<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleInfoId).collect(Collectors.toSet());
        Map<Long, String> roleNameMap = sysRoleInfoMapper.selectBatchIds(roleIds).stream()
                .collect(Collectors.toMap(SysRoleInfo::getId, SysRoleInfo::getRoleName));
        return userRoles.stream().collect(Collectors.groupingBy(
                SysUserRole::getUserInfoId,
                Collectors.mapping(ur -> roleNameMap.getOrDefault(ur.getRoleInfoId(), ""), Collectors.toList())));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(UserSaveRequest request) {
        SysUserInfo exist = baseMapper.selectByUserName(request.getUserName());
        if (exist != null) {
            throw new CustomException(ResultCode.USERNAME_EXISTS);
        }

        boolean isRepairRole = !CollectionUtils.isEmpty(request.getRoleIds())
                && request.getRoleIds().contains(REPAIR_ROLE_ID);

        if (!isRepairRole) {
            if (request.getOwnerId() == null) {
                throw new CustomException(ResultCode.BAD_REQUEST.getCode(), "非维修人员角色必须关联业主");
            }
            OwnerInfo owner = ownerInfoMapper.selectById(request.getOwnerId());
            if (owner == null || owner.getIsDeleted() == 1) {
                throw new CustomException(ResultCode.OWNER_NOT_FOUND);
            }
        }

        SysUserInfo user = new SysUserInfo();
        user.setUserName(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus());
        baseMapper.insert(user);

        // 如果指定了角色，直接分配角色
        if (!CollectionUtils.isEmpty(request.getRoleIds())) {
            for (Long roleId : request.getRoleIds()) {
                SysUserRole relation = new SysUserRole();
                relation.setUserInfoId(user.getId());
                relation.setRoleInfoId(roleId);
                sysUserRoleMapper.insert(relation);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserUpdateRequest request) {
        SysUserInfo user = baseMapper.selectById(request.getId());
        if (user == null || user.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.USER_NOT_FOUND);
        }
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        baseMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleStatus(UserStatusRequest request) {
        SysUserInfo user = baseMapper.selectById(request.getId());
        if (user == null || user.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.USER_NOT_FOUND);
        }
        user.setStatus(request.getStatus());
        baseMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId) {
        SysUserInfo user = baseMapper.selectById(userId);
        if (user == null || user.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.USER_NOT_FOUND);
        }
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        baseMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(UserAssignRoleRequest request) {
        SysUserInfo user = baseMapper.selectById(request.getUserId());
        if (user == null || user.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.USER_NOT_FOUND);
        }
        sysUserRoleMapper.update(null, Wrappers.<SysUserRole>lambdaUpdate()
                .eq(SysUserRole::getUserInfoId, request.getUserId())
                .set(SysUserRole::getIsDeleted, 1));
        for (Long roleId : request.getRoleIds()) {
            SysUserRole relation = new SysUserRole();
            relation.setUserInfoId(request.getUserId());
            relation.setRoleInfoId(roleId);
            sysUserRoleMapper.insert(relation);
        }
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        List<SysUserRole> list = sysUserRoleMapper.selectList(
                Wrappers.<SysUserRole>lambdaQuery()
                        .eq(SysUserRole::getUserInfoId, userId)
                        .eq(SysUserRole::getIsDeleted, 0));
        return list.stream().map(SysUserRole::getRoleInfoId).toList();
    }

    @Override
    public List<UserPageVO> listByRoleId(Long roleId) {
        List<Long> userIds = getUserIdsByRoleId(roleId);
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<SysUserInfo> users = baseMapper.selectBatchIds(userIds);
        Map<Long, List<String>> roleNameMap = queryRoleNamesByUserIds(userIds);
        return users.stream()
                .filter(u -> u.getIsDeleted() == 0)
                .map(user -> UserPageVO.builder()
                        .id(user.getId())
                        .userName(user.getUserName())
                        .fullName(user.getFullName())
                        .phoneNumber(user.getPhoneNumber())
                        .email(user.getEmail())
                        .status(user.getStatus())
                        .createTime(user.getCreateTime())
                        .roleNames(roleNameMap.getOrDefault(user.getId(), Collections.emptyList()))
                        .build()).toList();
    }

    @Override
    public UserDetailVO getUserDetail(Long userId) {
        SysUserInfo user = baseMapper.selectById(userId);
        if (user == null || user.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.USER_NOT_FOUND);
        }
        List<Long> roleIds = getUserRoleIds(userId);
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                Wrappers.<SysUserRole>lambdaQuery()
                        .eq(SysUserRole::getUserInfoId, userId)
                        .eq(SysUserRole::getIsDeleted, 0));
        Set<Long> rIds = userRoles.stream().map(SysUserRole::getRoleInfoId).collect(Collectors.toSet());
        List<String> roleNames = Collections.emptyList();
        if (!rIds.isEmpty()) {
            roleNames = sysRoleInfoMapper.selectBatchIds(rIds).stream()
                    .map(SysRoleInfo::getRoleName).toList();
        }
        return UserDetailVO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .avatarAddress(user.getAvatarAddress())
                .status(user.getStatus())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .roleIds(roleIds)
                .roleNames(roleNames)
                .build();
    }
}

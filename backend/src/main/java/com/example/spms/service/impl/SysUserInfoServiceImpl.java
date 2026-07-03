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
import com.example.spms.model.vo.UserPageVO;
import com.example.spms.service.SysUserInfoService;
import com.example.spms.mapper.SysUserInfoMapper;
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

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleInfoMapper sysRoleInfoMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UserPageVO> pageUsers(UserQueryRequest request) {
        LambdaQueryWrapper<SysUserInfo> wrapper = buildQueryWrapper(request);
        long total = baseMapper.selectCount(wrapper);
        long size = request.getPageSize();
        long current = request.getPageNum();
        long offset = (current - 1) * size;

        List<SysUserInfo> records = List.of();
        if (total > 0) {
            LambdaQueryWrapper<SysUserInfo> pageWrapper = buildQueryWrapper(request);
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

    private LambdaQueryWrapper<SysUserInfo> buildQueryWrapper(UserQueryRequest request) {
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
        wrapper.orderByDesc(SysUserInfo::getCreateTime);
        return wrapper;
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
        SysUserInfo user = new SysUserInfo();
        user.setUserName(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus());
        baseMapper.insert(user);
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
}

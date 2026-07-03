package com.example.spms.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spms.enums.ResultCode;
import com.example.spms.exception.CustomException;
import com.example.spms.mapper.SysRolePermissionMapper;
import com.example.spms.model.bo.RoleAssignPermissionRequest;
import com.example.spms.model.bo.RoleSaveRequest;
import com.example.spms.model.bo.RoleUpdateRequest;
import com.example.spms.model.po.SysRoleInfo;
import com.example.spms.model.po.SysRolePermission;
import com.example.spms.service.SysRoleInfoService;
import com.example.spms.mapper.SysRoleInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author poker
* @description 针对表【sys_role_info(角色信息表)】的数据库操作Service实现
* @createDate 2026-07-03 15:19:38
*/
@Service
@RequiredArgsConstructor
public class SysRoleInfoServiceImpl extends ServiceImpl<SysRoleInfoMapper, SysRoleInfo>
    implements SysRoleInfoService {

    private final SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public List<SysRoleInfo> listAll() {
        return baseMapper.selectList(Wrappers.<SysRoleInfo>lambdaQuery()
                .eq(SysRoleInfo::getIsDeleted, 0)
                .orderByAsc(SysRoleInfo::getCreateTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(RoleSaveRequest request) {
        SysRoleInfo exist = baseMapper.selectOne(Wrappers.<SysRoleInfo>lambdaQuery()
                .eq(SysRoleInfo::getRoleCode, request.getRoleCode())
                .eq(SysRoleInfo::getIsDeleted, 0));
        if (exist != null) {
            throw new CustomException(ResultCode.FAIL, "角色编码已存在");
        }
        SysRoleInfo role = new SysRoleInfo();
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setRoleType(2);
        baseMapper.insert(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleUpdateRequest request) {
        SysRoleInfo role = baseMapper.selectById(request.getId());
        if (role == null || role.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND);
        }
        if (request.getRoleCode() != null) {
            role.setRoleCode(request.getRoleCode());
        }
        if (request.getRoleName() != null) {
            role.setRoleName(request.getRoleName());
        }
        baseMapper.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(RoleAssignPermissionRequest request) {
        SysRoleInfo role = baseMapper.selectById(request.getRoleId());
        if (role == null || role.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND);
        }
        sysRolePermissionMapper.update(null, Wrappers.<SysRolePermission>lambdaUpdate()
                .eq(SysRolePermission::getRoleInfoId, request.getRoleId())
                .set(SysRolePermission::getIsDeleted, 1));
        for (Long permissionId : request.getPermissionIds()) {
            SysRolePermission relation = new SysRolePermission();
            relation.setRoleInfoId(request.getRoleId());
            relation.setPermissionInfoId(permissionId);
            sysRolePermissionMapper.insert(relation);
        }
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        List<SysRolePermission> list = sysRolePermissionMapper.selectList(
                Wrappers.<SysRolePermission>lambdaQuery()
                        .eq(SysRolePermission::getRoleInfoId, roleId)
                        .eq(SysRolePermission::getIsDeleted, 0));
        return list.stream().map(SysRolePermission::getPermissionInfoId).toList();
    }
}

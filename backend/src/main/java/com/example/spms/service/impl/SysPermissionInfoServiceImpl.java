package com.example.spms.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spms.enums.ResultCode;
import com.example.spms.exception.CustomException;
import com.example.spms.mapper.SysUserInfoMapper;
import com.example.spms.model.bo.PermissionSaveRequest;
import com.example.spms.model.bo.PermissionUpdateRequest;
import com.example.spms.model.po.SysPermissionInfo;
import com.example.spms.model.vo.MenuVO;
import com.example.spms.model.vo.PermissionTreeVO;
import com.example.spms.service.SysPermissionInfoService;
import com.example.spms.mapper.SysPermissionInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @author poker
* @description 针对表【sys_permission_info(权限信息表(菜单+按钮))】的数据库操作Service实现
* @createDate 2026-07-03 15:19:38
*/
@Service
@RequiredArgsConstructor
public class SysPermissionInfoServiceImpl extends ServiceImpl<SysPermissionInfoMapper, SysPermissionInfo>
    implements SysPermissionInfoService {

    private static final int PERMISSION_TYPE_MENU = 1;

    private final SysUserInfoMapper sysUserInfoMapper;

    @Override
    public List<PermissionTreeVO> tree() {
        List<SysPermissionInfo> all = baseMapper.selectList(
                Wrappers.<SysPermissionInfo>lambdaQuery()
                        .eq(SysPermissionInfo::getIsDeleted, 0)
                        .orderByAsc(SysPermissionInfo::getSortOrder));
        return buildPermissionTree(all, 0L);
    }

    @Override
    public List<MenuVO> listCurrentUserMenus(Long userId) {
        List<SysPermissionInfo> menus = sysUserInfoMapper.selectPermissionsByUserId(userId);
        return buildMenuTree(menus, 0L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePermission(PermissionSaveRequest request) {
        SysPermissionInfo permission = new SysPermissionInfo();
        permission.setParentId(request.getParentId());
        permission.setPermissionCode(request.getPermissionCode());
        permission.setPermissionName(request.getPermissionName());
        permission.setPermissionType(request.getPermissionType());
        permission.setPermissionIcon(request.getPermissionIcon());
        permission.setPermissionPath(request.getPermissionPath());
        permission.setPermissionComponent(request.getPermissionComponent());
        permission.setPermissionStr(request.getPermissionStr());
        permission.setSortOrder(request.getSortOrder());
        permission.setVisible(request.getVisible());
        baseMapper.insert(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermission(PermissionUpdateRequest request) {
        SysPermissionInfo permission = baseMapper.selectById(request.getId());
        if (permission == null || permission.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND);
        }
        if (request.getParentId() != null) {
            permission.setParentId(request.getParentId());
        }
        if (request.getPermissionCode() != null) {
            permission.setPermissionCode(request.getPermissionCode());
        }
        if (request.getPermissionName() != null) {
            permission.setPermissionName(request.getPermissionName());
        }
        if (request.getPermissionType() != null) {
            permission.setPermissionType(request.getPermissionType());
        }
        if (request.getPermissionIcon() != null) {
            permission.setPermissionIcon(request.getPermissionIcon());
        }
        if (request.getPermissionPath() != null) {
            permission.setPermissionPath(request.getPermissionPath());
        }
        if (request.getPermissionComponent() != null) {
            permission.setPermissionComponent(request.getPermissionComponent());
        }
        if (request.getPermissionStr() != null) {
            permission.setPermissionStr(request.getPermissionStr());
        }
        if (request.getSortOrder() != null) {
            permission.setSortOrder(request.getSortOrder());
        }
        if (request.getVisible() != null) {
            permission.setVisible(request.getVisible());
        }
        baseMapper.updateById(permission);
    }

    private List<PermissionTreeVO> buildPermissionTree(List<SysPermissionInfo> all, Long parentId) {
        Map<Long, List<SysPermissionInfo>> grouped = all.stream()
                .collect(Collectors.groupingBy(p -> p.getParentId() == null ? 0L : p.getParentId()));
        return buildChildren(grouped, parentId);
    }

    private List<PermissionTreeVO> buildChildren(Map<Long, List<SysPermissionInfo>> grouped, Long parentId) {
        return grouped.getOrDefault(parentId, List.of()).stream()
                .sorted(Comparator.comparing(SysPermissionInfo::getSortOrder,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(p -> PermissionTreeVO.builder()
                        .id(p.getId())
                        .parentId(p.getParentId())
                        .permissionCode(p.getPermissionCode())
                        .permissionName(p.getPermissionName())
                        .permissionType(p.getPermissionType())
                        .permissionIcon(p.getPermissionIcon())
                        .permissionPath(p.getPermissionPath())
                        .permissionComponent(p.getPermissionComponent())
                        .permissionStr(p.getPermissionStr())
                        .sortOrder(p.getSortOrder())
                        .visible(p.getVisible())
                        .children(buildChildren(grouped, p.getId()))
                        .build())
                .toList();
    }

    private List<MenuVO> buildMenuTree(List<SysPermissionInfo> menus, Long parentId) {
        Map<Long, List<SysPermissionInfo>> grouped = menus.stream()
                .filter(p -> Objects.equals(p.getPermissionType(), PERMISSION_TYPE_MENU))
                .filter(p -> p.getVisible() == null || p.getVisible() == 1)
                .collect(Collectors.groupingBy(p -> p.getParentId() == null ? 0L : p.getParentId()));
        return buildMenuChildren(grouped, parentId);
    }

    private List<MenuVO> buildMenuChildren(Map<Long, List<SysPermissionInfo>> grouped, Long parentId) {
        return grouped.getOrDefault(parentId, List.of()).stream()
                .sorted(Comparator.comparing(SysPermissionInfo::getSortOrder,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(p -> MenuVO.builder()
                        .id(p.getId())
                        .parentId(p.getParentId())
                        .name(p.getPermissionName())
                        .path(p.getPermissionPath())
                        .component(p.getPermissionComponent())
                        .icon(p.getPermissionIcon())
                        .sortOrder(p.getSortOrder())
                        .children(buildMenuChildren(grouped, p.getId()))
                        .build())
                .toList();
    }
}

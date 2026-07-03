package com.example.spms.service;

import com.example.spms.model.bo.PermissionSaveRequest;
import com.example.spms.model.bo.PermissionUpdateRequest;
import com.example.spms.model.po.SysPermissionInfo;
import com.example.spms.model.vo.MenuVO;
import com.example.spms.model.vo.PermissionTreeVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author poker
* @description 针对表【sys_permission_info(权限信息表(菜单+按钮))】的数据库操作Service
* @createDate 2026-07-03 15:19:38
*/
public interface SysPermissionInfoService extends IService<SysPermissionInfo> {

    List<PermissionTreeVO> tree();

    List<MenuVO> listCurrentUserMenus(Long userId);

    void savePermission(PermissionSaveRequest request);

    void updatePermission(PermissionUpdateRequest request);
}

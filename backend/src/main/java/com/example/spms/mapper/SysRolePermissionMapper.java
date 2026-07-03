package com.example.spms.mapper;

import com.example.spms.model.po.SysRolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author poker
* @description 针对表【sys_role_permission(角色权限关联表)】的数据库操作Mapper
* @createDate 2026-07-03 15:19:38
* @Entity com.example.spms.model.po.SysRolePermission
*/
@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

}





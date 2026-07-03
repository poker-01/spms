package com.example.spms.mapper;

import com.example.spms.model.po.SysPermissionInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author poker
* @description 针对表【sys_permission_info(权限信息表(菜单+按钮))】的数据库操作Mapper
* @createDate 2026-07-03 15:19:38
* @Entity com.example.spms.model.po.SysPermissionInfo
*/
@Mapper
public interface SysPermissionInfoMapper extends BaseMapper<SysPermissionInfo> {

}





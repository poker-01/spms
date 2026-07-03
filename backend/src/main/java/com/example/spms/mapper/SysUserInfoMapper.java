package com.example.spms.mapper;

import com.example.spms.model.po.SysPermissionInfo;
import com.example.spms.model.po.SysUserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author poker
* @description 针对表【sys_user_info(用户信息表)】的数据库操作Mapper
* @createDate 2026-07-03 15:19:38
* @Entity com.example.spms.model.po.SysUserInfo
*/
@Mapper
public interface SysUserInfoMapper extends BaseMapper<SysUserInfo> {

    SysUserInfo selectByUserName(@Param("userName") String userName);

    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    List<String> selectPermissionStrsByUserId(@Param("userId") Long userId);

    List<SysPermissionInfo> selectPermissionsByUserId(@Param("userId") Long userId);
}





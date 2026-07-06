package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 当前用户菜单视图
 */
@Data
@Builder
public class MenuVO {

    private Long id;

    private Long parentId;

    private String name;

    private String path;

    private String component;

    private String icon;

    private String permissionCode;

    private Integer sortOrder;

    private List<MenuVO> children;
}

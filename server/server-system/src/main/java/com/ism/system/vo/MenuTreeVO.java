package com.ism.system.vo;

import com.ism.common.entity.Menu;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class MenuTreeVO {
    private Long id;
    private String menuName;
    private String menuType;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private Integer sort;
    private Integer visible;
    private Integer status;
    private LocalDateTime createTime;
    private List<MenuTreeVO> children;

    public static MenuTreeVO from(Menu menu) {
        MenuTreeVO vo = new MenuTreeVO();
        vo.setId(menu.getId());
        vo.setMenuName(menu.getMenuName());
        vo.setMenuType(menu.getMenuType());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setPerms(menu.getPerms());
        vo.setIcon(menu.getIcon());
        vo.setSort(menu.getSort());
        vo.setVisible(menu.getVisible());
        vo.setStatus(menu.getStatus());
        vo.setCreateTime(menu.getCreateTime());
        vo.setChildren(new ArrayList<>());
        return vo;
    }
}
package com.ism.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ism.common.entity.Menu;
import com.ism.system.mapper.MenuMapper;
import com.ism.system.service.MenuService;
import com.ism.system.vo.MenuTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<Menu> buildTree() {
        List<Menu> allMenus = this.list(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getStatus, 1)
                .orderByAsc(Menu::getSort));
        return allMenus;
    }

    public List<MenuTreeVO> buildTreeVO() {
        List<Menu> allMenus = this.list(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getStatus, 1)
                .orderByAsc(Menu::getSort));
        
        Map<Long, List<MenuTreeVO>> childrenMap = new HashMap<>();
        List<MenuTreeVO> rootList = new ArrayList<>();
        
        for (Menu menu : allMenus) {
            MenuTreeVO vo = MenuTreeVO.from(menu);
            if (menu.getParentId() == 0) {
                rootList.add(vo);
            } else {
                childrenMap.computeIfAbsent(menu.getParentId(), k -> new ArrayList<>()).add(vo);
            }
        }
        
        for (MenuTreeVO root : rootList) {
            buildChildren(root, childrenMap);
        }
        
        return rootList;
    }

    private void buildChildren(MenuTreeVO parent, Map<Long, List<MenuTreeVO>> childrenMap) {
        List<MenuTreeVO> children = childrenMap.get(parent.getId());
        if (children != null && !children.isEmpty()) {
            parent.setChildren(children);
            for (MenuTreeVO child : children) {
                buildChildren(child, childrenMap);
            }
        }
    }

    @Override
    public List<String> getPermsByUserId(Long userId) {
        return baseMapper.getPermsByUserId(userId);
    }

    @Override
    public List<Menu> getByRoleId(Long roleId) {
        return baseMapper.getMenusByRoleId(roleId);
    }

    @Override
    public void saveMenu(Menu menu) {
        this.save(menu);
    }

    @Override
    public void updateMenu(Menu menu) {
        this.updateById(menu);
    }

    @Override
    public void deleteMenu(Long id) {
        // 检查是否有子菜单
        long count = this.count(new LambdaQueryWrapper<Menu>().eq(Menu::getParentId, id));
        if (count > 0) {
            throw new com.ism.common.exception.BusinessException("存在子菜单，无法删除");
        }
        this.removeById(id);
    }
}

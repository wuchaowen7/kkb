package com.ism.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ism.common.entity.Menu;
import com.ism.system.vo.MenuTreeVO;
import java.util.List;

public interface MenuService extends IService<Menu> {
    List<Menu> buildTree();
    List<MenuTreeVO> buildTreeVO();
    List<String> getPermsByUserId(Long userId);
    List<Menu> getByRoleId(Long roleId);
    void saveMenu(Menu menu);
    void updateMenu(Menu menu);
    void deleteMenu(Long id);
}

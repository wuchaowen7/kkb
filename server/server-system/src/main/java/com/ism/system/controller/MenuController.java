package com.ism.system.controller;

import com.ism.common.entity.Menu;
import com.ism.common.vo.Result;
import com.ism.system.service.MenuService;
import com.ism.system.vo.MenuTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/tree")
    public Result<List<MenuTreeVO>> tree() {
        return Result.ok(menuService.buildTreeVO());
    }

    @GetMapping("/{id}")
    public Result<Menu> getById(@PathVariable Long id) {
        return Result.ok(menuService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Menu menu) {
        menuService.saveMenu(menu);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Menu menu) {
        menu.setId(id);
        menuService.updateMenu(menu);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.ok();
    }
}

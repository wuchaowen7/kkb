package com.ism.system.controller;

import com.ism.common.entity.Menu;
import com.ism.common.entity.Role;
import com.ism.common.vo.Result;
import com.ism.system.service.MenuService;
import com.ism.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final MenuService menuService;

    @GetMapping("/list")
    public Result<List<Role>> list() {
        return Result.ok(roleService.list());
    }

    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        return Result.ok(roleService.getById(id));
    }

    @GetMapping("/{id}/menus")
    public Result<List<Menu>> getRoleMenus(@PathVariable Long id) {
        return Result.ok(menuService.getByRoleId(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Role role) {
        roleService.save(role);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        roleService.updateById(role);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable String ids) {
        for (String id : ids.split(",")) {
            roleService.removeById(Long.parseLong(id));
        }
        return Result.ok();
    }

    @PutMapping("/{roleId}/menus")
    public Result<Void> assignMenus(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(roleId, menuIds);
        return Result.ok();
    }
}

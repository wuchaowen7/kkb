package com.ism.system.controller;

import com.ism.common.annotation.OperationLog;
import com.ism.common.entity.User;
import com.ism.common.vo.PageResult;
import com.ism.common.vo.Result;
import com.ism.system.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/page")
    public Result<PageResult<User>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<User> page = userService.pageQuery(pageNum, pageSize, keyword);
        return Result.ok(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @GetMapping("/{id}/roles")
    public Result<List<Long>> getUserRoles(@PathVariable Long id) {
        return Result.ok(userService.getRoleIdsByUserId(id));
    }

    @PostMapping
    @OperationLog(title = "新增用户", businessType = "USER_ADD")
    public Result<Void> add(@Valid @RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @OperationLog(title = "修改用户", businessType = "USER_EDIT")
    public Result<Void> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.updateById(user);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    @OperationLog(title = "删除用户", businessType = "USER_DELETE")
    public Result<Void> delete(@PathVariable String ids) {
        String[] idArr = ids.split(",");
        for (String id : idArr) {
            userService.removeById(Long.parseLong(id));
        }
        return Result.ok();
    }

    @PutMapping("/{id}/roles")
    @OperationLog(title = "分配角色", businessType = "USER_ASSIGN_ROLE")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.ok();
    }

    @PutMapping("/{id}/resetPwd")
    @OperationLog(title = "重置密码", businessType = "USER_RESET_PWD")
    public Result<Void> resetPwd(@PathVariable Long id, @RequestBody java.util.Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        User user = userService.getById(id);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.fail(400, "原密码不正确");
        }
        
        userService.resetPassword(id, passwordEncoder.encode(newPassword));
        return Result.ok();
    }
}

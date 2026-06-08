package com.ism.system.controller;

import com.ism.common.dto.LoginDTO;
import com.ism.common.entity.User;
import com.ism.common.util.JwtUtil;
import com.ism.common.vo.LoginVO;
import com.ism.common.vo.Result;
import com.ism.system.service.UserService;
import com.ism.system.service.RoleService;
import com.ism.system.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RoleService roleService;
    private final MenuService menuService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        User user = userService.getByUsername(loginDTO.getUsername());
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return Result.fail(401, "用户名或密码错误");
        }
        if (user.getStatus() != 1) {
            return Result.fail(403, "账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        List<String> roles = roleService.getRoleCodesByUserId(user.getId());
        List<String> permissions = menuService.getPermsByUserId(user.getId());

        LoginVO loginVO = LoginVO.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .department(user.getDepartment())
                .avatar(user.getAvatar())
                .roleName(roles.isEmpty() ? "" : roles.get(0))
                .roles(roles)
                .permissions(permissions)
                .build();
        return Result.ok(loginVO);
    }

    @GetMapping("/info")
    public Result<LoginVO> info() {
        // 从 SecurityContext 获取当前用户信息
        org.springframework.security.core.Authentication auth =
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByUsername(username);
        if (user == null) {
            return Result.fail(401, "用户不存在");
        }
        List<String> roles = roleService.getRoleCodesByUserId(user.getId());
        List<String> permissions = menuService.getPermsByUserId(user.getId());

        LoginVO loginVO = LoginVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roleName(roles.isEmpty() ? "" : roles.get(0))
                .department(user.getDepartment())
                .avatar(user.getAvatar())
                .roles(roles)
                .permissions(permissions)
                .build();
        return Result.ok(loginVO);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.ok();
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody User user) {
        org.springframework.security.core.Authentication auth =
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User existingUser = userService.getByUsername(username);
        if (existingUser == null) {
            return Result.fail(401, "用户不存在");
        }
        existingUser.setRealName(user.getRealName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setDepartment(user.getDepartment());
        userService.updateById(existingUser);
        return Result.ok();
    }

    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        org.springframework.security.core.Authentication auth =
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByUsername(username);
        if (user == null) {
            return Result.fail(401, "用户不存在");
        }

        if (file.isEmpty()) {
            return Result.fail(400, "请选择要上传的图片");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.matches(".*\\.(jpg|jpeg|png|gif)$")) {
            return Result.fail(400, "只支持jpg、jpeg、png、gif格式的图片");
        }

        try {
            String uploadDir = "uploads/avatar";
            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String newFilename = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
            Path filePath = dirPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);

            String avatarUrl = "/api/auth/avatar/" + newFilename;
            user.setAvatar(avatarUrl);
            userService.updateById(user);

            return Result.ok(avatarUrl);
        } catch (IOException e) {
            return Result.fail(500, "上传失败");
        }
    }

    @GetMapping("/avatar/{filename}")
    public void getAvatar(@PathVariable String filename, jakarta.servlet.http.HttpServletResponse response) {
        try {
            Path filePath = Paths.get("uploads/avatar", filename);
            if (!Files.exists(filePath)) {
                response.setStatus(404);
                return;
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "image/jpeg";
            }
            response.setContentType(contentType);
            Files.copy(filePath, response.getOutputStream());
        } catch (IOException e) {
            response.setStatus(500);
        }
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody java.util.Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        org.springframework.security.core.Authentication auth =
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByUsername(username);
        if (user == null) {
            return Result.fail(401, "用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.fail(400, "原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateById(user);
        return Result.ok();
    }
}

package com.ism.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ism.common.entity.User;

import java.util.List;

public interface UserService extends IService<User> {
    User getByUsername(String username);
    Page<User> pageQuery(Integer pageNum, Integer pageSize, String keyword);
    void resetPassword(Long id, String encodedPassword);
    void assignRoles(Long userId, List<Long> roleIds);
    List<Long> getRoleIdsByUserId(Long userId);
}

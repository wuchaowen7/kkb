package com.ism.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ism.common.entity.User;
import com.ism.common.exception.BusinessException;
import com.ism.system.mapper.UserMapper;
import com.ism.system.mapper.UserRoleMapper;
import com.ism.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserRoleMapper userRoleMapper;

    @Override
    public User getByUsername(String username) {
        return this.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    public Page<User> pageQuery(Integer pageNum, Integer pageSize, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                             .or()
                             .like(User::getRealName, keyword)
                             .or()
                             .like(User::getPhone, keyword));
        }
        wrapper.orderByDesc(User::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public void resetPassword(Long id, String encodedPassword) {
        User user = new User();
        user.setId(id);
        user.setPassword(encodedPassword);
        this.updateById(user);
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.deleteByUserId(userId);
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                userRoleMapper.insertUserRole(userId, roleId);
            }
        }
    }

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        return baseMapper.getRoleIdsByUserId(userId);
    }
}

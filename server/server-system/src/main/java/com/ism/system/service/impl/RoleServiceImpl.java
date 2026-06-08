package com.ism.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ism.common.entity.Role;
import com.ism.system.mapper.RoleMapper;
import com.ism.system.mapper.RoleMenuMapper;
import com.ism.system.mapper.UserRoleMapper;
import com.ism.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final UserRoleMapper userRoleMapper;
    private final RoleMenuMapper roleMenuMapper;

    @Override
    public List<String> getRoleCodesByUserId(Long userId) {
        return baseMapper.getRoleCodesByUserId(userId);
    }

    @Override
    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        // 先删除原有菜单关联
        roleMenuMapper.deleteByRoleId(roleId);
        // 批量插入新关联
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                roleMenuMapper.insertRoleMenu(roleId, menuId);
            }
        }
    }
}

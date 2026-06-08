package com.ism.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ism.common.entity.Role;
import java.util.List;

public interface RoleService extends IService<Role> {
    List<String> getRoleCodesByUserId(Long userId);
    void assignMenus(Long roleId, List<Long> menuIds);
}

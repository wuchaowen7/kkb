package com.ism.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ism.common.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    @Select("SELECT r.role_code FROM sys_role r INNER JOIN sys_user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId} AND r.status = 1")
    List<String> getRoleCodesByUserId(Long userId);
}

package com.ism.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ism.common.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT role_id FROM sys_user_role WHERE user_id = #{userId}")
    List<Long> getRoleIdsByUserId(Long userId);
}

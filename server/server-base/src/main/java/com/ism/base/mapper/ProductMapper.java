package com.ism.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ism.common.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {}

package com.ism.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ism.common.entity.StockReturn;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReturnMapper extends BaseMapper<StockReturn> {
}
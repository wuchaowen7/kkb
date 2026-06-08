package com.ism.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ism.common.entity.TransferOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransferMapper extends BaseMapper<TransferOrder> {}

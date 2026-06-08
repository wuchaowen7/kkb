package com.ism.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ism.common.entity.Warehouse;
import com.ism.base.mapper.WarehouseMapper;
import com.ism.base.service.WarehouseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, Warehouse> implements WarehouseService {

    @Override
    public Page<Warehouse> pageQuery(Integer pageNum, Integer pageSize, String keyword) {
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Warehouse::getName, keyword).or().like(Warehouse::getAddress, keyword);
        }
        wrapper.orderByDesc(Warehouse::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }
}

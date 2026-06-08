package com.ism.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ism.common.entity.Supplier;
import com.ism.base.mapper.SupplierMapper;
import com.ism.base.service.SupplierService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {

    @Override
    public Page<Supplier> pageQuery(Integer pageNum, Integer pageSize, String keyword) {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Supplier::getName, keyword).or().like(Supplier::getContactName, keyword);
        }
        wrapper.orderByDesc(Supplier::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }
}

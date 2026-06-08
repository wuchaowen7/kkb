package com.ism.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ism.common.entity.Supplier;

public interface SupplierService extends IService<Supplier> {
    Page<Supplier> pageQuery(Integer pageNum, Integer pageSize, String keyword);
}

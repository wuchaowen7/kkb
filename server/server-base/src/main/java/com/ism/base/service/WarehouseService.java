package com.ism.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ism.common.entity.Warehouse;

public interface WarehouseService extends IService<Warehouse> {
    Page<Warehouse> pageQuery(Integer pageNum, Integer pageSize, String keyword);
}

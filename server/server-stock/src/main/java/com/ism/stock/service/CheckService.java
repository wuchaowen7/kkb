package com.ism.stock.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ism.common.entity.CheckOrder;

public interface CheckService extends IService<CheckOrder> {
    Page<CheckOrder> pageQuery(Integer pageNum, Integer pageSize);
    void createCheck(CheckOrder order);
    void confirmCheck(Long id);
}

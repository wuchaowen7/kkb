package com.ism.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ism.common.entity.Product;

public interface ProductService extends IService<Product> {
    Page<Product> pageQuery(Integer pageNum, Integer pageSize, String keyword, Long categoryId);
    void updateStatus(Long id, Integer status);
}

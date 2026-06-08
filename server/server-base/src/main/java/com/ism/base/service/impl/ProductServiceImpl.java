package com.ism.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ism.common.entity.Product;
import com.ism.base.mapper.ProductMapper;
import com.ism.base.service.ProductService;
import com.ism.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public Page<Product> pageQuery(Integer pageNum, Integer pageSize, String keyword, Long categoryId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Product::getName, keyword)
                .or().like(Product::getCode, keyword)
                .or().like(Product::getBarcode, keyword));
        }
        if (categoryId != null && categoryId > 0) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public boolean save(Product product) {
        // 检查商品编码是否已存在
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getCode, product.getCode());
        Product existing = this.getOne(wrapper);
        if (existing != null) {
            throw new BusinessException("商品编码已存在");
        }
        return super.save(product);
    }

    @Override
    @Transactional
    public boolean updateById(Product product) {
        // 检查商品编码是否与其他商品重复
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getCode, product.getCode());
        wrapper.ne(Product::getId, product.getId());
        Product existing = this.getOne(wrapper);
        if (existing != null) {
            throw new BusinessException("商品编码已存在");
        }
        return super.updateById(product);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Product product = new Product();
        product.setId(id);
        product.setStatus(status);
        this.updateById(product);
    }
}

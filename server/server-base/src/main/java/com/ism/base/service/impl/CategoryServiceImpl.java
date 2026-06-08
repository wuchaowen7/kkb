package com.ism.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ism.common.entity.Category;
import com.ism.base.mapper.CategoryMapper;
import com.ism.base.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> buildTree() {
        List<Category> all = this.list(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));
        Map<Long, List<Category>> childrenMap = all.stream()
                .filter(c -> c.getParentId() != 0)
                .collect(Collectors.groupingBy(Category::getParentId));
        return all.stream().filter(c -> c.getParentId() == 0).collect(Collectors.toList());
    }
}

package com.ism.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ism.common.entity.Category;
import java.util.List;

public interface CategoryService extends IService<Category> {
    List<Category> buildTree();
}

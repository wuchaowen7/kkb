package com.ism.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ism.common.entity.CheckOrder;
import com.ism.common.exception.BusinessException;
import com.ism.stock.mapper.CheckMapper;
import com.ism.stock.service.CheckService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class CheckServiceImpl extends ServiceImpl<CheckMapper, CheckOrder> implements CheckService {

    @Override
    public Page<CheckOrder> pageQuery(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<CheckOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(CheckOrder::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void createCheck(CheckOrder order) {
        order.setCheckNo("CK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4));
        order.setStatus("CHECKING");
        this.save(order);
    }

    @Override
    @Transactional
    public void confirmCheck(Long id) {
        CheckOrder order = this.getById(id);
        if (order == null || !"CHECKING".equals(order.getStatus())) {
            throw new BusinessException("盘点单状态不正确");
        }
        order.setStatus("CONFIRMED");
        this.updateById(order);
    }
}

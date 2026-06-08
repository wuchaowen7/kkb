package com.ism.stock.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ism.common.entity.TransferOrder;

public interface TransferService extends IService<TransferOrder> {
    Page<TransferOrder> pageQuery(Integer pageNum, Integer pageSize);
    void createTransfer(TransferOrder order);
    void confirmTransfer(Long id);
}

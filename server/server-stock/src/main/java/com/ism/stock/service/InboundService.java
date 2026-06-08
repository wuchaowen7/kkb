package com.ism.stock.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ism.common.entity.InboundOrder;
import com.ism.common.entity.InboundDetail;
import java.util.List;

public interface InboundService extends IService<InboundOrder> {
    Page<InboundOrder> pageQuery(Integer pageNum, Integer pageSize, String status);
    List<InboundDetail> getDetails(Long inboundId);
    void createInbound(InboundOrder order);
    void confirmInbound(Long id);
}

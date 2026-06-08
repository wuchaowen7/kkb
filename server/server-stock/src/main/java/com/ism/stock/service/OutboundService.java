package com.ism.stock.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ism.common.entity.OutboundOrder;
import com.ism.common.entity.OutboundDetail;
import java.util.List;

public interface OutboundService extends IService<OutboundOrder> {
    Page<OutboundOrder> pageQuery(Integer pageNum, Integer pageSize, String status);
    List<OutboundDetail> getDetails(Long outboundId);
    void createOutbound(OutboundOrder order);
    void auditOutbound(Long id, Boolean approved, String opinion);
    void updateOutbound(Long id, OutboundOrder order);
    void deleteOutbound(Long id);
}

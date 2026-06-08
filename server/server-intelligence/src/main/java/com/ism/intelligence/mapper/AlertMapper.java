package com.ism.intelligence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ism.common.entity.AlertRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AlertMapper extends BaseMapper<AlertRecord> {}

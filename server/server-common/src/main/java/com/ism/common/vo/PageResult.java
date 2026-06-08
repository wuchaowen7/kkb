package com.ism.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private List<T> list;

    public static <T> PageResult<T> of(Long total, List<T> list) {
        return new PageResult<>(total, 1, (int) Math.min(total, list.size()), list);
    }
}

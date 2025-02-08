package com.parking.model.dto.common;

import lombok.Data;
import java.util.List;

@Data
public class PageResponse<T> {
    private long total;
    private List<T> list;
    
    public PageResponse(long total, List<T> list) {
        this.total = total;
        this.list = list;
    }
} 
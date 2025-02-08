package com.parking.model.dto.owner;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class EarningsStatisticsResponse {
    private BigDecimal total;
    private List<EarningsItem> list;
    
    @Data
    public static class EarningsItem {
        private String date;
        private BigDecimal amount;
        private Integer orderCount;
    }
} 
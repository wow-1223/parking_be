package com.parking.model.dto.owner;

import lombok.Data;
import java.util.List;

@Data
public class UsageStatisticsResponse {
    private Double average;
    private List<UsageItem> list;
    
    @Data
    public static class UsageItem {
        private String date;
        private Double rate;
    }
} 
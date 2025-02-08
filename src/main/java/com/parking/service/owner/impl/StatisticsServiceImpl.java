package com.parking.service.impl;

import com.parking.model.dto.owner.EarningsStatisticsResponse;
import com.parking.model.dto.owner.UsageStatisticsResponse;
import com.parking.model.entity.User;
import com.parking.repository.OrderRepository;
import com.parking.service.StatisticsService;
import com.parking.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private SecurityUtil securityUtil;

    @Override
    public EarningsStatisticsResponse getEarningsStatistics(String timeRange, 
            String startDate, String endDate) {
        User currentUser = securityUtil.getCurrentUser();
        
        LocalDateTime start = parseStartDate(timeRange, startDate);
        LocalDateTime end = parseEndDate(timeRange, endDate);
        
        List<Object[]> statistics = orderRepository.getEarningsStatistics(
                currentUser.getId(), 
                start,
                end,
                timeRange
        );
        
        return buildEarningsResponse(statistics);
    }

    @Override
    public UsageStatisticsResponse getUsageStatistics(String parkingId, String timeRange, 
            String startDate, String endDate) {
        User currentUser = securityUtil.getCurrentUser();
        
        LocalDateTime start = parseStartDate(timeRange, startDate);
        LocalDateTime end = parseEndDate(timeRange, endDate);
        
        List<Object[]> statistics;
        if (parkingId != null) {
            statistics = orderRepository.getParkingUsageStatistics(
                    Long.parseLong(parkingId),
                    start,
                    end,
                    timeRange
            );
        } else {
            statistics = orderRepository.getOverallUsageStatistics(
                    currentUser.getId(),
                    start,
                    end,
                    timeRange
            );
        }
        
        return buildUsageResponse(statistics);
    }
    
    // ... 其他辅助方法实现
} 
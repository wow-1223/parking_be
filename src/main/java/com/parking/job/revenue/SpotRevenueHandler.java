package com.parking.job.revenue;

import com.parking.model.entity.mybatis.SpotRevenue;
import com.parking.repository.mybatis.SpotRevenueRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class SpotRevenueHandler {

    @Autowired
    private SpotRevenueRepository spotRevenueRepository;

    /**
     * 批量处理超时订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchProcessSpotRevenues(List<SpotRevenue> revenues) {
        if (CollectionUtils.isEmpty(revenues)) {
            return;
        }
        try {
            // 1. 批量插入收益记录
            spotRevenueRepository.batchInsert(revenues);
        } catch (Exception e) {
            log.error("Batch process spot revenue failed", e);
            throw e; // 触发事务回滚
        }
    }
}

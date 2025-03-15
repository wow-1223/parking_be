package com.parking.job.venue;

import com.google.common.collect.Lists;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.SpotRevenue;
import com.parking.repository.mybatis.OrderRepository;
import com.parking.util.DateUtil;
import com.parking.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.parking.enums.order.OrderStatusEnum.*;

@Slf4j
@Component
public class SpotRevenueJob {

    private static final BigDecimal SPOT_VENUE_RATE = BigDecimal.valueOf(0.60);

    @Autowired
    private SpotRevenueHandler spotRevenueHandler;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * 每天0点1min执行
     * 扫描昨日的订单，统计对应车位的订单金额
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void spotVenueJob() {
        log.info("Spot Venue Job started");
        // 1. 查询昨日已完成与已取消的订单，包括逾期后完成的订单
        List<Order> orders = orderRepository.findOrdersByDay(
                DateUtil.getYesterday(), Lists.newArrayList(COMPLETED.getStatus(), CANCELED.getStatus()));

        // 2. 统计对应车位的订单金额
        List<SpotRevenue> revenues = generateSpotVenue(orders);

        // 3. 批量插入收益记录
        try {
            spotRevenueHandler.batchProcessSpotRevenues(revenues);
        } catch (Exception e) {
            log.error("Batch process spot revenue failed, revenues: {}", JsonUtil.toJson(revenues), e);
        }

        log.info("Spot Venue Job finished");
    }

    /**
     * 统计对应车位的订单金额
     */
    private List<SpotRevenue> generateSpotVenue(List<Order> orders) {
        Map<Long, SpotRevenue> map = new HashMap<>();
        for (Order order : orders) {
            Long spotId = order.getParkingSpotId();
            SpotRevenue revenue = map.getOrDefault(spotId, new SpotRevenue());
            revenue.setId(order.getId());
            revenue.setParkingSpotId(order.getParkingSpotId());
            revenue.setOwnerId(order.getOwnerId());
            revenue.setParkingDay(DateUtil.getYesterday());
            revenue.setRevenue(calculateSpotVenue(
                    revenue.getRevenue(), order, SPOT_VENUE_RATE));
            revenue.setPlatformRevenue(calculateSpotVenue(
                    revenue.getPlatformRevenue(), order, BigDecimal.ONE.subtract(SPOT_VENUE_RATE)));
            map.put(spotId, revenue);
        }
        return map.values().stream().toList();
    }

    /**
     * 计算收益
     * 收益 = (订单金额 + 超时金额 - 退款金额) * 收益比例
     */
    private BigDecimal calculateSpotVenue(BigDecimal currentAmount, Order order, BigDecimal spotVenueRate) {
        if (currentAmount == null) {
            currentAmount = BigDecimal.ZERO;
        }
        return currentAmount
                .add(order.getAmount())
                .add(order.getTimeoutAmount())
                .subtract(order.getRefundAmount())
                .multiply(spotVenueRate);
    }
}

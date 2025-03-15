package com.parking.job.venue;

import com.parking.model.entity.mybatis.Order;
import com.parking.repository.mybatis.OrderRepository;
import com.parking.repository.mybatis.ParkingSpotRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SpotVenueJob {

    @Autowired
    private SpotVenueHandler spotVenueHandler;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    /**
     * 每天0点执行
     * 扫描昨日的订单，统计对应车位的订单金额
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void spotVenueJob() {
        log.info("Spot Venue Job started");
        // 1. 查询昨日的订单
//        List<Order> orders = orderRepository.findByStatusAndCreatedAtBetween(
//        OrderStatusEnum.COMPLETED.getStatus(), DateUtil.getYesterdayStart(), DateUtil.getYesterdayEnd());

        // 2. 统计对应车位的订单金额
        // spotVenueHandler.calculateSpotVenue(orders);

        log.info("Spot Venue Job finished");
    }
}

package com.parking.repository.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.mapper.OrderMapper;
import com.parking.model.entity.mybatis.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepository {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 根据订单ID查找订单
     */
    public Order findById(Long id) {
        return orderMapper.selectById(id);
    }

    /**
     * 根据用户ID查找订单
     */
    public List<Order> findAll() {
        return orderMapper.selectList(null);
    }

    /**
     * 根据用户ID查找订单
     */
    public List<Order> findByUserId(Long userId) {
        return orderMapper.selectList(new QueryWrapper<Order>().eq("user_id", userId));
    }

    /**
     * 根据用户ID统计订单数量
     */
    public Long countByUserId(Long userId) {
        return orderMapper.selectCount(new QueryWrapper<Order>().eq("user_id", userId));
    }

    /**
     * 根据用户ID分页查找订单
     */
    public List<Order> findByUserId(Long userId, int pageNum, int pageSize) {
        return orderMapper.selectPage(new Page<>(pageNum, pageSize), new QueryWrapper<Order>().eq("user_id", userId)).getRecords();
    }

    /**
     * 根据停车场ID查找订单
     */
    public List<Order> findByParkingSpotId(Long parkingSpotId) {
        return orderMapper.selectList(new QueryWrapper<Order>().eq("parking_spot_id", parkingSpotId));
    }

    /**
     * 根据停车时段ID查找订单
     */
    public List<Order> findByParkingPeriodId(Long parkingPeriodId) {
        return orderMapper.selectList(new QueryWrapper<Order>().eq("parking_period_id", parkingPeriodId));
    }

    /**
     * 根据订单状态查找订单
     */
    public List<Order> findByStatus(String status) {
        return orderMapper.selectList(new QueryWrapper<Order>().eq("status", status));
    }

    /**
     * 根据支付状态查找订单
     */
    public List<Order> findByPaymentStatus(String paymentStatus) {
        return orderMapper.selectList(new QueryWrapper<Order>().eq("payment_status", paymentStatus));
    }

    /**
     * 根据支付方式查找订单
     */
    public List<Order> findByPaymentMethod(String paymentMethod) {
        return orderMapper.selectList(new QueryWrapper<Order>().eq("payment_method", paymentMethod));
    }

    /**
     * 根据支付时间查找订单
     */
    public List<Order> findByPaymentTime(String paymentTime) {
        return orderMapper.selectList(new QueryWrapper<Order>().eq("payment_time", paymentTime));
    }

}

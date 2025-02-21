package com.parking.repository.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.parking.mapper.mybatis.OrderMapper;
import com.parking.model.entity.mybatis.Order;
import com.parking.util.tool.DateUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class OrderRepository {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 新增订单
     */
    public void insert(Order order) {
        orderMapper.insert(order);
    }

    /**
     * 更新订单
     */
    public void update(Order order) {
        orderMapper.updateById(order);
    }

    /**
     * 删除订单
     */
    public void delete(Long id) {
        Order order = findById(id, Lists.newArrayList("id"));
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        order.setDeletedAt(DateUtil.getCurrentTimestamp());
        update(order);
    }


    /**
     * 根据订单ID查找订单
     */
    public Order findById(Long id) {
        return orderMapper.selectOne(new QueryWrapper<Order>(){}.eq("id", id).eq("deleted_at", 0L));
    }

    /**
     * 根据订单ID查找订单
     */
    public Order findById(Long id, List<String> fields) {
        return orderMapper.selectOne(new QueryWrapper<Order>(){}.eq("id", id).eq("deleted_at", 0L).select(fields));
    }

    /**
     * 根据订单ID与user查找订单
     */
    public Order findByIdAndUserId(Long id, Long userId) {
        return orderMapper.selectOne(new QueryWrapper<Order>().eq("id", id).eq("user_id", userId).eq("deleted_at", 0L));

    }

    /**
     * 根据用户ID分页查找订单
     */
    public IPage<Order> findByUserAndStatus(Long userId, Integer status, int page, int size) {
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(status);
        return findByOrder(order, null, page, size);
    }

    public IPage<Order> findByOwnerAndStatus(Long owner, Integer status, int page, int size) {
        Order order = new Order();
        order.setOwnerId(owner);
        order.setStatus(status);
        return findByOrder(order, null, page, size);
    }

    public IPage<Order> findByOrder(Order order, List<String> selectFields, int page, int size) {
        QueryWrapper<Order> query = new QueryWrapper<>();
        if (order.getId() != null) {
            query.eq("id", order.getId());
        }
        if (order.getUserId() != null) {
            query.eq("user_id", order.getUserId());
        }
        if (order.getOwnerId()!= null) {
            query.eq("owner_id", order.getOwnerId());
        }
        if (order.getParkingSpotId()!= null) {
            query.eq("parking_spot_id", order.getParkingSpotId());
        }
        if (order.getParkingOccupiedId()!= null) {
            query.eq("parking_occupied_id", order.getParkingOccupiedId());
        }
        if (order.getCarNumber()!= null) {
            query.eq("car_number", order.getCarNumber());
        }
        if (order.getAmount()!= null) {
            query.eq("amount", order.getAmount());
        }
        if (order.getRefundAmount()!= null) {
            query.eq("refund_amount", order.getRefundAmount());
        }
        if (order.getTransactionId()!= null) {
            query.eq("transaction_id", order.getTransactionId());
        }
        if (order.getStatus() != null) {
            query.eq("status", order.getStatus());
        }
        query.eq("deleted_at", 0L);
        query.orderByDesc("update_time");

        if (CollectionUtils.isNotEmpty(selectFields)) {
            query.select(selectFields);
        }

        return orderMapper.selectPage(new Page<>(page, size), query);
    }

    /**
     * 获取收益统计数据
     */
    public List<Object[]> getEarningsStatistics(Long ownerId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderMapper.selectEarningsStatistics(ownerId, startDate, endDate);
    }

    /**
     * 获取单个停车位使用统计
     */
    public List<Object[]> getParkingUsageStatistics(Long ownerId, Long parkingSpotId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderMapper.selectParkingUsageStatistics(ownerId, parkingSpotId, startDate, endDate);
    }

    /**
     * 获取所有停车位整体使用统计
     */
    public List<Object[]> getOverallUsageStatistics(Long ownerId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderMapper.selectOverallUsageStatistics(ownerId, startDate, endDate);
    }

}

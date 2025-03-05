package com.parking.repository.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.parking.mapper.mybatis.OrderMapper;
import com.parking.model.dto.join.OrderUserDTO;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.param.owner.response.StatisticsResponse;
import com.parking.util.DateUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
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

    public void batchUpdate(List<Order> orders) {
        orderMapper.updateById(orders);
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

    public Boolean exist(Long id) {
        return orderMapper.selectCount(new QueryWrapper<Order>().eq("id", id).eq("deleted_at", 0L)) > 0;
    }

    /**
     * 根据订单ID查找订单
     */
    public Order findById(Long id, List<String> fields) {
        return orderMapper.selectOne(new QueryWrapper<Order>()
                .eq("id", id).eq("deleted_at", 0L).select(fields));
    }

    /**
     * 根据订单ID与user查找订单
     */
    public Order findByIdAndUserId(Long id, Long userId) {
        return orderMapper.selectOne(new QueryWrapper<Order>()
                .eq("id", id).eq("user_id", userId).eq("deleted_at", 0L));
    }

    /**
     * 根据订单状态查找
     */
    public List<Order> findByStatus(Integer status) {
        return orderMapper.selectList(new QueryWrapper<Order>().eq("status", status).eq("deleted_at", 0L));
    }


    /**
     * 根据用户ID分页查找订单
     */
    public IPage<Order> findByUserAndStatus(Long userId, Integer status, int page, int size) {
        QueryWrapper<Order> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        if (status != null) {
            query.eq("status", status);
        }
        query.eq("deleted_at", 0L);
        query.orderByDesc("update_time");
        return orderMapper.selectPage(new Page<>(page, size), query);
    }

    /**
     * 根据租户ID分页查找订单
     */
    public IPage<Order> findByOwnerAndStatus(Long owner, Integer status, int page, int size) {
        QueryWrapper<Order> query = new QueryWrapper<>();
        query.eq("owner_id", owner);
        if (status != null) {
            query.eq("status", status);
        }
        query.eq("deleted_at", 0L);
        query.orderByDesc("update_time");
        return orderMapper.selectPage(new Page<>(page, size), query);
    }


    // 统计相关

    /**
     * 获取收益统计数据
     */
    public List<StatisticsResponse> getEarningsStatistics(Long ownerId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderMapper.selectEarningsStatistics(ownerId, startDate, endDate);
    }

    /**
     * 获取单个停车位使用统计
     */
    public List<StatisticsResponse> getParkingUsageStatistics(Long ownerId, Long parkingSpotId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderMapper.selectParkingUsageStatistics(ownerId, parkingSpotId, startDate, endDate);
    }

    /**
     * 获取所有停车位整体使用统计
     */
    public List<StatisticsResponse> getOverallUsageStatistics(Long ownerId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderMapper.selectOverallUsageStatistics(ownerId, startDate, endDate);
    }

    // end 统计相关

    // 联表查询
    public List<OrderUserDTO> findOrderWithUserByOccupied(List<Long> occupiedIds, Integer status) {
        return orderMapper.selectOrderWithUserByOccupied(StringUtils.join(occupiedIds), status);
    }

}

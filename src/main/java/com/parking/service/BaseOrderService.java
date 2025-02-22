package com.parking.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.parking.enums.order.OrderStatusEnum;
import com.parking.exception.BusinessException;
import com.parking.exception.ResourceNotFoundException;
import com.parking.model.dto.order.OrderDTO;
import com.parking.model.dto.order.OrderDetailDTO;
import com.parking.model.dto.user.UserDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.entity.mybatis.User;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.repository.mybatis.OccupiedSpotRepository;
import com.parking.repository.mybatis.OrderRepository;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.repository.mybatis.UserRepository;
import com.parking.handler.encrypt.AesUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseOrderService implements OrderService {

    @Autowired
    protected OrderRepository orderRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ParkingSpotRepository parkingSpotRepository;

    @Autowired
    protected OccupiedSpotRepository occupiedSpotRepository;

    @Autowired
    private AesUtil aesUtil;

    public PageResponse<OrderDTO> convertOrderPage(IPage<Order> page) {
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return new PageResponse<>(0L, Collections.emptyList());
        }
        List<Order> records = page.getRecords();
        List<Long> parkingSpotIds = records.stream().map(Order::getParkingSpotId).toList();
        List<Long> occupiedSpotIds = records.stream().map(Order::getParkingOccupiedId).toList();
        // 查询停车位信息
        List<ParkingSpot> parkingSpots = parkingSpotRepository.findAll(
                parkingSpotIds, Lists.newArrayList("id", "owner_id", "longitude", "latitude", "location"));
        if (CollectionUtils.isEmpty(parkingSpots)) {
            throw new ResourceNotFoundException("ParkingSpot not found");
        }

        // 查询占用信息
        List<OccupiedSpot> occupiedSpots = occupiedSpotRepository.findAll(
                occupiedSpotIds, Lists.newArrayList("id", "parking_day", "start_time", "end_time"));

        Map<Long, ParkingSpot> parkingSpotMap = parkingSpots.stream()
                .collect(Collectors.toMap(ParkingSpot::getId, parkingSpot -> parkingSpot));
        Map<Long, OccupiedSpot> occupiedSpotMap = occupiedSpots.stream()
                .collect(Collectors.toMap(OccupiedSpot::getId, occupiedSpot -> occupiedSpot));

        List<OrderDTO> orders = Lists.newArrayListWithCapacity(records.size());
        for (Order order : records) {
            ParkingSpot parkingSpot = parkingSpotMap.get(order.getParkingSpotId());
            if (parkingSpot == null) {
                throw new ResourceNotFoundException("ParkingSpot not found");
            }
            OccupiedSpot occupiedSpot = occupiedSpotMap.get(order.getParkingOccupiedId());
            OrderDTO dto = convertToDTO(order, parkingSpot, occupiedSpot);
            orders.add(dto);
        }


        // 构建分页响应
        return PageResponse.pageSuccess(orders, page.getTotal());
    }

    public DetailResponse<OrderDetailDTO> convertOrderDetail(Order order) {
        OrderDetailDTO dto = new OrderDetailDTO();
        // 设置订单基本信息
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setParkingSpotId(order.getParkingSpotId());
        dto.setOwnerId(order.getOwnerId());
        dto.setCarNumber(order.getCarNumber());
        dto.setAmount(order.getAmount());
        dto.setRefundAmount(order.getRefundAmount());
        dto.setTransactionId(order.getTransactionId());
        dto.setStatus(order.getStatus());

        // 设置租户信息
        User owr = userRepository.findById(order.getOwnerId(), Lists.newArrayList("id", "nick_name", "phone"));
        if (owr == null) {
            throw new ResourceNotFoundException("Owner not found");
        }
        UserDTO owner = new UserDTO();
        owner.setId(owr.getId());
        owner.setName(owr.getNickName());
        owner.setPhone(owr.getPhone());
        dto.setOwner(owner);


        // 设置停车位信息
        ParkingSpot parkingSpot = parkingSpotRepository.findById(order.getParkingSpotId(),
                Lists.newArrayList("id", "owner_id", "longitude", "latitude", "location"));
        if (parkingSpot == null) {
            throw new ResourceNotFoundException("ParkingSpot not found");
        }
        dto.setParkingSpotId(parkingSpot.getId());
        dto.setOwnerId(parkingSpot.getOwnerId());
        dto.setLongitude(parkingSpot.getLongitude().doubleValue());
        dto.setLatitude(parkingSpot.getLatitude().doubleValue());
        dto.setLocation(parkingSpot.getLocation());

        // 设置占用信息
        OccupiedSpot occupiedSpot = occupiedSpotRepository.findById(order.getParkingOccupiedId(),
                Lists.newArrayList("id", "parking_day", "start_time", "end_time"));
        if (occupiedSpot == null) {
            throw new ResourceNotFoundException("OccupiedSpot not found");
        }
        dto.setStartTime(occupiedSpot.getStartTime());
        dto.setEndTime(occupiedSpot.getEndTime());

        return DetailResponse.detailSuccess(dto, "get order detail success");
    }

    /**
     * 将订单实体转换为DTO
     */
    public OrderDTO convertToDTO(Order order, ParkingSpot parkingSpot, OccupiedSpot occupiedSpot) {

        OrderDTO dto = new OrderDTO();

        // 设置订单基本信息
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setCarNumber(aesUtil.decrypt(order.getCarNumber()));
        dto.setAmount(order.getAmount());
        dto.setRefundAmount(order.getRefundAmount());
        dto.setTransactionId(order.getTransactionId());
        dto.setStatus(order.getStatus());

        // 设置停车位信息
        dto.setParkingSpotId(parkingSpot.getId());
        dto.setOwnerId(parkingSpot.getOwnerId());
        dto.setLocation(parkingSpot.getLocation());
        dto.setLongitude(parkingSpot.getLongitude().doubleValue());
        dto.setLatitude(parkingSpot.getLatitude().doubleValue());

        // 设置占用信息
        if (occupiedSpot != null) {
            dto.setOccupiedSpotId(occupiedSpot.getId());
            dto.setStartTime(occupiedSpot.getStartTime());
            dto.setEndTime(occupiedSpot.getEndTime());
        }

        return dto;
    }

    /**
     * 计算订单金额
     * @param price 每小时价格
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单金额
     */
    public BigDecimal calculateAmount(BigDecimal price, LocalDateTime startTime, LocalDateTime endTime) {
        // 1. 验证时间
        if (startTime.isAfter(endTime)) {
            throw new BusinessException("endTime must be after startTime");
        }

        // 2. 计算时长（小时）
        long hours = Duration.between(startTime, endTime).toHours();
        if (hours < 1) {
            hours = 1; // 不足1小时按1小时计算
        }

        // 3. 计算金额
        return price.multiply(BigDecimal.valueOf(hours))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算退款金额
     * @param order 订单
     * @return 退款金额
     */
    public BigDecimal calculateRefundAmount(Order order, LocalDateTime startTime) {
        LocalDateTime now = LocalDateTime.now();

        // 1. 如果订单未支付，全额退款
        if (OrderStatusEnum.PENDING_PAYMENT.getStatus() == order.getStatus()) {
            return order.getAmount();
        }

        // 2. 如果距离开始时间不足2小时，不予退款
        if (Duration.between(now, startTime).toHours() < 2) {
            return BigDecimal.ZERO;
        }

        // 3. 根据距离开始时间计算退款比例
        BigDecimal refundRate;
        long hoursBeforeStart = Duration.between(now, startTime).toHours();

        if (hoursBeforeStart >= 24) {
            // 提前24小时以上取消，全额退款
            refundRate = BigDecimal.ONE;
        } else if (hoursBeforeStart >= 12) {
            // 提前12-24小时取消，退款90%
            refundRate = new BigDecimal("0.90");
        } else if (hoursBeforeStart >= 6) {
            // 提前6-12小时取消，退款70%
            refundRate = new BigDecimal("0.70");
        } else {
            // 提前2-6小时取消，退款50%
            refundRate = new BigDecimal("0.50");
        }

        // 4. 计算退款金额
        return order.getAmount()
                .multiply(refundRate)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
